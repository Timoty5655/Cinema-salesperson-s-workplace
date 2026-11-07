package ua.cinema.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton, що тримає пул з'єднань (DataSource), а не одне спільне Connection.
 * Кожна операція бере своє з'єднання з пулу й повертає його в close() (try-with-resources),
 * тому транзакції різних клієнтів і різних потоків не заважають одна одній.
 *
 * Потокобезпечність: ідіома Initialization-on-demand holder. Клас Holder завантажується
 * JVM лише при першому getInstance(), а ініціалізація класу гарантовано відбувається
 * один раз і синхронізовано (JLS §12.4.2) — без synchronized і double-checked locking.
 */
public final class DBConnection {

    private static final String PROPERTIES_FILE = "application.properties";

    private final HikariDataSource dataSource;

    private DBConnection() {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(setting(props, "db.url", "DB_URL"));
        config.setUsername(setting(props, "db.user", "DB_USER"));
        config.setPassword(setting(props, "db.password", "DB_PASSWORD"));
        config.setMaximumPoolSize(Integer.parseInt(setting(props, "db.pool.size", "DB_POOL_SIZE")));
        config.setPoolName("cinema-pool");
        this.dataSource = new HikariDataSource(config);
    }

    private static final class Holder {
        private static final DBConnection INSTANCE = new DBConnection();
    }

    public static DBConnection getInstance() {
        return Holder.INSTANCE;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /** З'єднання з пулу. Викликаючий код закриває його (повертає в пул) через try-with-resources. */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        dataSource.close();
    }

    /** Змінна оточення має пріоритет над файлом: так пароль production-БД не комітиться. */
    private static String setting(Properties props, String key, String envName) {
        String fromEnv = System.getenv(envName);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("У " + PROPERTIES_FILE + " немає параметра " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Не знайдено " + PROPERTIES_FILE + " у classpath");
            }
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося прочитати " + PROPERTIES_FILE, e);
        }
    }
}
