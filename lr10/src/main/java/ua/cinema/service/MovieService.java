package ua.cinema.service;

import ua.cinema.model.Movie;

import java.util.List;

public interface MovieService extends CRUDService<Movie> {

    /** Спочатку за назвою, при однакових назвах (ремейки) — за роком виходу. */
    List<Movie> findAllSortedByNameAndByReleaseYear();

    List<Movie> findByName(String name);
}
