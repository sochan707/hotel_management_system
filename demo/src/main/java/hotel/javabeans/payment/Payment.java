package hotel.javabeans.payment;

public abstract class Payment {
    protected String paymentId;
    protected double amountPaid;
    protected String paymentDate;
    protected PaymentStatus paymentStatus;

    public Payment(String paymentId, double amountPaid, String paymentDate, PaymentStatus paymentStatus) {
        setPaymentId(paymentId);
        setAmountPaid(amountPaid);
        setPaymentDate(paymentDate);
        setPaymentStatus(paymentStatus);
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

    public void setPaymentDate(String paymentDate) {
        if(paymentDate != null && !paymentDate.isEmpty()) {
            this.paymentDate = paymentDate;
        } else {
            throw new IllegalArgumentException("Payment date cannot be null or empty."); //need to fix this later too
        }
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public abstract void processPayment();
    public abstract void displayPaymentDetails();
}
