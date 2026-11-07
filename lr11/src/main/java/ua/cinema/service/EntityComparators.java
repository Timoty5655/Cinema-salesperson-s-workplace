package ua.cinema.service;

import ua.cinema.model.BaseEntity;
import ua.cinema.model.Movie;
import ua.cinema.model.Screening;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class EntityComparators {

    private EntityComparators() {
    }

    /** Українська абетка ("Ї" після "Д", перед "К"), регістр не враховується. */
    private static final Collator UA_COLLATOR = Collator.getInstance(Locale.of("uk", "UA"));

    static {
        UA_COLLATOR.setStrength(Collator.SECONDARY);
    }

    public static <T extends BaseEntity> Comparator<T> byName() {
        return Comparator.comparing(BaseEntity::getName, Comparator.nullsLast(UA_COLLATOR));
    }

    public static final Comparator<BaseEntity> BY_NAME = byName();

    public static final Comparator<Movie> MOVIE_BY_NAME_AND_YEAR =
            EntityComparators.<Movie>byName().thenComparingInt(Movie::getReleaseYear);

    public static final Comparator<Screening> SCREENING_BY_NAME_AND_TIME =
            EntityComparators.<Screening>byName().thenComparing(Screening::getStartTime);
}
