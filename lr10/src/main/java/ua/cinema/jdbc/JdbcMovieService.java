package ua.cinema.jdbc;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.service.MovieService;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** JDBC-реалізація того самого MovieService, що й InMemoryMovieService з ЛР №7. */
public class JdbcMovieService extends AbstractJdbcCrudService<Movie> implements MovieService {

    private static final String SELECT =
            "SELECT id, name, release_year, duration_minutes, genre FROM movie";

    public JdbcMovieService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String entityType() {
        return "Movie";
    }

    @Override
    protected String selectSql() {
        return SELECT;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO movie (name, release_year, duration_minutes, genre) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE movie SET name = ?, release_year = ?, duration_minutes = ?, genre = ? WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM movie WHERE id = ?";
    }

    @Override
    protected Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("release_year"),
                rs.getInt("duration_minutes"),
                Genre.valueOf(rs.getString("genre")));
    }

    @Override
    protected int setParameters(PreparedStatement ps, Movie m) throws SQLException {
        ps.setString(1, m.getName());
        ps.setInt(2, m.getReleaseYear());
        ps.setInt(3, m.getDurationMinutes());
        ps.setString(4, m.getGenre().name());
        return 4;
    }

    /** Сортування виконує БД. */
    @Override
    public List<Movie> findAllSortedByNameAndByReleaseYear() {
        return query(SELECT + " ORDER BY name, release_year", ps -> { });
    }

    /** Значення передається параметром ?, тому "' OR '1'='1" — просто дивна назва, а не SQL. */
    @Override
    public List<Movie> findByName(String name) {
        return query(SELECT + " WHERE name = ? ORDER BY release_year", ps -> ps.setString(1, name));
    }
}
