/** Групова знижка: діє лише від певної кількості квитків. */
public class GroupDiscount implements DiscountStrategy {

    private static final int MIN_TICKETS = 5;
    private static final double PERCENT = 15.0;

    @Override
    public String getName() {
        return "Групова знижка";
    }

    @Override
    public double apply(double subtotal, int ticketCount) {
        if (ticketCount >= MIN_TICKETS) {
            return subtotal * (1 - PERCENT / 100.0);
        }
        return subtotal;
    }

    // перевизначення default-методу
    @Override
    public String describe() {
        return getName() + " (" + (int) PERCENT + "% від " + MIN_TICKETS + " квитків)";
    }
}
