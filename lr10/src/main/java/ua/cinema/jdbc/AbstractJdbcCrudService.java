package ua.cinema.jdbc;

import ua.cinema.model.BaseEntity;
import ua.cinema.service.CRUDService;
import ua.cinema.service.EntityNotFoundException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Template Method для JDBC-сервісів.
 * Каркас (findById, save, delete, findAll, findPage, saveAll) однаковий для всіх сутностей
 * і написаний тут один раз. Підклас надає лише те, що відрізняється:
 *   SQL-запити, mapRow(ResultSet) і setParameters(PreparedStatement, T).
 *
 * Усі запити — PreparedStatement, усі ресурси — try-with-resources.
 * Імена таблиць/колонок у SQL — константи підкласів, а не введення користувача.
 */
public abstract class AbstractJdbcCrudService<T extends BaseEntity> implements CRUDService<T> {

    private final DataSource dataSource;

    protected AbstractJdbcCrudService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ---------------- "гачки" для підкласів ----------------

    protected abstract String entityType();

    /** SELECT зі списком колонок, без WHERE/ORDER BY, наприклад "SELECT id, name FROM movie". */
    protected abstract String selectSql();

    /** INSERT без колонки id (її генерує БД). */
    protected abstract String insertSql();

    /** UPDATE ... WHERE id = ? — id завжди останній параметр. */
    protected abstract String updateSql();

    protected abstract String deleteSql();

    /** Один рядок ResultSet -> об'єкт. Курсор уже стоїть на потрібному рядку. */
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /** Заповнює параметри INSERT/UPDATE (без id). Повертає кількість заповнених параметрів. */
    protected abstract int setParameters(PreparedStatement ps, T entity) throws SQLException;

    // ---------------- каркас ----------------

    @Override
    public T findById(long id) {
        List<T> found = query(selectSql() + " WHERE id = ?", ps -> ps.setLong(1, id));
        if (found.isEmpty()) {
            throw new EntityNotFoundException(entityType(), id);
        }
        return found.getFirst();
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Не можна зберегти null");
        try (Connection c = dataSource.getConnection()) {
            if (entity.isNew()) {
                entity.assignId(insertRow(c, entity));
            } else {
                updateRow(c, entity);
            }
            return entity;
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося зберегти " + entity, e);
        }
    }

    /**
     * INSERT у переданому з'єднанні (щоб працювати всередині чужої транзакції).
     * Повертає згенерований БД ключ, але не призначає його сутності: після rollback
     * об'єкт не повинен мати id запису, якого немає.
     */
    long insertRow(Connection c, T entity) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(insertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, entity);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("БД не повернула згенерований id");
                }
                return keys.getLong(1);
            }
        }
    }

    private void updateRow(Connection c, T entity) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(updateSql())) {
            int n = setParameters(ps, entity);
            ps.setLong(n + 1, entity.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException(entityType(), entity.getId());
            }
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(deleteSql())) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataAccessException(entityType() + " id=" + id
                    + " не можна видалити: на нього посилаються інші записи", e);
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося видалити " + entityType() + " id=" + id, e);
        }
    }

    /** Без обмежень — лише для невеликих таблиць; для великих використовуйте findPage. */
    @Override
    public List<T> findAll() {
        return query(selectSql() + " ORDER BY id", ps -> { });
    }

    /** Довільний Comparator можна застосувати лише в Java (SQL його не знає). */
    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        List<T> all = findAll();
        all.sort(comparator);
        return all;
    }

    /**
     * Перевизначає default-метод: сортує БД (ORDER BY), а не Java.
     * Українська абетка — завдяки COLLATION=UKRAINIAN у JDBC URL (див. README).
     */
    @Override
    public List<T> findAllSortedByName() {
        return query(selectSql() + " ORDER BY name, id", ps -> { });
    }

    /** Посторінкове читання: pageNumber з 0. */
    public List<T> findPage(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Некоректна сторінка: " + pageNumber + "/" + pageSize);
        }
        return query(selectSql() + " ORDER BY id LIMIT ? OFFSET ?", ps -> {
            ps.setInt(1, pageSize);
            ps.setLong(2, (long) pageNumber * pageSize);
        });
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM (" + selectSql() + ") t";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new DataAccessException("Помилка COUNT для " + entityType(), e);
        }
    }

    /**
     * Пакетна вставка нових сутностей: один PreparedStatement, addBatch для кожного рядка,
     * один executeBatch і одна транзакція. Згенеровані id призначаються після commit.
     */
    public void saveAll(List<T> entities) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(insertSql(), Statement.RETURN_GENERATED_KEYS)) {
                for (T e : entities) {
                    setParameters(ps, e);
                    ps.addBatch();
                }
                ps.executeBatch();
                List<Long> ids = new ArrayList<>();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    while (keys.next()) {
                        ids.add(keys.getLong(1));
                    }
                }
                c.commit();
                for (int i = 0; i < ids.size() && i < entities.size(); i++) {
                    entities.get(i).assignId(ids.get(i));
                }
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Пакетна вставка " + entityType() + " не вдалася", e);
        }
    }

    /** Пакетне видалення за id. */
    public void deleteAll(Collection<Long> ids) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(deleteSql())) {
            for (long id : ids) {
                ps.setLong(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Пакетне видалення " + entityType() + " не вдалося", e);
        }
    }

    // ---------------- спільний код читання ----------------

    @FunctionalInterface
    protected interface ParameterSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    /** Один цикл while(rs.next()) + mapRow для всіх SELECT-ів підкласів. */
    protected List<T> query(String sql, ParameterSetter parameters) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            parameters.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Помилка запиту до " + entityType() + ": " + sql, e);
        }
    }
}
