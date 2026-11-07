package ua.cinema;

import ua.cinema.hibernate.HibernateMovieService;
import ua.cinema.hibernate.HibernateProblemsDemo;
import ua.cinema.hibernate.HibernateScreeningService;
import ua.cinema.hibernate.JpaUtil;
import ua.cinema.jdbc.DBConnection;
import ua.cinema.jdbc.DbInitializer;
import ua.cinema.memory.InMemoryMovieService;
import ua.cinema.memory.InMemoryScreeningService;
import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.model.Screening;
import ua.cinema.service.EntityPrinter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Запуск:  mvnw compile exec:java                  — демо
 *          mvnw exec:java -Dexec.args="reset"      — спершу видалити всі таблиці (порожня БД)
 */
public class App {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("reset")) {
            DbInitializer.reset(DBConnection.getInstance().getDataSource());
        }

        System.out.println("\n=========== ServiceDemo: реалізація В ПАМ'ЯТІ (ЛР №7) ===========");
        ServiceDemo.run(new InMemoryMovieService(), new InMemoryScreeningService());

        JpaUtil jpa = JpaUtil.getInstance();   // init.sql + EntityManagerFactory (validate)
        HibernateMovieService movies = new HibernateMovieService(jpa);
        HibernateScreeningService screenings = new HibernateScreeningService(jpa);

        System.out.println("\n=========== ServiceDemo: реалізація HIBERNATE (той самий код) ===========");
        ServiceDemo.run(movies, screenings);

        System.out.println("\n=========== Можливості й проблеми Hibernate ===========");
        hibernateFeatures(jpa, movies, screenings);

        System.out.println("\n=========== hbm2ddl.auto=validate: сутність не збігається з таблицею ===========");
        HibernateProblemsDemo.schemaValidation();

        jpa.shutdown();
    }

    private static void hibernateFeatures(JpaUtil jpa, HibernateMovieService movies,
                                          HibernateScreeningService screenings) {
        LocalDateTime day = LocalDateTime.of(2026, 11, 8, 0, 0);
        List<Movie> created = new ArrayList<>();
        created.add(movies.save(new Movie("Оппенгеймер", 2023, 180, Genre.DRAMA)));
        created.add(movies.save(new Movie("Барбі", 2023, 114, Genre.COMEDY)));
        created.add(movies.save(new Movie("Дюна", 2021, 155, Genre.SCI_FI)));
        created.add(movies.save(new Movie("Дюна", 1984, 137, Genre.SCI_FI)));
        Movie oppenheimer = created.get(0);
        Movie barbie = created.get(1);

        System.out.println("\n--- A. addScreening + cascade PERSIST (без em.persist для сеансу) ---");
        movies.addScreening(oppenheimer.getId(), new Screening("Денний", 4, day.withHour(13), new BigDecimal("150.00")));
        movies.addScreening(oppenheimer.getId(), new Screening("Вечірній", 4, day.withHour(20), new BigDecimal("190.00")));
        movies.addScreening(barbie.getId(), new Screening("Ранковий", 5, day.withHour(10), new BigDecimal("120.00")));
        movies.addScreening(created.get(2).getId(), new Screening("Нічний", 1, day.withHour(23), new BigDecimal("130.00")));
        EntityPrinter.printAll(screenings.findAll());

        System.out.println("\n--- B. Пагінація (setFirstResult / setMaxResults) ---");
        for (int p = 0; p < 2; p++) {
            System.out.println("  сторінка " + p + " (по 3):");
            EntityPrinter.printAll(movies.findPage(p, 3));
        }

        System.out.println("\n--- C. Пошук за полем з іменованим параметром ---");
        EntityPrinter.printAll(movies.findByGenre(Genre.SCI_FI));
        System.out.println("  findByName(\"' OR '1'='1\") -> " + movies.findByName("' OR '1'='1").size() + " записів");

        System.out.println("\n--- D. LazyInitializationException ---");
        HibernateProblemsDemo.lazyInitialization(movies, oppenheimer.getId());

        System.out.println("\n--- E. Проблема N+1 ---");
        HibernateProblemsDemo.nPlusOne(jpa, movies);

        System.out.println("\n--- F. Фільм із сеансами видалити не можна ---");
        try {
            movies.delete(oppenheimer.getId());
        } catch (IllegalStateException e) {
            System.out.println("  " + e.getMessage());
        }

        System.out.println("\n--- G. removeScreening + orphanRemoval -> DELETE ---");
        for (Movie m : created) {
            for (Screening s : screenings.findByMovieId(m.getId())) {
                movies.removeScreening(m.getId(), s.getId());
            }
            movies.delete(m.getId());
        }
        System.out.println("  фільмів у БД: " + movies.count() + ", сеансів: " + screenings.count());
    }
}
