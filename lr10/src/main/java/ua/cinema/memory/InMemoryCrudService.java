package ua.cinema.memory;

import ua.cinema.model.BaseEntity;
import ua.cinema.service.CRUDService;
import ua.cinema.service.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/** Реалізація з ЛР №7. Єдина зміна: для нових сутностей (id == 0) генерує id, як це робить БД. */
public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {

    private final Map<Long, T> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final String entityType;

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

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Не можна зберегти null");
        if (entity.isNew()) {
            entity.assignId(sequence.incrementAndGet());
        }
        storage.put(entity.getId(), entity);
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

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        List<T> copy = new ArrayList<>(storage.values());
        copy.sort(comparator);
        return copy;
    }
}
