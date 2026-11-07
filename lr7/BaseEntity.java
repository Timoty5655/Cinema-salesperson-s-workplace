import java.util.Objects;

/**
 * Базовий клас усіх сутностей, які зберігає CRUD-сервіс.
 * Тут лише те, що потрібно сервісу: ідентифікатор (ключ сховища) і назва (для сортування).
 * Поле id final: після створення сутності воно не змінюється, тому ключ у Map завжди коректний.
 */
public abstract class BaseEntity {

    private final long id;
    private final String name;

    protected BaseEntity(long id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("id має бути додатним: " + id);
        }
        this.id = id;
        this.name = name; // null допускаємо свідомо, компаратор за іменем його обробляє (nullsLast)
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Рівність за id: два об'єкти описують один і той самий запис сховища,
     * навіть якщо інші поля відрізняються (стара і нова версії при оновленні).
     * getClass(): Movie з id=1 і Screening з id=1 — різні сутності.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((BaseEntity) o).id;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
