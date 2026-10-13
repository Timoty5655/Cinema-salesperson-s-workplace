/** Знижка у відсотках (студентська, дитяча, пенсійна тощо). */
public class PercentDiscount implements DiscountStrategy {

    private final String name;
    private final double percent;

    public PercentDiscount(String name, double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Відсоток знижки має бути в межах 0..100");
        }
        this.name = name;
        this.percent = percent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double apply(double subtotal, int ticketCount) {
        return subtotal * (1 - percent / 100.0);
    }
}
