/** 3D-квиток. Рефакторинг прапорця is3D з ЛР1: доплата тепер живе у своєму класі. */
public sealed class Ticket3D extends Ticket permits ImaxTicket {

    private static final double SURCHARGE_3D = 30.0;   // доплата за 3D (як у ЛР1)
    private static final double GLASSES_FEE = 10.0;    // оренда окулярів

    private final boolean glassesIncluded;

    public Ticket3D(String movieTitle, int hallNumber, double basePrice, boolean glassesIncluded) {
        super(movieTitle, hallNumber, basePrice);
        this.glassesIncluded = glassesIncluded;
        System.out.println("  [Ticket3D] окуляри = " + glassesIncluded);
    }

    public Ticket3D(String movieTitle, int hallNumber, double basePrice) {
        this(movieTitle, hallNumber, basePrice, true);
    }

    @Override
    public double calculatePrice() {
        return getBasePrice() + SURCHARGE_3D + (glassesIncluded ? GLASSES_FEE : 0.0);
    }

    @Override
    public String getFormat() {
        return "3D";
    }

    public boolean isGlassesIncluded() {
        return glassesIncluded;
    }

    @Override
    public String toString() {
        return super.toString() + " [окуляри=" + glassesIncluded + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Ticket3D t = (Ticket3D) o;
        return glassesIncluded == t.glassesIncluded;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Boolean.hashCode(glassesIncluded);
    }
}
