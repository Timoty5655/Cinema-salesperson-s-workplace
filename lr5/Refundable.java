/**
 * Окремий інтерфейс: можливість повернення коштів.
 * Його реалізують не всі способи оплати (готівка - ні),
 * тому це окремий контракт, а не метод у PaymentMethod.
 */
public interface Refundable {

    /** Повертає суму, яка буде повернена клієнту. */
    double refund(double paidAmount);

    default String refundPolicy() {
        return "Повернення коштів у повному обсязі";
    }
}
