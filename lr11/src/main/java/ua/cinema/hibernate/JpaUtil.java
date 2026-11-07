package ua.cinema.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import ua.cinema.jdbc.DBConnection;
import ua.cinema.jdbc.DbInitializer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Function;

/**
 * Singleton: EntityManagerFactory — важкий потокобезпечний об'єкт (метамодель, пул, кеші),
 * створюється ОДИН раз на застосунок. EntityManager — легкий і НЕ потокобезпечний,
 * створюється на кожну операцію й одразу закривається.
 */
public final class JpaUtil {

    public static final String PERSISTENCE_UNIT = "cinema";

    static {
        // jboss-logging (через нього пише Hibernate) сам обрав би java.util.logging; спрямовуємо в slf4j-simple
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private final EntityManagerFactory emf;

    private JpaUtil() {
        DataSource dataSource = DBConnection.getInstance().getDataSource();
        DbInitializer.run(dataSource);  // схему створює init.sql, Hibernate її лише перевіряє (validate)
        this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT,
                Map.of("jakarta.persistence.nonJtaDataSource", dataSource));
    }

    private static final class Holder {
        private static final JpaUtil INSTANCE = new JpaUtil();
    }

    public static JpaUtil getInstance() {
        return Holder.INSTANCE;
    }

    /** Робота, що змінює дані: begin -> work -> commit, при винятку — rollback. */
    public <R> R inTransaction(Function<EntityManager, R> work) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Лише читання. Після виходу EntityManager закритий: усі отримані сутності — detached. */
    public <R> R read(Function<EntityManager, R> work) {
        try (EntityManager em = emf.createEntityManager()) {
            return work.apply(em);
        }
    }

    /** Статистика Hibernate (hibernate.generate_statistics=true) — рахуємо SQL-запити для N+1. */
    public Statistics statistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void shutdown() {
        emf.close();
        DBConnection.getInstance().shutdown();
    }
}
