import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Скільки квитків продано (розмір масиву)? ");
        int n = readInt(scanner);
        Ticket[] tickets = new Ticket[n];

        for (int i = 0; i < tickets.length; i++) {
            System.out.println("\n--- Квиток №" + (i + 1) + " ---");

            System.out.print("Назва фільму: ");
            String title = scanner.nextLine();

            System.out.print("Час сеансу (наприклад, 18:30): ");
            String time = scanner.nextLine();

            System.out.print("Номер залу: ");
            int hall = readInt(scanner);

            System.out.print("Номер місця: ");
            int seat = readInt(scanner);

            System.out.print("Ціна, грн: ");
            double price = readDouble(scanner);

            tickets[i] = new Ticket(title, time, hall, seat, price);
        }

        System.out.println("\n===== Усі продані квитки =====");
        for (Ticket t : tickets) {
            System.out.println(t);
        }

        System.out.print("\nПорахувати квитки дешевші за (грн): ");
        double limit = readDouble(scanner);

        int count = 0;
        for (Ticket t : tickets) {
            if (t.getPrice() < limit) {
                count++;
            }
        }
        System.out.println("Квитків дешевших за " + limit + " грн: " + count);

        System.out.println("\n===== Масив ДО сортування =====");
        printArray(tickets);

        bubbleSortByPrice(tickets);

        System.out.println("\n===== Масив ПІСЛЯ сортування за ціною (за зростанням) =====");
        printArray(tickets);

        scanner.close();
    }

    static void bubbleSortByPrice(Ticket[] arr) {
        for (int pass = 0; pass < arr.length - 1; pass++) {
            boolean swapped = false;
            // після кожного проходу останні pass елементів уже на своїх місцях
            for (int j = 0; j < arr.length - 1 - pass; j++) {
                if (arr[j].getPrice() > arr[j + 1].getPrice()) {
                    Ticket tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // обмінів не було — масив уже відсортовано
            }
        }
    }

    static void printArray(Ticket[] arr) {
        for (Ticket t : arr) {
            System.out.println(t);
        }
    }

    static int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введіть ціле число: ");
            }
        }
    }

    static double readDouble(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.print("Введіть число (наприклад, 150.50): ");
            }
        }
    }
}
