/**
 * Strategy: спосіб розрахунку знижки на замовлення квитків.
 * Чистий інтерфейс: реалізації не мають спільного стану, лише спільний контракт.
 */
public interface DiscountStrategy {

    String getName();

    /** Повертає суму до сплати після застосування знижки. */
    double apply(double subtotal, int ticketCount);

    /** default-метод: опис за замовчуванням, його успадковують усі класи. */
    default String describe() {
        return getName();
    }

    /** default-метод: розмір знижки в гривнях. */
    default double discountAmount(double subtotal, int ticketCount) {
        return subtotal - apply(subtotal, ticketCount);
    }
}
