/** Клас реалізує два інтерфейси: PaymentMethod (через AbstractPayment) та Refundable. */
public class CardPayment extends AbstractPayment implements Refundable {

    private static final double FEE_PERCENT = 1.5;

    public CardPayment() {
        super("Банківська картка");
    }

    @Override
    public double calculateFee(double amount) {
        return amount * FEE_PERCENT / 100.0;
    }

    @Override
    protected String doPay(double total) {
        return "Кошти списано з картки";
    }

    @Override
    public double refund(double paidAmount) {
        return paidAmount;
    }
}
