/** Спостерігач 1: журнал подій. */
public class OrderLogger implements OrderObserver {

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        System.out.println("[LOG] Замовлення #" + order.getId() + ": " + oldStatus + " -> " + newStatus);
    }
}
