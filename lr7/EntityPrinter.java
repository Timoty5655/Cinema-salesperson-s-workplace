import java.util.Collection;

public final class EntityPrinter {

    private EntityPrinter() {
    }

    /**
     * Collection<? extends BaseEntity> приймає List<Movie>, List<Screening>, Set<BaseEntity>...
     * Collection<BaseEntity> прийняв би лише колекцію саме BaseEntity:
     * generics інваріантні, List<Movie> не є підтипом List<BaseEntity>.
     * Метод лише читає елементи, тому extends (producer) тут правильний.
     */
    public static void printAll(Collection<? extends BaseEntity> items) {
        if (items.isEmpty()) {
            System.out.println("  (порожньо)");
        }
        for (BaseEntity item : items) {
            System.out.println("  " + item);
        }
    }
}
