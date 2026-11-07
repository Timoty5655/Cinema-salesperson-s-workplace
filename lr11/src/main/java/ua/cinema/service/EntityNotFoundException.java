package ua.cinema.service;

/** Неперевірений: звертання за неіснуючим id — помилка логіки викликаючого коду (див. README ЛР №7). */
public class EntityNotFoundException extends RuntimeException {

    private final long id;

    public EntityNotFoundException(String entityType, long id) {
        super(entityType + " з id=" + id + " не знайдено");
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
