import java.util.ArrayList;
import java.util.List;

/**
 * Subject: замовлення на квитки. Зберігає список підписників
 * і сповіщає їх при зміні статусу. Про конкретні класи спостерігачів нічого не знає.
 */
public class Order {

    private final int id;
    private final String movieTitle;
    private final int ticketCount;
    private final double total;
    private OrderStatus status = OrderStatus.NEW;
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(int id, String movieTitle, int ticketCount, double total) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.ticketCount = ticketCount;
        this.total = total;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    public void changeStatus(OrderStatus newStatus) {
        if (newStatus == status) {
            return; // нічого не змінилось - нікого не сповіщаємо
        }
        OrderStatus oldStatus = status;
        status = newStatus;
        notifyObservers(oldStatus, newStatus);
    }

    private void notifyObservers(OrderStatus oldStatus, OrderStatus newStatus) {
        // копія списку: спостерігач може відписатися під час сповіщення
        for (OrderObserver observer : new ArrayList<>(observers)) {
            try {
                observer.onStatusChanged(this, oldStatus, newStatus);
            } catch (RuntimeException e) {
                // помилка одного спостерігача не повинна зламати решту
                System.out.println("[Order] Спостерігач " + observer.getClass().getSimpleName()
                        + " викинув виняток: " + e.getMessage());
            }
        }
    }

    public int getId() { return id; }
    public String getMovieTitle() { return movieTitle; }
    public int getTicketCount() { return ticketCount; }
    public double getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
}
