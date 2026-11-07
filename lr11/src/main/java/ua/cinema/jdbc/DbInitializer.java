package ua.cinema.jdbc;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Виконує init.sql з classpath під час старту. Скрипт ідемпотентний (IF NOT EXISTS),
 * тож повторний запуск нічого не дублює.
 * Тут Statement допустимий: SQL — константний текст із ресурсу, без даних користувача.
 */
public final class DbInitializer {

    private static final String SCRIPT = "init.sql";

    private DbInitializer() {
    }

    public static void run(DataSource dataSource) {
        List<String> statements = splitStatements(readScript());
        try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
            for (String sql : statements) {
                st.execute(sql);
            }
            System.out.println("[init] init.sql виконано: " + statements.size() + " інструкцій");
        } catch (SQLException e) {
            throw new DataAccessException("Помилка виконання " + SCRIPT, e);
        }
    }

    /** Повне очищення БД (для режиму reset), після нього знову init.sql. */
    public static void reset(DataSource dataSource) {
        try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
            st.execute("DROP ALL OBJECTS");
            System.out.println("[init] БД очищено");
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося очистити БД", e);
        }
        run(dataSource);
    }

    private static String readScript() {
        // getResourceAsStream шукає файл у classpath: src/main/resources потрапляє в target/classes
        try (InputStream in = DbInitializer.class.getClassLoader().getResourceAsStream(SCRIPT)) {
            if (in == null) {
                throw new IllegalStateException(SCRIPT + " не знайдено у classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося прочитати " + SCRIPT, e);
        }
    }

    /** Прибирає коментарі "--" і ділить скрипт за ";". */
    private static List<String> splitStatements(String script) {
        StringBuilder clean = new StringBuilder();
        for (String line : script.split("\\R")) {
            int comment = line.indexOf("--");
            clean.append(comment >= 0 ? line.substring(0, comment) : line).append('\n');
        }
        List<String> result = new ArrayList<>();
        for (String part : clean.toString().split(";")) {
            if (!part.isBlank()) {
                result.add(part.trim());
            }
        }
        return result;
    }
}
