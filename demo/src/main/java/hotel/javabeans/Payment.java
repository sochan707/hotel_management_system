package hotel.javabeans;

import java.time.LocalDate;
import java.util.Objects;

import hotel.javabeans.Payment.PaymentMethod;
import hotel.javabeans.Payment.PaymentStatus;

public class Payment {
    private String paymentId;
    private double amountPaid;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private PaymentStatus paymentStatus;

    public Payment( // data dea ke required
            String paymentId,
            double amountPaid,
            PaymentMethod paymentMethod,
            LocalDate paymentDate,
            PaymentStatus paymentStatus
    ) { // add validation 
        setPaymentId(paymentId);
        setAmountPaid(amountPaid);
        setPaymentMethod(paymentMethod);
        setPaymentDate(paymentDate);
        setPaymentStatus(paymentStatus);
    }

    public enum PaymentMethod { 
        CASH, CARD, ONLINE
    }

    public enum PaymentStatus {
        PAID, PENDING, FAILED
    }

    public void setPaymentId(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }
        this.paymentId = paymentId;
    }

    public void setAmountPaid(double amountPaid) {
        if (amountPaid < 0) {
            throw new IllegalArgumentException("Amount paid cannot be negative.");
        }
        this.amountPaid = amountPaid;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = Objects.requireNonNull(
                paymentMethod, "Payment method cannot be null."
        );
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = Objects.requireNonNull(
                paymentDate, "Payment date cannot be null."
        );
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        this.paymentStatus = Objects.requireNonNull(
                paymentStatus, "Payment status cannot be null."
        );
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public double getAmountPaid() {
        return amountPaid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}