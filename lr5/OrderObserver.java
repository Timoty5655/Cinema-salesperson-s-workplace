/** Observer: усі, хто хоче знати про зміну статусу замовлення, реалізують цей інтерфейс. */
public interface OrderObserver {
    void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}
