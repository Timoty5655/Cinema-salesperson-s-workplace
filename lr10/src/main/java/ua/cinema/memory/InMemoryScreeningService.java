package ua.cinema.memory;

import ua.cinema.model.Screening;
import ua.cinema.service.EntityComparators;
import ua.cinema.service.ScreeningService;

import java.util.List;

public class InMemoryScreeningService extends InMemoryCrudService<Screening> implements ScreeningService {

    public InMemoryScreeningService() {
        super("Screening");
    }

    @Override
    public List<Screening> findAllSortedByNameAndByStartTime() {
        return findAllSorted(EntityComparators.SCREENING_BY_NAME_AND_TIME);
    }

    @Override
    public List<Screening> findByMovieId(long movieId) {
        return findAll().stream().filter(s -> s.getMovieId() == movieId).toList();
    }
}
