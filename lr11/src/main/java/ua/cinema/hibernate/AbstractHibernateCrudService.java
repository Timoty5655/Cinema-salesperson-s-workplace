package ua.cinema.hibernate;

import jakarta.persistence.EntityManager;
import ua.cinema.model.BaseEntity;
import ua.cinema.service.CRUDService;
import ua.cinema.service.EntityNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Спільна реалізація CRUD на Hibernate. Порівняно з JDBC (ЛР №10) тут немає SQL для
 * INSERT/UPDATE/DELETE, mapRow і setParameters — це робить Hibernate за анотаціями.
 * Підклас передає лише клас сутності (потрібен em.find і JPQL: тип T стирається).
 */
public abstract class AbstractHibernateCrudService<T extends BaseEntity> implements CRUDService<T> {

    protected final JpaUtil jpa;
    private final Class<T> type;

    protected AbstractHibernateCrudService(JpaUtil jpa, Class<T> type) {
        this.jpa = jpa;
        this.type = type;
    }

    /** Ім'я сутності в JPQL. Константа з класу, не з введення користувача. */
    protected String entityName() {
        return type.getSimpleName();
    }

    @Override
    public T findById(long id) {
        T entity = jpa.read(em -> em.find(type, id));
        if (entity == null) {
            throw new EntityNotFoundException(entityName(), id);
        }
        return entity;
    }

    /**
     * Новий (id == null) -> persist: INSERT, id з IDENTITY записується в той самий об'єкт.
     * Наявний -> merge: копіює стан detached-об'єкта в керований, на commit — UPDATE.
     */
    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Не можна зберегти null");
        return jpa.inTransaction(em -> {
            if (entity.isNew()) {
                em.persist(entity);
                return entity;
            }
            if (em.find(type, entity.getId()) == null) {
                throw new EntityNotFoundException(entityName(), entity.getId());
            }
            return em.merge(entity);
        });
    }

    @Override
    public boolean delete(long id) {
        return jpa.inTransaction(em -> {
            T entity = em.find(type, id);
            if (entity == null) {
                return false;
            }
            beforeDelete(em, entity);
            em.remove(entity);
            return true;
        });
    }

    /** Гачок: перевірки перед видаленням (за замовчуванням — немає). */
    protected void beforeDelete(EntityManager em, T entity) {
    }

    @Override
    public List<T> findAll() {
        return jpa.read(em -> em.createQuery(
                "SELECT e FROM " + entityName() + " e ORDER BY e.id", type).getResultList());
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        List<T> all = new java.util.ArrayList<>(findAll()); // довільний Comparator — лише в Java
        all.sort(comparator);
        return all;
    }

    /** Сортування в БД через JPQL ORDER BY. */
    @Override
    public List<T> findAllSortedByName() {
        return jpa.read(em -> em.createQuery(
                "SELECT e FROM " + entityName() + " e ORDER BY e.name, e.id", type).getResultList());
    }

    /** Сторінка: pageNumber з 0. setFirstResult = OFFSET, setMaxResults = LIMIT. */
    public List<T> findPage(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Некоректна сторінка: " + pageNumber + "/" + pageSize);
        }
        return jpa.read(em -> em.createQuery(
                        "SELECT e FROM " + entityName() + " e ORDER BY e.id", type)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList());
    }

    public long count() {
        return jpa.read(em -> em.createQuery(
                "SELECT COUNT(e) FROM " + entityName() + " e", Long.class).getSingleResult());
    }
}
