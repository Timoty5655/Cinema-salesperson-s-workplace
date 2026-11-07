package ua.cinema.hibernate;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import org.hibernate.LazyInitializationException;
import org.hibernate.stat.Statistics;
import ua.cinema.hibernate.broken.BrokenMovie;
import ua.cinema.jdbc.DBConnection;
import ua.cinema.model.Movie;

import java.util.List;

/** Відтворення типових проблем Hibernate та їх виправлення. */
public final class HibernateProblemsDemo {

    private HibernateProblemsDemo() {
    }

    /** Ліниву колекцію читають після закриття EntityManager. */
    public static void lazyInitialization(HibernateMovieService movies, long movieId) {
        Movie detached = movies.findById(movieId);         // EntityManager уже закрито
        try {
            System.out.println("  сеансів: " + detached.getScreenings().size());
        } catch (LazyInitializationException e) {
            System.out.println("  ПОМИЛКА: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        Movie fetched = movies.findByIdWithScreenings(movieId); // JOIN FETCH ще всередині сесії
        System.out.println("  ВИПРАВЛЕНО (JOIN FETCH): «" + fetched.getName() + "» має "
                + fetched.getScreenings().size() + " сеанс(и)");
    }

    /** N+1: 1 запит за фільмами + по одному запиту за сеансами КОЖНОГО фільму. */
    public static void nPlusOne(JpaUtil jpa, HibernateMovieService movies) {
        Statistics stats = jpa.statistics();

        System.out.println("  >>> ДО виправлення: SELECT m FROM Movie m + обхід m.getScreenings() у циклі");
        stats.clear();
        jpa.read(em -> {
            List<Movie> list = em.createQuery("SELECT m FROM Movie m ORDER BY m.name, m.releaseYear", Movie.class)
                    .getResultList();
            for (Movie m : list) {
                System.out.println("    " + m.getName() + " (" + m.getReleaseYear() + "): "
                        + m.getScreenings().size() + " сеанс(и)");   // кожен виклик — окремий SELECT
            }
            System.out.println("  фільмів: " + list.size());
            return null;
        });
        long before = stats.getPrepareStatementCount();

        System.out.println("  >>> ПІСЛЯ виправлення: LEFT JOIN FETCH m.screenings");
        stats.clear();
        for (Movie m : movies.findAllWithScreenings()) {
            System.out.println("    " + m.getName() + " (" + m.getReleaseYear() + "): "
                    + m.getScreenings().size() + " сеанс(и)");
        }
        long after = stats.getPrepareStatementCount();
        System.out.println("  SQL-запитів: до = " + before + " (1 + N), після = " + after);
    }

    /**
     * hbm2ddl.auto=validate з навмисно хибною сутністю (@Column(name = "duration")).
     * Окрема програмна конфігурація (Jakarta Persistence 3.2), щоб основний persistence-unit
     * залишався коректним і програма працювала далі.
     */
    public static void schemaValidation() {
        PersistenceConfiguration config = new PersistenceConfiguration("broken-validation-demo")
                .provider("org.hibernate.jpa.HibernatePersistenceProvider")
                .managedClass(BrokenMovie.class)
                .property("jakarta.persistence.nonJtaDataSource", DBConnection.getInstance().getDataSource())
                .property("hibernate.hbm2ddl.auto", "validate");
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory(config)) {
            System.out.println("  несподівано: валідація пройшла");
        } catch (RuntimeException e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println("  Hibernate відмовився стартувати: " + root.getClass().getSimpleName()
                    + ": " + root.getMessage());
        }
    }
}
