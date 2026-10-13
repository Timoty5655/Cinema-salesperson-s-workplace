import java.util.Scanner;

void main() {

Scanner scanner = new Scanner(System.in);

    System.out.print("Введіть назву фільму: ");
    String movieTitle = scanner.nextLine();

    System.out.print("Введіть номер залу: ");
    int hallNumber = scanner.nextInt();

    System.out.print("Введіть кількість квитків: ");
    int ticketCount = scanner.nextInt();

    System.out.print("Введіть ціну одного квитка (грн): ");
    double ticketPrice  = scanner.nextDouble();

    System.out.print("Введіть знижку (%): ");
    double discountPercent   = scanner.nextDouble();

    System.out.print("Це 3D-сеанс? (true/false): ");
    boolean is3D = scanner.nextBoolean();

    // 2. Обчислювальна операція:
    //    підсумкова вартість з урахуванням знижки
    final double surcharge3D = 30.0; // доплата за один 3D-квиток, грн
 
    double subtotal = ticketCount * ticketPrice;
    double surchargeAmount = is3D ? ticketCount * surcharge3D : 0.0;
    double subtotalWithSurcharge = subtotal + surchargeAmount;
    double discountAmount = subtotalWithSurcharge * discountPercent / 100.0;
    double totalPrice = subtotalWithSurcharge - discountAmount;

    System.out.println();
    System.out.println("========= Чек кінотеатру =========");
    System.out.printf("Фільм: %s%n", movieTitle);
    System.out.printf("Зал: %d%n", hallNumber);
    System.out.printf("Кількість квитків: %d%n", ticketCount);
    System.out.printf("Ціна одного квитка: %.2f грн%n", ticketPrice);
    System.out.printf("Формат: %s%n", is3D ? "3D" : "2D");
    System.out.printf("Сума за квитки: %.2f грн%n", subtotal);
    if (is3D) {
        System.out.printf("Доплата за 3D: %.2f грн%n", surchargeAmount);
    }
    System.out.printf("Сума перед знижкою: %.2f грн%n", subtotalWithSurcharge);
    System.out.printf("Знижка: %.2f%% (-%.2f грн)%n", discountPercent, discountAmount);
    System.out.printf("До сплати: %.2f грн%n", totalPrice);
    System.out.println("===================================");

    scanner.close();
}