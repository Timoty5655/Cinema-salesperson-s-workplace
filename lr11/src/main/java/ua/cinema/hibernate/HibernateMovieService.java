package ua.cinema.hibernate;

import jakarta.persistence.EntityManager;
import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.model.Screening;
import ua.cinema.service.EntityNotFoundException;
import ua.cinema.service.MovieService;

import java.util.List;

/** Третя реалізація MovieService (після InMemoryMovieService і JdbcMovieService). */
public class HibernateMovieService extends AbstractHibernateCrudService<Movie> implements MovieService {

    public HibernateMovieService(JpaUtil jpa) {
        super(jpa, Movie.class);
    }

    /** JPQL оперує полями класу (m.releaseYear), а не колонками (release_year). */
    @Override
    public List<Movie> findAllSortedByNameAndByReleaseYear() {
        return jpa.read(em -> em.createQuery(
                "SELECT m FROM Movie m ORDER BY m.name, m.releaseYear", Movie.class).getResultList());
    }

    /** Іменований параметр :name — значення ніколи не потрапляє в текст запиту. */
    @Override
    public List<Movie> findByName(String name) {
        return jpa.read(em -> em.createQuery(
                        "SELECT m FROM Movie m WHERE m.name = :name ORDER BY m.releaseYear", Movie.class)
                .setParameter("name", name)
                .getResultList());
    }

    @Override
    public List<Movie> findByGenre(Genre genre) {
        return jpa.read(em -> em.createQuery(
                        "SELECT m FROM Movie m WHERE m.genre = :genre ORDER BY m.name, m.releaseYear", Movie.class)
                .setParameter("genre", genre)
                .getResultList());
    }

    /** Фільм разом із сеансами одним запитом (лікування LazyInitializationException). */
    public Movie findByIdWithScreenings(long id) {
        List<Movie> found = jpa.read(em -> em.createQuery(
                        "SELECT m FROM Movie m LEFT JOIN FETCH m.screenings WHERE m.id = :id", Movie.class)
                .setParameter("id", id)
                .getResultList());
        if (found.isEmpty()) {
            throw new EntityNotFoundException(entityName(), id);
        }
        return found.getFirst();
    }

    /** Усі фільми з сеансами одним SQL (лікування N+1). */
    public List<Movie> findAllWithScreenings() {
        return jpa.read(em -> em.createQuery(
                "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.screenings ORDER BY m.name, m.releaseYear",
                Movie.class).getResultList());
    }

    /** Додати сеанс до розкладу: addScreening + cascade PERSIST, без явного em.persist(screening). */
    public Screening addScreening(long movieId, Screening screening) {
        return jpa.inTransaction(em -> {
            Movie movie = em.find(Movie.class, movieId);
            if (movie == null) {
                throw new EntityNotFoundException(entityName(), movieId);
            }
            return movie.addScreening(screening);
        });
    }

    /** Прибрати сеанс з розкладу: removeScreening + orphanRemoval -> DELETE FROM screening. */
    public void removeScreening(long movieId, long screeningId) {
        jpa.inTransaction(em -> {
            Movie movie = em.find(Movie.class, movieId);
            if (movie == null) {
                throw new EntityNotFoundException(entityName(), movieId);
            }
            movie.getScreenings().stream()
                    .filter(s -> s.getId() == screeningId)
                    .findFirst()
                    .ifPresent(movie::removeScreening);
            return null;
        });
    }

    /**
     * Та сама бізнес-логіка, що й ON DELETE RESTRICT у ЛР №10: фільм із сеансами не видаляємо.
     * Без цієї перевірки orphanRemoval каскадно видалив би і весь розклад.
     */
    @Override
    protected void beforeDelete(EntityManager em, Movie movie) {
        long screenings = em.createQuery(
                        "SELECT COUNT(s) FROM Screening s WHERE s.movie = :movie", Long.class)
                .setParameter("movie", movie)
                .getSingleResult();
        if (screenings > 0) {
            throw new IllegalStateException("Фільм «" + movie.getName() + "» має " + screenings
                    + " сеанс(и): спершу скасуйте їх");
        }
    }
}
