/** Спостерігач 2: сповіщення клієнта. */
public class SmsNotifier implements OrderObserver {

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.PAID) {
            System.out.println("[SMS] Замовлення #" + order.getId() + " оплачено. Фільм: "
                    + order.getMovieTitle() + ", квитків: " + order.getTicketCount());
        } else if (newStatus == OrderStatus.CANCELLED) {
            System.out.println("[SMS] Замовлення #" + order.getId() + " скасовано.");
        }
    }
}
