package ua.cinema.memory;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;
import ua.cinema.service.EntityComparators;
import ua.cinema.service.MovieService;

import java.util.List;

public class InMemoryMovieService extends InMemoryCrudService<Movie> implements MovieService {

    public InMemoryMovieService() {
        super("Movie");
    }

    @Override
    public List<Movie> findAllSortedByNameAndByReleaseYear() {
        return findAllSorted(EntityComparators.MOVIE_BY_NAME_AND_YEAR);
    }

    @Override
    public List<Movie> findByName(String name) {
        return findAll().stream().filter(m -> name.equals(m.getName())).toList();
    }

    @Override
    public List<Movie> findByGenre(Genre genre) {
        return findAllSortedByName().stream().filter(m -> m.getGenre() == genre).toList();
    }
}
