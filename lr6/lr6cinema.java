void main() {

    // ---------- 1. Ланцюжок конструкторів (3 рівні) ----------
    System.out.println("=== 1. Створення ImaxTicket: порядок конструкторів ===");
    new ImaxTicket("Dune", 3);   // Ticket -> Ticket3D -> ImaxTicket

    // ---------- 2. Поліморфізм ----------
    System.out.println("\n=== 2. Поліморфізм: масив типу Ticket ===");
    Ticket[] tickets = {
            new StandardTicket("Marvel", 2, 150.0),
            new StandardTicket("Marvel", 2, 150.0, true),
            new Ticket3D("Marvel", 2, 150.0, true),
            new ImaxTicket("Dune", 3, 200.0)
    };

    System.out.println();
    for (Ticket t : tickets) {
        System.out.printf("%-6s код %-14s ціна: %7.2f грн%n   %s%n",
                t.getFormat(), t.getTicketCode(), t.calculatePrice(), t);
    }

    // ---------- 3. instanceof, статичний і динамічний тип ----------
    System.out.println("\n=== 3. instanceof та статичний/динамічний тип ===");
    for (Ticket t : tickets) {
        // статичний тип змінної t - Ticket; динамічний - те, що повертає getClass()
        System.out.print("статичний: Ticket, динамічний: " + t.getClass().getSimpleName());
        if (t instanceof Ticket3D t3) {
            System.out.println(", окуляри: " + t3.isGlassesIncluded());
        } else if (t instanceof StandardTicket s) {
            System.out.println(", дитячий: " + s.isChild());
        } else {
            System.out.println();
        }
    }

    // ---------- 4. sealed + switch із pattern matching ----------
    System.out.println("\n=== 4. sealed + switch (без default) ===");
    for (Ticket t : tickets) {
        System.out.println(t.getTicketCode() + " -> " + describe(t));
    }

    // ---------- 5. equals / hashCode ----------
    System.out.println("\n=== 5. equals і hashCode ===");
    Ticket a = new Ticket3D("Marvel", 2, 150.0, true);
    Ticket b = new Ticket3D("Marvel", 2, 150.0, true);
    Ticket c = new StandardTicket("Marvel", 2, 150.0);
    System.out.println("a.equals(b) = " + a.equals(b) + ", hash однакові: " + (a.hashCode() == b.hashCode()));
    System.out.println("c.equals(a) = " + c.equals(a) + ", a.equals(c) = " + a.equals(c)
            + "   (getClass(): симетрія збережена)");
    EqualsDemo.run();

    // ---------- 6. Композиція ----------
    System.out.println("\n=== 6. Композиція: TicketOrder ===");
    TicketOrder order = new TicketOrder();
    for (Ticket t : tickets) {
        order.add(t);
    }
    System.out.printf("У замовленні %d квитків, разом: %.2f грн%n", order.count(), order.total());

    // ---------- Валідація ----------
    System.out.println("\n=== Некоректна ціна ===");
    try {
        new StandardTicket("Test", 1, -5.0);
    } catch (IllegalArgumentException e) {
        System.out.println("Помилка: " + e.getMessage());
    }
}

// switch без default: компілятор знає всіх нащадків завдяки sealed.
// Порядок важливий: ImaxTicket має стояти перед Ticket3D.
String describe(Ticket t) {
    return switch (t) {
        case StandardTicket s -> s.isChild() ? "2D дитячий" : "2D дорослий";
        case ImaxTicket i -> "IMAX із преміум-звуком";
        case Ticket3D d -> "3D" + (d.isGlassesIncluded() ? " з окулярами" : " без окулярів");
    };
}
