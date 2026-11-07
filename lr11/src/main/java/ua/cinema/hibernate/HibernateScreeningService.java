package ua.cinema.hibernate;

import ua.cinema.model.Screening;
import ua.cinema.service.ScreeningService;

import java.util.List;

public class HibernateScreeningService extends AbstractHibernateCrudService<Screening> implements ScreeningService {

    public HibernateScreeningService(JpaUtil jpa) {
        super(jpa, Screening.class);
    }

    @Override
    public List<Screening> findAllSortedByNameAndByStartTime() {
        return jpa.read(em -> em.createQuery(
                "SELECT s FROM Screening s ORDER BY s.name, s.startTime", Screening.class).getResultList());
    }

    /** s.movie.id — порівняння з FK-колонкою, без JOIN до таблиці movie. */
    @Override
    public List<Screening> findByMovieId(long movieId) {
        return jpa.read(em -> em.createQuery(
                        "SELECT s FROM Screening s WHERE s.movie.id = :movieId ORDER BY s.startTime", Screening.class)
                .setParameter("movieId", movieId)
                .getResultList());
    }
}
