void main() {

    double ticketPrice = 150.0;
    int ticketCount = 6;
    double subtotal = ticketCount * ticketPrice;

    // ================= Рівень 1: Factory =================
    System.out.println("=== 1. Factory: знижки ===");
    String[] discountCodes = {"none", "student", "promo", "group"};
    for (String code : discountCodes) {
        DiscountStrategy d = DiscountFactory.create(code);
        System.out.printf("%-8s -> %-40s до сплати: %.2f грн%n",
                code, d.describe(), d.apply(subtotal, ticketCount));
    }

    double toPay = DiscountFactory.create("student").apply(subtotal, ticketCount);

    System.out.printf("%n=== 1. Factory: способи оплати (сума %.2f грн) ===%n", toPay);
    String[] paymentCodes = {"cash", "card", "online", "bank"};
    for (String code : paymentCodes) {
        try {
            PaymentMethod method = PaymentFactory.create(code);
            System.out.println(method.process(toPay));
        } catch (InvalidTicketDataException e) {
            System.out.println("Помилка в полі \"" + e.getFieldName() + "\": " + e.getMessage());
        }
    }

    try {
        PaymentFactory.create("bitcoin");
    } catch (IllegalArgumentException e) {
        System.out.println("Невідомий код: " + e.getMessage());
    }

    // ================= Рівень 3: Adapter =================
    System.out.println("\n=== 3. Adapter: сторонній шлюз через наш інтерфейс ===");
    PaymentMethod bank = PaymentFactory.create("bank");
    try {
        System.out.println(bank.process(720.0));
        System.out.println(bank.process(60000.0)); // перевищує ліміт шлюзу
    } catch (InvalidTicketDataException e) {
        System.out.println("Помилка: " + e.getMessage());
    }

    // ================= Рівень 2: Observer =================
    System.out.println("\n=== 2. Observer: зміна статусу замовлення ===");
    Order order = new Order(1, "Marvel", ticketCount, toPay);

    SalesReport report = new SalesReport();
    OrderObserver faulty = new FaultyObserver();
    order.addObserver(new OrderLogger());
    order.addObserver(faulty);          // кидає виняток, але інші все одно отримають сповіщення
    order.addObserver(new SmsNotifier());
    order.addObserver(report);

    order.changeStatus(OrderStatus.PAID);
    System.out.println("Виручка за звітом: " + report.getRevenue() + " грн");

    System.out.println("\n--- відписуємо зламаного спостерігача ---");
    order.removeObserver(faulty);
    order.changeStatus(OrderStatus.CANCELLED);
    System.out.println("Виручка за звітом: " + report.getRevenue() + " грн");
}
