import java.util.List;

/**
 * Сервіс конкретної сутності. Тут живуть операції, що залежать від полів Movie:
 * у загальному CRUDService<T> немає releaseYear, бо його немає в кожного T.
 */
public interface MovieService extends CRUDService<Movie> {

    /** Спочатку за назвою, при однакових назвах — за роком виходу. */
    List<Movie> findAllSortedByNameAndByReleaseYear();
}
