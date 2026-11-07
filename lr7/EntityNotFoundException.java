/**
 * Сутність із заданим id відсутня у сховищі.
 * Неперевірений (RuntimeException): звертання за неіснуючим id — помилка логіки
 * викликаючого коду, а не очікувана ситуація, яку треба обробляти в кожному місці.
 * Пояснення — у README.
 */
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
