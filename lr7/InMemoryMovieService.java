import java.util.List;

/** Успадковує всю CRUD-логіку, додає лише специфічне сортування. */
public class InMemoryMovieService extends InMemoryCrudService<Movie> implements MovieService {

    public InMemoryMovieService() {
        super("Movie");
    }

    @Override
    public List<Movie> findAllSortedByNameAndByReleaseYear() {
        return findAllSorted(EntityComparators.MOVIE_BY_NAME_AND_YEAR);
    }
}
