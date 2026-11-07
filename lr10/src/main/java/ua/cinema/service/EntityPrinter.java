package ua.cinema.service;

import ua.cinema.model.BaseEntity;

import java.util.Collection;

public final class EntityPrinter {

    private EntityPrinter() {
    }

    public static void printAll(Collection<? extends BaseEntity> items) {
        if (items.isEmpty()) {
            System.out.println("  (порожньо)");
        }
        for (BaseEntity item : items) {
            System.out.println("  " + item);
        }
    }
}
