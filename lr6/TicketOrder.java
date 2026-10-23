import java.util.ArrayList;
import java.util.List;

/**
 * Композиція: замовлення МАЄ список квитків, але НЕ Є списком.
 * "TicketOrder extends ArrayList<Ticket>" було б хибним "is-a":
 * відкрило б clear(), remove(), sort() тощо і зробило б замовлення
 * залежним від внутрішньої структури списку.
 */
public class TicketOrder {

    private final List<Ticket> tickets = new ArrayList<>();

    public void add(Ticket ticket) {
        tickets.add(ticket);
    }

    public int count() {
        return tickets.size();
    }

    public double total() {
        double sum = 0;
        for (Ticket t : tickets) {
            sum += t.calculatePrice();
        }
        return sum;
    }

    /** Лише для читання: зовні список змінити не можна. */
    public List<Ticket> getTickets() {
        return List.copyOf(tickets);
    }
}
