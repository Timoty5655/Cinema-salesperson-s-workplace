/** Спостерігач 3: звітність - рахує виручку. */
public class SalesReport implements OrderObserver {

    private double revenue = 0.0;

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.PAID) {
            revenue += order.getTotal();
        } else if (newStatus == OrderStatus.CANCELLED && oldStatus == OrderStatus.PAID) {
            revenue -= order.getTotal();
        }
    }

    public double getRevenue() {
        return revenue;
    }
}
