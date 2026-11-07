package ua.cinema.model;

/** Фільм у репертуарі кінотеатру. name — назва фільму. Таблиця movie. */
public class Movie extends BaseEntity {

    private int releaseYear;
    private int durationMinutes;
    private Genre genre;

    /** Новий фільм, id призначить сховище. */
    public Movie(String name, int releaseYear, int durationMinutes, Genre genre) {
        this(0, name, releaseYear, durationMinutes, genre);
    }

    public Movie(long id, String name, int releaseYear, int durationMinutes, Genre genre) {
        super(id, name);
        this.releaseYear = releaseYear;
        setDurationMinutes(durationMinutes);
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Тривалість має бути додатною: " + durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return String.format("Movie{id=%d, '%s', %d, %d хв, %s}",
                getId(), getName(), releaseYear, durationMinutes, genre);
    }
}
