/**
 * Третій рівень ієрархії: Ticket -> Ticket3D -> ImaxTicket.
 * final: IMAX-квиток далі не уточнюється, підкласів у нього немає.
 */
public final class ImaxTicket extends Ticket3D {

    private static final double IMAX_SURCHARGE = 50.0;
    private static final double DEFAULT_IMAX_PRICE = 200.0;

    public ImaxTicket(String movieTitle, int hallNumber, double basePrice) {
        super(movieTitle, hallNumber, basePrice, true); // в IMAX окуляри завжди видаються
        System.out.println("  [ImaxTicket] квиток IMAX створено");
    }

    public ImaxTicket(String movieTitle, int hallNumber) {
        this(movieTitle, hallNumber, DEFAULT_IMAX_PRICE);
    }

    @Override
    public double calculatePrice() {
        return super.calculatePrice() + IMAX_SURCHARGE;
    }

    @Override
    public String getFormat() {
        return "IMAX";
    }

    @Override
    public String toString() {
        return super.toString() + " [IMAX, преміум-звук]";
    }
    // equals/hashCode успадковані від Ticket3D: нових полів немає, getClass() відрізняє IMAX від 3D
}
