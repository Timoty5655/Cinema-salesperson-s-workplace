public class NoDiscount implements DiscountStrategy {

    @Override
    public String getName() {
        return "Без знижки";
    }

    @Override
    public double apply(double subtotal, int ticketCount) {
        return subtotal;
    }
}
