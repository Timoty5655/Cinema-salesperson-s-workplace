package ua.cinema.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Сеанс. Таблиця screening з init.sql. */
@Entity
@Table(name = "screening")
@AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 100))
public class Screening extends BaseEntity {

    /**
     * ВЛАСНИК зв'язку (owning side): саме тут колонка-зовнішній ключ movie_id,
     * і Hibernate записує в неї значення лише за цим полем.
     * LAZY: за замовчуванням @ManyToOne — EAGER, що дає зайві JOIN/запити.
     * Без cascade: видалення/збереження сеансу не повинне чіпати фільм.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "hall_number", nullable = false)
    private int hallNumber;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** NUMERIC(8, 2) в init.sql. */
    @Column(name = "price", nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    protected Screening() {
        // для Hibernate
    }

    /** Фільм прив'язується через movie.addScreening(screening). */
    public Screening(String name, int hallNumber, LocalDateTime startTime, BigDecimal price) {
        super(name);
        this.hallNumber = hallNumber;
        this.startTime = startTime;
        setPrice(price);
    }

    public Movie getMovie() {
        return movie;
    }

    /** package-private: напряму змінює лише одну сторону, тому викликається з Movie.add/removeScreening. */
    void setMovie(Movie movie) {
        this.movie = movie;
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

    /** Лише id фільму: getId() у LAZY-проксі не ініціалізує його і не робить запит. */
    @Override
    public String toString() {
        return String.format("Screening{id=%d, '%s', фільм=%s, зал %d, %s, %s грн}",
                getId(), getName(), movie == null ? null : movie.getId(), hallNumber, startTime, price);
    }
}
