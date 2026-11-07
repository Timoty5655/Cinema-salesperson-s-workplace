package ua.cinema.jdbc;

import ua.cinema.model.Movie;
import ua.cinema.model.Screening;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Операція над двома таблицями в одній транзакції:
 * «додати фільм у репертуар разом із розкладом сеансів».
 * Або фільм з'являється з усіма сеансами, або не з'являється нічого.
 */
public class ScheduleService {

    private final DataSource dataSource;
    private final JdbcMovieService movies;
    private final JdbcScreeningService screenings;

    public ScheduleService(DataSource dataSource, JdbcMovieService movies, JdbcScreeningService screenings) {
        this.dataSource = dataSource;
        this.movies = movies;
        this.screenings = screenings;
    }

    public void addMovieWithScreenings(Movie movie, List<Screening> schedule) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);                       // початок транзакції
            try {
                long movieId = movies.insertRow(c, movie);  // INSERT INTO movie
                List<Long> screeningIds = new ArrayList<>();
                for (Screening s : schedule) {
                    s.setMovieId(movieId);
                    screeningIds.add(screenings.insertRow(c, s)); // INSERT INTO screening
                }
                c.commit();                                // усе разом стає видимим
                movie.assignId(movieId);
                for (int i = 0; i < schedule.size(); i++) {
                    schedule.get(i).assignId(screeningIds.get(i));
                }
                System.out.println("  [tx] COMMIT: фільм id=" + movieId + " + " + schedule.size() + " сеанси");
            } catch (SQLException | RuntimeException e) {
                c.rollback();                              // скасувати і вже виконаний INSERT фільму
                schedule.forEach(s -> s.setMovieId(0));
                System.out.println("  [tx] ROLLBACK: " + e.getMessage().lines().findFirst().orElse(""));
                throw e;
            } finally {
                c.setAutoCommit(true);                     // з'єднання повертається в пул чистим
            }
        } catch (SQLException e) {
            throw new DataAccessException("Транзакцію скасовано", e);
        }
    }
}
