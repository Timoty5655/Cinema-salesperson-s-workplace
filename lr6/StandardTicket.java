/** Звичайний 2D-квиток (дорослий або дитячий). */
public final class StandardTicket extends Ticket {

    private final boolean child;

    public StandardTicket(String movieTitle, int hallNumber, double basePrice, boolean child) {
        super(movieTitle, hallNumber, basePrice);
        this.child = child;
        System.out.println("  [StandardTicket] дитячий = " + child);
    }

    public StandardTicket(String movieTitle, int hallNumber, double basePrice) {
        this(movieTitle, hallNumber, basePrice, false);
    }

    @Override
    public double calculatePrice() {
        return child ? getBasePrice() * 0.5 : getBasePrice();
    }

    @Override
    public String getFormat() {
        return "2D";
    }

    public boolean isChild() {
        return child;
    }

    @Override
    public String toString() {
        return super.toString() + " [дитячий=" + child + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false; // перевіряє і клас, тому приведення безпечне
        StandardTicket t = (StandardTicket) o;
        return child == t.child;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Boolean.hashCode(child);
    }
}
