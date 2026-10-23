/** Simple Factory для стратегій знижок. */
public class DiscountFactory {

    private DiscountFactory() {
    }

    public static DiscountStrategy create(String code) {
        return switch (code.trim().toLowerCase()) {
            case "none" -> new NoDiscount();
            case "student" -> new PercentDiscount("Студентська знижка 20%", 20);
            case "promo" -> new FixedDiscount("Промокод", 100);
            case "group" -> new GroupDiscount();
            default -> throw new IllegalArgumentException("Невідомий тип знижки: " + code);
        };
    }
}
