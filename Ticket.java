public class Ticket {
    private String movieTitle;   // назва фільму
    private String sessionTime;  // час початку сеансу, напр. "18:30"
    private int hallNumber;      // номер залу
    private int seatNumber;      // номер місця
    private double price;        // ціна квитка, грн

    public Ticket(String movieTitle, String sessionTime, int hallNumber, int seatNumber, double price) {
        this.movieTitle = movieTitle;
        this.sessionTime = sessionTime;
        this.hallNumber = hallNumber;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("Квиток[фільм=\"%s\", сеанс=%s, зал=%d, місце=%d, ціна=%.2f грн]",
                movieTitle, sessionTime, hallNumber, seatNumber, price);
    }
}
