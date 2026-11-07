package ua.cinema.jdbc;

import ua.cinema.model.Screening;
import ua.cinema.service.ScreeningService;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/** Друга сутність — повністю через AbstractJdbcCrudService: лише SQL, mapRow і setParameters. */
public class JdbcScreeningService extends AbstractJdbcCrudService<Screening> implements ScreeningService {

    private static final String SELECT =
            "SELECT id, name, movie_id, hall_number, start_time, price FROM screening";

    public JdbcScreeningService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String entityType() {
        return "Screening";
    }

    @Override
    protected String selectSql() {
        return SELECT;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO screening (name, movie_id, hall_number, start_time, price) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE screening SET name = ?, movie_id = ?, hall_number = ?, start_time = ?, price = ? WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM screening WHERE id = ?";
    }

    @Override
    protected Screening mapRow(ResultSet rs) throws SQLException {
        return new Screening(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("movie_id"),
                rs.getInt("hall_number"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getBigDecimal("price"));           // NUMERIC -> BigDecimal без втрати точності
    }

    @Override
    protected int setParameters(PreparedStatement ps, Screening s) throws SQLException {
        ps.setString(1, s.getName());
        ps.setLong(2, s.getMovieId());
        ps.setInt(3, s.getHallNumber());
        ps.setTimestamp(4, Timestamp.valueOf(s.getStartTime()));
        ps.setBigDecimal(5, s.getPrice());
        return 5;
    }

    @Override
    public List<Screening> findAllSortedByNameAndByStartTime() {
        return query(SELECT + " ORDER BY name, start_time", ps -> { });
    }

    @Override
    public List<Screening> findByMovieId(long movieId) {
        return query(SELECT + " WHERE movie_id = ? ORDER BY start_time", ps -> ps.setLong(1, movieId));
    }
}
