/**
 * Adapter: "перекладає" очікуваний нашим кодом інтерфейс PaymentMethod
 * у виклики несумісного LegacyBankGateway (композиція, шлюз не змінюється).
 *
 * Що перекладається:
 *  - гривні (double)  ->  копійки (int);
 *  - makeTransaction / calcCommissionKop  ->  doPay / calculateFee;
 *  - числовий код результату  ->  зрозумілий текст.
 */
public class BankGatewayPaymentAdapter extends AbstractPayment {

    private final LegacyBankGateway gateway; // адаптований клас (композиція)
    private final String cardMask;

    public BankGatewayPaymentAdapter(LegacyBankGateway gateway, String cardMask) {
        super("Банківський шлюз");
        this.gateway = gateway;
        this.cardMask = cardMask;
    }

    @Override
    public double calculateFee(double amount) {
        return gateway.calcCommissionKop(toKop(amount)) / 100.0;
    }

    @Override
    protected String doPay(double total) {
        int code = gateway.makeTransaction(toKop(total), cardMask);
        if (code == 0) {
            return "Шлюз підтвердив транзакцію (картка " + cardMask + ")";
        }
        return "Шлюз ВІДХИЛИВ транзакцію: " + gateway.getLastError();
    }

    private int toKop(double uah) {
        return (int) Math.round(uah * 100);
    }
}
