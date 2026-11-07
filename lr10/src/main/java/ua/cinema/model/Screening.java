package ua.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Сеанс показу фільму. Багато сеансів -> один фільм (screening.movie_id). */
public class Screening extends BaseEntity {

    private long movieId;
    private int hallNumber;
    private LocalDateTime startTime;
    private BigDecimal price;

    public Screening(String name, long movieId, int hallNumber, LocalDateTime startTime, BigDecimal price) {
        this(0, name, movieId, hallNumber, startTime, price);
    }

    public Screening(long id, String name, long movieId, int hallNumber,
                     LocalDateTime startTime, BigDecimal price) {
        super(id, name);
        this.movieId = movieId;
        this.hallNumber = hallNumber;
        this.startTime = startTime;
        setPrice(price);
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Ціна має бути додатною: " + price);
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Screening{id=%d, '%s', фільм=%d, зал %d, %s, %s грн}",
                getId(), getName(), movieId, hallNumber, startTime, price);
    }
}
