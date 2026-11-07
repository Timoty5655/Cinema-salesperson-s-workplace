package ua.cinema.model;

/**
 * Базовий клас сутностей. Порівняно з ЛР №7 id тепер генерує сховище (БД),
 * тому 0 означає «ще не збережено». Призначити id можна лише один раз —
 * після цього він незмінний, як і раніше.
 */
public abstract class BaseEntity {

    private long id;
    private String name;

    protected BaseEntity(long id, String name) {
        if (id < 0) {
            throw new IllegalArgumentException("id не може бути від'ємним: " + id);
        }
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public boolean isNew() {
        return id == 0;
    }

    /** Викликається сховищем після INSERT (згенерований ключ). Повторно змінити id не можна. */
    public void assignId(long generatedId) {
        if (this.id != 0) {
            throw new IllegalStateException("id уже призначено: " + this.id);
        }
        if (generatedId <= 0) {
            throw new IllegalArgumentException("id має бути додатним: " + generatedId);
        }
        this.id = generatedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Рівність за id. Ще не збережені (id == 0) сутності рівні лише самі собі.
     * hashCode не залежить від id: він не має змінитися, коли сховище призначить id
     * об'єкту, що вже лежить у HashSet.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity other = (BaseEntity) o;
        return id != 0 && id == other.id;
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
