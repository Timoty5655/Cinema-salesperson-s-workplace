/** Фіксована знижка в гривнях (промокод). Сума не може стати від'ємною. */
public class FixedDiscount implements DiscountStrategy {

    private final String name;
    private final double amount;

    public FixedDiscount(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double apply(double subtotal, int ticketCount) {
        return Math.max(0.0, subtotal - amount);
    }

    // перевизначення default-методу
    @Override
    public String describe() {
        return name + " (-" + String.format("%.2f", amount) + " грн)";
    }
}
