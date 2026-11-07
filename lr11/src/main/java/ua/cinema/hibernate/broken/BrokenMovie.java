package ua.cinema.hibernate.broken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * НАВМИСНО ХИБНЕ відображення таблиці movie — лише для демонстрації hbm2ddl.auto=validate.
 * Відрізняється від Movie одним @Column: "duration" замість "duration_minutes".
 * У persistence-unit "cinema" цей клас не входить (exclude-unlisted-classes).
 */
@Entity(name = "BrokenMovie")
@Table(name = "movie")
public class BrokenMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "duration", nullable = false)   // у таблиці колонка називається duration_minutes
    private int durationMinutes;

    protected BrokenMovie() {
    }
}
