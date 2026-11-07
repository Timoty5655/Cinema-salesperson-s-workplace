import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Узагальнене сховище в пам'яті. Працює з будь-якою сутністю-нащадком BaseEntity без змін.
 *
 * Сховище — LinkedHashMap<Long, T>:
 *  - findById / save / delete за O(1) (хеш-таблиця за id);
 *  - порядок обходу = порядок додавання, тому findAll() передбачуваний;
 *  - new T[] створити не можна (стирання типів), а Map<Long, T> обходиться без масивів.
 * Порівняння з ArrayList, HashMap, TreeMap — у README.
 */
public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {

    private final Map<Long, T> storage = new LinkedHashMap<>();
    private final String entityType;

    /** entityType — лише для повідомлень: тип T під час виконання недоступний (стирання). */
    public InMemoryCrudService(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public T findById(long id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(entityType, id);
        }
        return entity;
    }

    /**
     * Map.put сам розрізняє додавання й оновлення: повертає попереднє значення або null.
     * Для наявного ключа LinkedHashMap зберігає початкову позицію запису.
     */
    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Не можна зберегти null");
        T previous = storage.put(entity.getId(), entity); // getId() доступний завдяки T extends BaseEntity
        System.out.println("  save: " + (previous == null ? "додано " : "оновлено ")
                + entityType + " id=" + entity.getId());
        return entity;
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /** Сортуємо копію, тому порядок і склад сховища не змінюються. */
    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        List<T> copy = new ArrayList<>(storage.values());
        copy.sort(comparator);
        return copy;
    }

    public int count() {
        return storage.size();
    }
}
