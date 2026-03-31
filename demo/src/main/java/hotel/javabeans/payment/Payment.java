package hotel.javabeans.payment;

import java.time.LocalDate;

public abstract class Payment {
    protected String paymentId;
    protected double amountPaid;
    protected LocalDate paymentDate;
    protected PaymentStatus paymentStatus;

    public Payment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus) {
        setPaymentId(paymentId);
        setAmountPaid(amountPaid);
        setPaymentDate(paymentDate);
        setPaymentStatus(paymentStatus);
        
        // try {
        //     setPaymentId(paymentId);
        //     setAmountPaid(amountPaid);
        //     setPaymentDate(paymentDate);
        //     setPaymentStatus(paymentStatus);
        // } catch (IllegalArgumentException e) {
        //     // throw new PaymentException("Payment validation failed: " + e.getMessage());
        // }
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public void setPaymentId(String paymentId) {
        if(paymentId != null && !paymentId.isEmpty()) {
            this.paymentId = paymentId;
        } else {
            throw new IllegalArgumentException("Payment ID cannot be null or empty.");
        }
    }

    public void setAmountPaid(double amountPaid) {
        if(amountPaid >= 0) {
            this.amountPaid = amountPaid;
        } else {
            throw new IllegalArgumentException("Amount paid cannot be negative.");
        }
    }

    public void setPaymentDate(LocalDate paymentDate) {
        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date cannot be null.");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Payment date cannot be in the future.");
        }
        this.paymentDate = paymentDate;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus != null) {
            this.paymentStatus = paymentStatus;
        } else {
            throw new IllegalArgumentException("Payment status cannot be null.");
        }
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
        return "Payment:" + '\n' +
                "--------------------------------------------" + '\n' +
                "paymentId: " + paymentId + '\n' +
                "amountPaid: " + amountPaid + '\n' +
                "paymentDate: " + getPaymentDate() + '\n' +
                "paymentStatus: " + paymentStatus + '\n' + 
                "--------------------------------------------";
    }
    
}
