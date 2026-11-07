/** Фільм у репертуарі кінотеатру. name — назва фільму. */
public class Movie extends BaseEntity {

    private final int releaseYear;
    private final int durationMinutes;
    private final Genre genre;

    public Movie(long id, String name, int releaseYear, int durationMinutes, Genre genre) {
        super(id, name);
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Тривалість має бути додатною: " + durationMinutes);
        }
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public Genre getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return String.format("Movie{id=%d, '%s', %d, %d хв, %s}",
                getId(), getName(), releaseYear, durationMinutes, genre);
    }
}
