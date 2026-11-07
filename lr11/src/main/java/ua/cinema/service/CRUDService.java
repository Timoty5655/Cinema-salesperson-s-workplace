package ua.cinema.service;

import ua.cinema.model.BaseEntity;

import java.util.Comparator;
import java.util.List;

/** Контракт з ЛР №7 — без змін. Реалізації: у пам'яті (memory) і Hibernate (hibernate). */
public interface CRUDService<T extends BaseEntity> {

    /** @throws EntityNotFoundException якщо запису з таким id немає */
    T findById(long id);

    /** Новий (id == null) — додає й призначає id; наявний — оновлює. */
    T save(T entity);

    /** @return true, якщо запис було видалено; false, якщо такого id не було */
    boolean delete(long id);

    List<T> findAll();

    List<T> findAllSorted(Comparator<? super T> comparator);

    default List<T> findAllSortedByName() {
        return findAllSorted(EntityComparators.BY_NAME);
    }
}
