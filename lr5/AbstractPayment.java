/**
 * Абстрактний клас + інтерфейс: частково реалізує PaymentMethod.
 * Template Method: process() задає незмінний алгоритм оплати
 * (перевірка -> комісія -> проведення платежу -> підсумок),
 * а конкретні кроки (calculateFee, doPay) визначають підкласи.
 *
 * Абстрактний клас доречний, бо є спільний стан (name) і спільна логіка (process).
 */
public abstract class AbstractPayment implements PaymentMethod {

    private final String name;

    protected AbstractPayment(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final String process(double amount) throws InvalidTicketDataException {
        if (amount <= 0) {
            throw new InvalidTicketDataException(
                    "сума оплати має бути більше нуля", "amount", amount);
        }
        double fee = calculateFee(amount);
        double total = amount + fee;
        String details = doPay(total);
        return String.format("[%s] %s | %s | Разом: %.2f грн",
                name, details, feeInfo(amount), total);
    }

    /** Крок, який реалізує кожен спосіб оплати по-своєму. */
    protected abstract String doPay(double total);
}
