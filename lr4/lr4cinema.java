void main() {

    double ticketPrice = 150.0;

    // ---------- Рівень 1: поліморфізм (Strategy) ----------
    DiscountStrategy[] strategies = {
            new NoDiscount(),
            new PercentDiscount("Студентська знижка 20%", 20),
            new FixedDiscount("Промокод", 100),
            new GroupDiscount()
    };

    int[] counts = {3, 6};
    for (int ticketCount : counts) {
        double subtotal = ticketCount * ticketPrice;
        System.out.println("=== " + ticketCount + " квитків по " + ticketPrice
                + " грн, сума без знижки: " + subtotal + " грн ===");
        for (DiscountStrategy s : strategies) {
            System.out.printf("%-45s -> до сплати: %8.2f грн (знижка %.2f)%n",
                    s.describe(),
                    s.apply(subtotal, ticketCount),
                    s.discountAmount(subtotal, ticketCount));
        }
        System.out.println();
    }

    // ---------- Рівень 2: абстрактний клас + Template Method ----------
    int ticketCount = 6;
    double subtotal = ticketCount * ticketPrice;
    double toPay = strategies[1].apply(subtotal, ticketCount); // студентська знижка

    PaymentMethod[] methods = {
            new CashPayment(),
            new CardPayment(),
            new OnlinePayment()
    };

    System.out.printf("=== Оплата замовлення: %.2f грн ===%n", toPay);
    for (PaymentMethod m : methods) {
        try {
            System.out.println(m.process(toPay));
        } catch (InvalidTicketDataException e) {
            System.out.println("Помилка в полі \"" + e.getFieldName() + "\": " + e.getMessage());
        }
    }

    // власний виняток із ЛР3 всередині template method
    try {
        methods[0].process(0);
    } catch (InvalidTicketDataException e) {
        System.out.println("Перевірка в template method: " + e.getMessage()
                + " (введено: " + e.getInvalidValue() + ")");
    }

    // ---------- Рівень 2: кілька інтерфейсів в одного класу ----------
    System.out.println("\n=== Повернення коштів ===");
    for (PaymentMethod m : methods) {
        if (m instanceof Refundable r) {
            System.out.printf("%s: повернемо %.2f грн. %s%n",
                    m.getName(), r.refund(toPay), r.refundPolicy());
        } else {
            System.out.println(m.getName() + ": повернення не підтримується");
        }
    }
}
