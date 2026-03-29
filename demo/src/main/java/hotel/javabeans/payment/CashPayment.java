package hotel.javabeans.payment;

import java.time.LocalDate;

public class CashPayment extends Payment implements PaymentMethods{
    public CashPayment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus) {
        super(paymentId, amountPaid, paymentDate, paymentStatus);
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
    public String toString() {
        return super.toString();
    }

    
    
}
