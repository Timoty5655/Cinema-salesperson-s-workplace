import java.util.Comparator;
import java.util.List;

/**
 * Узагальнений контракт сервісу керування сутностями.
 * Обмеження T extends BaseEntity дає сервісу право викликати getId() і getName()
 * у будь-якого T: без нього T був би просто Object і ключ сховища взяти було б ніде.
 */
public interface CRUDService<T extends BaseEntity> {

    /** @throws EntityNotFoundException якщо запису з таким id немає */
    T findById(long id);

    /** Додає нову сутність або замінює наявну з тим самим id. Повертає збережену сутність. */
    T save(T entity);

    /** @return true, якщо запис було видалено; false, якщо такого id не було */
    boolean delete(long id);

    /** Усі записи у порядку сховища (копія). */
    List<T> findAll();

    /** Нова відсортована копія; сховище не змінюється. */
    List<T> findAllSorted(Comparator<? super T> comparator);

    /**
     * default-метод: сортування за іменем однакове для всіх T, бо name є в BaseEntity.
     * EntityComparators.BY_NAME має тип Comparator<BaseEntity> — він підходить сюди
     * саме завдяки Comparator<? super T> у findAllSorted.
     */
    default List<T> findAllSortedByName() {
        return findAllSorted(EntityComparators.BY_NAME);
    }
}
