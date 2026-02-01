package hotel.javabeans;

public class Payment {
    private String paymentId;
    private double amountPaid;
    private String paymentMethod;
    private String paymentDate;
    private String paymentStatus;

    public Payment(String paymentId, double amountPaid, String paymentMethod, String paymentDate, String paymentStatus) {
        setPaymentId(paymentId);
        setAmountPaid(amountPaid);
        setPaymentMethod(paymentMethod);
        setPaymentDate(paymentDate);
        setPaymentStatus(paymentStatus);
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

    public void setPaymentMethod(String paymentMethod) {
        if(paymentMethod != null && !paymentMethod.isEmpty()) {
            this.paymentMethod = paymentMethod;
        } else {
            throw new IllegalArgumentException("Payment method cannot be null or empty."); //need to fix this later
        }
    }

    public void setPaymentDate(String paymentDate) {
        if(paymentDate != null && !paymentDate.isEmpty()) {
            this.paymentDate = paymentDate;
        } else {
            throw new IllegalArgumentException("Payment date cannot be null or empty."); //need to fix this later too
        }
    }

    public void setPaymentStatus(String paymentStatus) { //DEFINE PAYMENT STATUS LATER CUZ I DONT KNOW WHAT IT IS RN
        this.paymentStatus = paymentStatus;
    }
}
