package hotel.javabeans.payment;

public class CashPayment extends Payment implements PaymentMethods{
    public CashPayment(String paymentId, double amountPaid, String paymentDate, String paymentStatus) {
        super(paymentId, amountPaid, paymentDate, PaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public void processPayment() {
        System.out.println("Processing cash payment of amount: $" + amountPaid);
    }

    @Override
    public void validatePayment() {
        System.out.println("Cash payment validated.");
    }

    @Override
    public double calculateTotal() {
        return amountPaid;
    }

    @Override
    public void displayPaymentDetails() {
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Amount Paid: $" + amountPaid);
        System.out.println("Payment Date: " + paymentDate);
        System.out.println("Payment Status: " + paymentStatus);
        System.out.println("Payment Method: Cash");
    }
}
