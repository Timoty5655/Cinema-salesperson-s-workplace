public class CashPayment extends AbstractPayment {

    public CashPayment() {
        super("Готівка");
    }

    @Override
    public double calculateFee(double amount) {
        return 0.0;
    }

    @Override
    protected String doPay(double total) {
        return "Готівку прийнято в касі";
    }

    @Override
    public String feeInfo(double amount) {
        return "Без комісії";
    }
}
