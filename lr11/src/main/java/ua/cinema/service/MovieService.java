package ua.cinema.service;

import ua.cinema.model.Genre;
import ua.cinema.model.Movie;

import java.util.List;

public interface MovieService extends CRUDService<Movie> {

    /** Спочатку за назвою, при однакових назвах (ремейки) — за роком виходу. */
    List<Movie> findAllSortedByNameAndByReleaseYear();

    List<Movie> findByName(String name);

    /** Пошук за значенням поля: усі фільми жанру, за назвою. */
    List<Movie> findByGenre(Genre genre);
}
