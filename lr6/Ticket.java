import java.util.Objects;

/**
 * Базовий клас ієрархії квитків. Абстрактний: "квиток взагалі" не існує,
 * є квиток конкретного формату (2D, 3D, IMAX).
 * Запечатаний (sealed): дозволені лише StandardTicket і Ticket3D.
 */
public abstract sealed class Ticket permits StandardTicket, Ticket3D {

    public static final double DEFAULT_PRICE = 120.0;

    // private: підкласи працюють із полями лише через getter-и,
    // тому базовий клас сам контролює свої інваріанти (ціна > 0)
    private final String movieTitle;
    private final int hallNumber;
    private final double basePrice;

    // скорочений конструктор делегує повному через this(...)
    protected Ticket(String movieTitle, int hallNumber) {
        this(movieTitle, hallNumber, DEFAULT_PRICE);
    }

    protected Ticket(String movieTitle, int hallNumber, double basePrice) {
        if (basePrice <= 0) {
            throw new IllegalArgumentException("Ціна має бути більше нуля: " + basePrice);
        }
        this.movieTitle = movieTitle;
        this.hallNumber = hallNumber;
        this.basePrice = basePrice;
        System.out.println("  [Ticket] ініціалізовано базові поля: " + movieTitle + ", зал " + hallNumber);
    }

    /**
     * Абстрактний метод: правило ціни повністю залежить від формату,
     * "загальної" реалізації в базовому класі бути не може.
     */
    public abstract double calculatePrice();

    /** Абстрактний метод: назва формату. */
    public abstract String getFormat();

    /**
     * final: правило формування коду квитка однакове для всіх видів,
     * підкласи не повинні його змінювати (код використовується як ідентифікатор).
     */
    public final String getTicketCode() {
        return "H" + hallNumber + "-" + movieTitle.toUpperCase().replace(' ', '_');
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{фільм='" + movieTitle + "', зал=" + hallNumber
                + ", базова ціна=" + basePrice + "}";
    }

    // getClass(), а не instanceof: рівними можуть бути лише квитки одного й того ж класу
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket t = (Ticket) o;
        return hallNumber == t.hallNumber
                && Double.compare(basePrice, t.basePrice) == 0
                && movieTitle.equals(t.movieTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieTitle, hallNumber, basePrice);
    }
}
