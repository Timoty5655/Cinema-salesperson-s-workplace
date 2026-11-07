package ua.cinema.service;

import ua.cinema.model.Screening;

import java.util.List;

public interface ScreeningService extends CRUDService<Screening> {

    /** Спочатку за назвою сеансу, при збігу — за часом початку. */
    List<Screening> findAllSortedByNameAndByStartTime();

    List<Screening> findByMovieId(long movieId);
}
