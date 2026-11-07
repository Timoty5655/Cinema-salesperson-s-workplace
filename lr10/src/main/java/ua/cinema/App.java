package ua.cinema;

import ua.cinema.jdbc.DBConnection;
import ua.cinema.jdbc.DataAccessException;
import ua.cinema.jdbc.DbInitializer;
import ua.cinema.jdbc.JdbcMovieService;
import ua.cinema.jdbc.JdbcScreeningService;
import ua.cinema.jdbc.ScheduleService;
import ua.cinema.memory.InMemoryMovieService;
import ua.cinema.memory.InMemoryScreeningService;
import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.model.Screening;
import ua.cinema.service.EntityPrinter;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Запуск:  mvn compile exec:java                  — демо на обох реалізаціях + JDBC-можливості
 *          mvn exec:java -Dexec.args="reset"      — спершу очистити БД
 */
public class App {

    public static void main(String[] args) {
        DataSource ds = DBConnection.getInstance().getDataSource();
        if (args.length > 0 && args[0].equals("reset")) {
            DbInitializer.reset(ds);
        } else {
            DbInitializer.run(ds);   // init.sql при кожному старті — ідемпотентно
        }

        System.out.println("\n=========== ServiceDemo: реалізація В ПАМ'ЯТІ (ЛР №7) ===========");
        ServiceDemo.run(new InMemoryMovieService(), new InMemoryScreeningService());

        JdbcMovieService movies = new JdbcMovieService(ds);
        JdbcScreeningService screenings = new JdbcScreeningService(ds);
        System.out.println("\n=========== ServiceDemo: реалізація JDBC (той самий код) ===========");
        ServiceDemo.run(movies, screenings);

        System.out.println("\n=========== Можливості, специфічні для JDBC ===========");
        jdbcFeatures(ds, movies, screenings);

        DBConnection.getInstance().shutdown();
    }

    private static void jdbcFeatures(DataSource ds, JdbcMovieService movies, JdbcScreeningService screenings) {
        ScheduleService schedule = new ScheduleService(ds, movies, screenings);
        LocalDateTime day = LocalDateTime.of(2026, 11, 8, 0, 0);

        System.out.println("--- A. Транзакція: фільм + сеанси, COMMIT ---");
        Movie oppenheimer = new Movie("Оппенгеймер", 2023, 180, Genre.DRAMA);
        List<Screening> ok = List.of(
                new Screening("Денний", 0, 4, day.withHour(13), new BigDecimal("150.00")),
                new Screening("Вечірній", 0, 4, day.withHour(20), new BigDecimal("190.00")));
        schedule.addMovieWithScreenings(oppenheimer, ok);
        EntityPrinter.printAll(screenings.findByMovieId(oppenheimer.getId()));

        System.out.println("--- B. Транзакція з відкатом: другий сеанс у той самий зал і час ---");
        long moviesBefore = movies.count();
        Movie barbie = new Movie("Барбі", 2023, 114, Genre.COMEDY);
        List<Screening> conflicting = List.of(
                new Screening("Ранковий", 0, 5, day.withHour(10), new BigDecimal("120.00")),
                new Screening("Вечірній", 0, 4, day.withHour(20), new BigDecimal("190.00"))); // зайнято!
        try {
            schedule.addMovieWithScreenings(barbie, conflicting);
        } catch (DataAccessException e) {
            System.out.println("  " + e.getMessage());
        }
        System.out.println("  фільмів до: " + moviesBefore + ", після: " + movies.count()
                + ", «Барбі» в БД: " + movies.findByName("Барбі").size() + ", id об'єкта: " + barbie.getId());

        System.out.println("--- C. ON DELETE RESTRICT: фільм із сеансами видалити не можна ---");
        try {
            movies.delete(oppenheimer.getId());
        } catch (DataAccessException e) {
            System.out.println("  " + e.getMessage());
        }

        System.out.println("--- D. SQL-ін'єкція проти PreparedStatement ---");
        String attack = "' OR '1'='1";
        System.out.println("  findByName(\"" + attack + "\") -> знайдено " + movies.findByName(attack).size()
                + " записів (рядок трактується як значення, а не як SQL)");

        System.out.println("--- E. Пагінація (LIMIT/OFFSET) ---");
        List<Movie> page = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            page.add(new Movie("Сторінковий фільм " + i, 2000 + i, 100, Genre.DRAMA));
        }
        movies.saveAll(page);
        for (int p = 0; p < 3; p++) {
            System.out.println("  сторінка " + p + " (по 2):");
            EntityPrinter.printAll(movies.findPage(p, 2));
        }

        System.out.println("--- F. Пакетна вставка vs по одному ---");
        benchmark(movies, 2000);

        System.out.println("--- Прибирання ---");
        screenings.findByMovieId(oppenheimer.getId()).forEach(s -> screenings.delete(s.getId()));
        movies.delete(oppenheimer.getId());
        movies.deleteAll(page.stream().map(Movie::getId).toList());
        System.out.println("  фільмів у БД: " + movies.count() + ", сеансів: " + screenings.count());
    }

    private static void benchmark(JdbcMovieService movies, int n) {
        List<Movie> single = generate("Одиночний", n);
        long t0 = System.nanoTime();
        for (Movie m : single) {
            movies.save(m);          // окремий INSERT і окремий commit (autocommit) на кожен рядок
        }
        long singleMs = (System.nanoTime() - t0) / 1_000_000;

        List<Movie> batch = generate("Пакетний", n);
        long t1 = System.nanoTime();
        movies.saveAll(batch);       // addBatch * n + один executeBatch + один commit
        long batchMs = (System.nanoTime() - t1) / 1_000_000;

        System.out.printf("  %d записів: по одному %d мс, пакетом %d мс (у %.1f раза швидше)%n",
                n, singleMs, batchMs, (double) singleMs / Math.max(batchMs, 1));
        movies.deleteAll(single.stream().map(Movie::getId).toList());
        movies.deleteAll(batch.stream().map(Movie::getId).toList());
    }

    private static List<Movie> generate(String prefix, int n) {
        List<Movie> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(new Movie(prefix + " " + i, 1900 + i % 200, 90, Genre.ACTION));
        }
        return list;
    }
}
