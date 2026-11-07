package ua.cinema;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.model.Screening;
import ua.cinema.service.EntityNotFoundException;
import ua.cinema.service.EntityPrinter;
import ua.cinema.service.MovieService;
import ua.cinema.service.ScreeningService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Клієнтський код. Знає лише інтерфейси MovieService і ScreeningService —
 * працює без змін і зі сховищем у пам'яті, і з Hibernate.
 * Наприкінці прибирає за собою все створене.
 */
public final class ServiceDemo {

    private ServiceDemo() {
    }

    public static void run(MovieService movies, ScreeningService screenings) {
        System.out.println("--- 1. Створення ---");
        Movie dune2021 = movies.save(new Movie("Дюна", 2021, 155, Genre.SCI_FI));
        Movie avatar = movies.save(new Movie("Аватар", 2009, 162, Genre.SCI_FI));
        Movie dune1984 = movies.save(new Movie("Дюна", 1984, 137, Genre.SCI_FI));
        Movie hedgehog = movies.save(new Movie("Їжачок у тумані", 1975, 10, Genre.ANIMATION));
        Movie lion2019 = movies.save(new Movie("Король Лев", 2019, 118, Genre.ANIMATION));
        Movie lion1994 = movies.save(new Movie("Король Лев", 1994, 88, Genre.ANIMATION));
        Movie temp = movies.save(new Movie("Тимчасовий", 2020, 90, Genre.COMEDY));
        EntityPrinter.printAll(movies.findAll());

        System.out.println("--- 2. Читання й оновлення ---");
        int before = movies.findAll().size();
        Movie loaded = movies.findById(avatar.getId());
        loaded.setDurationMinutes(192);                  // режисерська версія
        movies.save(loaded);
        System.out.println("  " + movies.findById(avatar.getId()));
        System.out.println("  записів до: " + before + ", після: " + movies.findAll().size());

        System.out.println("--- 3. Видалення ---");
        long tempId = temp.getId();
        System.out.println("  delete(" + tempId + ") -> " + movies.delete(tempId));
        System.out.println("  delete(" + tempId + ") ще раз -> " + movies.delete(tempId));

        System.out.println("--- 4. Пошук неіснуючого id ---");
        try {
            movies.findById(tempId);
        } catch (EntityNotFoundException e) {
            System.out.println("  Помилка: " + e.getMessage());
        }

        System.out.println("--- 5. Сортування і пошук ---");
        System.out.println("  findAllSortedByName():");
        EntityPrinter.printAll(movies.findAllSortedByName());
        System.out.println("  findAllSortedByNameAndByReleaseYear():");
        EntityPrinter.printAll(movies.findAllSortedByNameAndByReleaseYear());
        System.out.println("  findByGenre(ANIMATION):");
        EntityPrinter.printAll(movies.findByGenre(Genre.ANIMATION));

        System.out.println("--- 6. Сеанси (друга сутність, зв'язок з фільмом) ---");
        LocalDateTime day = LocalDateTime.of(2026, 11, 7, 0, 0);
        Screening s1 = screenings.save(dune2021.addScreening(new Screening("Вечірній", 3, day.withHour(19), new BigDecimal("180.00"))));
        Screening s2 = screenings.save(avatar.addScreening(new Screening("Ранковий", 1, day.withHour(10), new BigDecimal("110.00"))));
        Screening s3 = screenings.save(lion2019.addScreening(new Screening("Вечірній", 2, day.withHour(18), new BigDecimal("160.00"))));
        Screening s4 = screenings.save(dune2021.addScreening(new Screening("Денний", 3, day.withHour(14), new BigDecimal("140.00"))));
        System.out.println("  findAllSortedByNameAndByStartTime():");
        EntityPrinter.printAll(screenings.findAllSortedByNameAndByStartTime());
        System.out.println("  сеанси фільму «Дюна» (2021):");
        EntityPrinter.printAll(screenings.findByMovieId(dune2021.getId()));

        System.out.println("--- 7. Прибирання ---");
        for (Screening s : List.of(s1, s2, s3, s4)) {
            s.getMovie().removeScreening(s);
            screenings.delete(s.getId());
        }
        for (Movie m : List.of(dune2021, avatar, dune1984, hedgehog, lion2019, lion1994)) {
            movies.delete(m.getId());
        }
        System.out.println("  залишилось фільмів: " + movies.findAll().size()
                + ", сеансів: " + screenings.findAll().size());
    }
}
