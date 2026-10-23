/**
 * Simple Factory: єдине місце, де створюються способи оплати.
 * Клієнтський код працює лише з інтерфейсом PaymentMethod і не знає конкретних класів.
 */
public class PaymentFactory {

    private PaymentFactory() {
    }

    public static PaymentMethod create(String code) {
        return switch (code.trim().toLowerCase()) {
            case "cash" -> new CashPayment();
            case "card" -> new CardPayment();
            case "online" -> new OnlinePayment();
            case "bank" -> new BankGatewayPaymentAdapter(new LegacyBankGateway(), "**** 4242");
            // невідомий код - явна помилка, а не "тихий" null
            default -> throw new IllegalArgumentException("Невідомий спосіб оплати: " + code);
        };
    }
}
