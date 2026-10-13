/** Контракт способу оплати квитків. */
public interface PaymentMethod {

    String getName();

    double calculateFee(double amount);

    String process(double amount) throws InvalidTicketDataException;

    /** default-метод: інформація про комісію. */
    default String feeInfo(double amount) {
        return String.format("Комісія: %.2f грн", calculateFee(amount));
    }
}
