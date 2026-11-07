import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

void main() {

    // ========== Рівень 1: CRUD для обох сутностей одним узагальненим класом ==========
    System.out.println("=== 1. Додавання фільмів ===");
    MovieService movies = new InMemoryMovieService();   // працюємо через інтерфейс
    movies.save(new Movie(1, "Дюна", 2021, 155, Genre.SCI_FI));
    movies.save(new Movie(2, "Аватар", 2009, 162, Genre.SCI_FI));
    movies.save(new Movie(3, "Дюна", 1984, 137, Genre.SCI_FI));       // та сама назва, інший рік
    movies.save(new Movie(4, "Їжачок у тумані", 1975, 10, Genre.ANIMATION));
    movies.save(new Movie(5, "Король Лев", 2019, 118, Genre.ANIMATION));
    movies.save(new Movie(6, "Король Лев", 1994, 88, Genre.ANIMATION));
    movies.save(new Movie(7, "Інтерстеллар", 2014, 169, Genre.SCI_FI));
    EntityPrinter.printAll(movies.findAll());

    System.out.println("\n=== 2. Оновлення наявного фільму (id=2) ===");
    int before = movies.findAll().size();
    movies.save(new Movie(2, "Аватар", 2009, 192, Genre.SCI_FI)); // режисерська версія, той самий id
    System.out.println("  записів до: " + before + ", після: " + movies.findAll().size());
    System.out.println("  " + movies.findById(2));

    System.out.println("\n=== 3. Видалення ===");
    System.out.println("  delete(7)  -> " + movies.delete(7));
    System.out.println("  delete(99) -> " + movies.delete(99) + "   (такого id немає)");

    System.out.println("\n=== 4. Пошук неіснуючого id ===");
    try {
        movies.findById(42);
    } catch (EntityNotFoundException e) {
        System.out.println("  Помилка: " + e.getMessage() + " (id у винятку = " + e.getId() + ")");
    }

    System.out.println("\n=== 5. Той самий InMemoryCrudService для другої сутності ===");
    CRUDService<Screening> screenings = new InMemoryCrudService<>("Screening");
    LocalDateTime day = LocalDateTime.of(2026, 11, 7, 0, 0);
    screenings.save(new Screening(1, "Вечірній", 1, 3, day.withHour(19), new BigDecimal("180.00")));
    screenings.save(new Screening(2, "Ранковий", 2, 1, day.withHour(10), new BigDecimal("110.00")));
    screenings.save(new Screening(3, "Вечірній", 5, 2, day.withHour(18), new BigDecimal("160.00")));
    screenings.save(new Screening(4, "Денний", 1, 3, day.withHour(14), new BigDecimal("140.00")));
    EntityPrinter.printAll(screenings.findAll());
    try {
        screenings.save(null);
    } catch (NullPointerException e) {
        System.out.println("  save(null): " + e.getMessage());
    }

    // ========== Рівень 2: сортування ==========
    System.out.println("\n=== 6. Фільми ДО сортування (порядок сховища) ===");
    EntityPrinter.printAll(movies.findAll());

    System.out.println("\n=== 7. findAllSortedByName() ===");
    EntityPrinter.printAll(movies.findAllSortedByName());

    System.out.println("\n=== 8. findAllSortedByNameAndByReleaseYear() ===");
    EntityPrinter.printAll(movies.findAllSortedByNameAndByReleaseYear());

    System.out.println("\n=== 9. Сховище ПІСЛЯ сортувань не змінилося ===");
    EntityPrinter.printAll(movies.findAll());

    System.out.println("\n=== 10. Ручний компаратор дає той самий порядок, що й thenComparing ===");
    System.out.println("  " + movies.findAllSorted(EntityComparators.MOVIE_BY_NAME_AND_YEAR_MANUAL)
            .equals(movies.findAllSortedByNameAndByReleaseYear()));

    // ========== Рівень 3: findAllSorted(Comparator<? super T>) і wildcard ==========
    System.out.println("\n=== 11. findAllSorted з довільним компаратором ===");
    System.out.println("  Сеанси лише за іменем (default-метод findAllSortedByName):");
    EntityPrinter.printAll(screenings.findAllSortedByName());
    System.out.println("  Сеанси за іменем, при збігу — за часом початку:");
    EntityPrinter.printAll(screenings.findAllSorted(EntityComparators.SCREENING_BY_NAME_AND_TIME));
    System.out.println("  Фільми за тривалістю:");
    EntityPrinter.printAll(movies.findAllSorted(java.util.Comparator.comparingInt(Movie::getDurationMinutes)));

    System.out.println("\n=== 12. printAll приймає колекції різних нащадків BaseEntity ===");
    List<BaseEntity> mixed = new ArrayList<>();
    mixed.add(movies.findById(1));
    mixed.add(screenings.findById(1));
    EntityPrinter.printAll(mixed);                 // List<BaseEntity>
    EntityPrinter.printAll(List.of(movies.findById(4))); // List<Movie>

    System.out.println("\n=== 13. equals за id ===");
    Movie a = new Movie(10, "Тест", 2000, 90, Genre.DRAMA);
    Movie b = new Movie(10, "Інша назва", 2001, 95, Genre.COMEDY);
    System.out.println("  a.equals(b) = " + a.equals(b) + " (той самий id), hash рівні: "
            + (a.hashCode() == b.hashCode()));
    System.out.println("  BY_NAME.compare(a, b) = " + EntityComparators.BY_NAME.compare(a, b)
            + " -> компаратор НЕ узгоджений з equals");

    System.out.println("\n=== 14. Сутність без імені (name == null) ===");
    List<Movie> withNull = new ArrayList<>(List.of(new Movie(11, null, 2020, 100, Genre.HORROR), a));
    withNull.sort(EntityComparators.BY_NAME); // nullsLast: без нього — NullPointerException
    EntityPrinter.printAll(withNull);
}
