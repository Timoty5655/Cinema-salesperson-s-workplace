package ua.cinema.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Фільм. Таблиця movie з init.sql. Клас не final — Hibernate створює для нього проксі. */
@Entity
@Table(name = "movie")
@AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 200))
public class Movie extends BaseEntity {

    @Column(name = "release_year", nullable = false)
    private int releaseYear;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    /** STRING: у БД "SCI_FI", а не 3. Додавання нового жанру в середину enum не зіпсує дані. */
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 20)
    private Genre genre;

    /**
     * Зворотна (inverse) сторона зв'язку: mappedBy = "movie" — колонка movie_id належить Screening.
     * LAZY: сеанси завантажуються лише при зверненні до колекції.
     * PERSIST/MERGE: новий сеанс, доданий через addScreening, зберігається разом із фільмом.
     * orphanRemoval: сеанс, прибраний з колекції (removeScreening), видаляється з БД —
     *   сеанс без фільму не має сенсу (movie_id NOT NULL).
     * REMOVE не вказано: як і ON DELETE RESTRICT в ЛР №10, видалення фільму не має
     *   мовчки знищувати розклад (див. HibernateMovieService.beforeDelete).
     */
    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("startTime")
    private List<Screening> screenings = new ArrayList<>();

    /** Не зберігається в БД: позначка касира «показати у вітрині сьогодні». */
    @Transient
    private boolean highlighted;

    protected Movie() {
        // для Hibernate
    }

    public Movie(String name, int releaseYear, int durationMinutes, Genre genre) {
        super(name);
        this.releaseYear = releaseYear;
        setDurationMinutes(durationMinutes);
        this.genre = genre;
    }

    /** Допоміжний метод: підтримує узгодженими ОБИДВІ сторони зв'язку. */
    public Screening addScreening(Screening screening) {
        screenings.add(screening);
        screening.setMovie(this);
        return screening;
    }

    public void removeScreening(Screening screening) {
        screenings.remove(screening);
        screening.setMovie(null);
    }

    /** Лише для читання: змінювати колекцію — тільки через addScreening/removeScreening. */
    public List<Screening> getScreenings() {
        return Collections.unmodifiableList(screenings);
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

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    /** Без screenings: інакше Movie.toString -> Screening.toString -> Movie.toString ... і LAZY-завантаження. */
    @Override
    public String toString() {
        return String.format("Movie{id=%d, '%s', %d, %d хв, %s}",
                getId(), getName(), releaseYear, durationMinutes, genre);
    }
}
