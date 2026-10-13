import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

void main() {

    Scanner scanner = new Scanner(System.in);
    scanner.useLocale(Locale.US); // дробові числа вводимо через крапку: 150.5

    try {
        System.out.print("Введіть назву фільму: ");
        String movieTitle = scanner.nextLine();

        int hallNumber = readInt(scanner, "Введіть номер залу: ");

        // Вводимо дані, доки вони не пройдуть доменну валідацію
        int ticketCount;
        double ticketPrice;
        double discountPercent;
        while (true) {
            ticketCount = readInt(scanner, "Введіть кількість квитків: ");
            ticketPrice = readDouble(scanner, "Введіть ціну одного квитка (грн): ");
            discountPercent = readDouble(scanner, "Введіть знижку (%): ");
            try {
                validate(ticketCount, ticketPrice, discountPercent);
                break; // дані коректні
            } catch (InvalidTicketDataException e) { // власний виняток
                System.out.println("Помилка в полі \"" + e.getFieldName() + "\": " + e.getMessage()
                        + " (введено: " + e.getInvalidValue() + "). Введіть дані ще раз.");
            }
        }

        boolean is3D = readYesNo(scanner, "Це 3D-сеанс? (так/ні): ");

        // Обчислення (без змін відносно ЛР1)
        final double surcharge3D = 30.0;

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

        // Нова функція: поділ рахунку між відвідувачами (цілочисельне ділення в копійках)
        int people = readInt(scanner, "\nНа скількох людей розділити оплату? ");
        try {
            if (people < 0) {
                throw new InvalidTicketDataException(
                        "Кількість людей не може бути від'ємною", "people", people);
            }
            long totalKop = Math.round(totalPrice * 100);
            long perPersonKop = totalKop / people; // при people == 0 -> ArithmeticException
            System.out.printf("З кожного: %d.%02d грн%n", perPersonKop / 100, perPersonKop % 100);
        } catch (ArithmeticException e) {
            System.out.println("Ділити оплату на 0 людей неможливо. Вкажіть кількість людей більше нуля.");
        } catch (InvalidTicketDataException e) {
            System.out.println("Помилка в полі \"" + e.getFieldName() + "\": " + e.getMessage()
                    + " (введено: " + e.getInvalidValue() + ")");
        }

    } finally {
        // виконується завжди
        scanner.close();
        System.out.println("Роботу завершено, Scanner закрито.");
    }
}

// Доменні правила: кидаємо власний виняток
void validate(int ticketCount, double ticketPrice, double discountPercent)
        throws InvalidTicketDataException {
    if (ticketCount <= 0) {
        throw new InvalidTicketDataException(
                "кількість квитків має бути більше нуля", "ticketCount", ticketCount);
    }
    if (ticketPrice <= 0) {
        throw new InvalidTicketDataException(
                "ціна має бути більше нуля", "ticketPrice", ticketPrice);
    }
    if (discountPercent < 0 || discountPercent > 100) {
        throw new InvalidTicketDataException(
                "знижка має бути в межах від 0 до 100", "discountPercent", discountPercent);
    }
}

// Читає ціле число; при некоректному вводі просить повторити
int readInt(Scanner scanner, String prompt) {
    while (true) {
        System.out.print(prompt);
        try {
            int value = scanner.nextInt();
            scanner.nextLine(); // з'їсти залишок рядка
            return value;
        } catch (InputMismatchException e) {
            System.out.println("Некоректне введення: потрібно ціле число. Спробуйте ще раз.");
            scanner.nextLine(); // викинути помилковий ввід
        }
    }
}

// Те саме для дробових чисел
double readDouble(Scanner scanner, String prompt) {
    while (true) {
        System.out.print(prompt);
        try {
            double value = scanner.nextDouble();
            scanner.nextLine();
            return value;
        } catch (InputMismatchException e) {
            System.out.println("Некоректне введення: потрібне число (наприклад, 150.5). Спробуйте ще раз.");
            scanner.nextLine();
        }
    }
}

// Читає "так"/"ні"
boolean readYesNo(Scanner scanner, String prompt) {
    while (true) {
        System.out.print(prompt);
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("так")) return true;
        if (answer.equalsIgnoreCase("ні")) return false;
        System.out.println("Введіть \"так\" або \"ні\".");
    }
}
