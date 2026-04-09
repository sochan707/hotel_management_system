package hotel.javabeans.payment;

import java.time.LocalDate;

public abstract class Payment {
    protected String paymentId;
    protected double amountPaid;
    protected LocalDate paymentDate;
    protected PaymentStatus paymentStatus;

    public Payment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus) {
        this.paymentId = validateString(paymentId);
        this.amountPaid = validateAmountPay(amountPaid);
        this.paymentDate = validateDate(paymentDate);
        this.paymentStatus = validatePaymentStatus(paymentStatus);
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    private String validateString(String value) {
        if (value == null || value.isEmpty()) 
            throw new IllegalArgumentException("Payment ID cannot be null or empty.");
        return value;
    }

    private double validateAmountPay(double amount){
        if (amount < 0) 
            throw new IllegalArgumentException("Amount paid cannot be negative.");
        return amount;
    }

    private LocalDate validateDate(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) 
            throw new IllegalArgumentException("Invalid payment date.");
        return date;
    }

    private PaymentStatus validatePaymentStatus(PaymentStatus paymentStatus){
        if (paymentStatus == null) 
            throw new IllegalArgumentException("Payment status cannot be null.");
        return paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }
    
    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public abstract void processPayment();    

    @Override
    public String toString() {
        return String.format("Payment:%n--------------------------------------------%npaymentId: %s%namountPaid: %.2f%npaymentDate: %s%npaymentStatus: %s%n--------------------------------------------",
                             paymentId, amountPaid, paymentDate, paymentStatus);
    }
    
}
