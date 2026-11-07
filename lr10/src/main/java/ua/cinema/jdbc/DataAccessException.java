package ua.cinema.jdbc;

/**
 * Обгортка над SQLException. Інтерфейси сервісів (з ЛР №7) не оголошують throws SQLException —
 * і не повинні: клієнтський код не має залежати від того, що всередині саме JDBC.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
