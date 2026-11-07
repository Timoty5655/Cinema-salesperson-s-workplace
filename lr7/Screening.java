import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Сеанс показу фільму. name — назва сеансу ("Ранковий", "Вечірній"...). */
public class Screening extends BaseEntity {

    private final long movieId;
    private final int hallNumber;
    private final LocalDateTime startTime;
    private final BigDecimal price;

    public Screening(long id, String name, long movieId, int hallNumber,
                     LocalDateTime startTime, BigDecimal price) {
        super(id, name);
        if (price.signum() <= 0) {
            throw new IllegalArgumentException("Ціна має бути додатною: " + price);
        }
        this.movieId = movieId;
        this.hallNumber = hallNumber;
        this.startTime = startTime;
        this.price = price;
    }

    public long getMovieId() {
        return movieId;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("Screening{id=%d, '%s', фільм=%d, зал %d, %s, %s грн}",
                getId(), getName(), movieId, hallNumber, startTime, price);
    }
}
