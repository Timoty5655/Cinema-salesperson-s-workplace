/** Навмисно "зламаний" спостерігач: показує, що виняток одного не ламає інших. */
public class FaultyObserver implements OrderObserver {

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        throw new IllegalStateException("помилка в спостерігачі");
    }
}
