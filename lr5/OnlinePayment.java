/** Онлайн-оплата: комісія з фіксованою частиною, часткове повернення. */
public class OnlinePayment extends AbstractPayment implements Refundable {

    private static final double FEE_PERCENT = 2.0;
    private static final double FIXED_FEE = 5.0;

    public OnlinePayment() {
        super("Онлайн-оплата");
    }

    @Override
    public double calculateFee(double amount) {
        return amount * FEE_PERCENT / 100.0 + FIXED_FEE;
    }

    @Override
    protected String doPay(double total) {
        return "Платіж проведено через платіжний сервіс";
    }

    @Override
    public double refund(double paidAmount) {
        return paidAmount - FIXED_FEE; // фіксована комісія не повертається
    }

    // перевизначення default-методу
    @Override
    public String refundPolicy() {
        return "Повернення без фіксованої комісії " + FIXED_FEE + " грн";
    }
}
