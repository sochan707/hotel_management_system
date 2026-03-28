package hotel.javabeans.payment;

public class CreditCardPayment extends Payment implements PaymentMethods {
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiryDate;

    public CreditCardPayment(String paymentId, double amountPaid, String paymentDate, String paymentStatus, String cardNumber, String cardHolderName, String cardExpiryDate) {
        super(paymentId, amountPaid, paymentDate, PaymentStatus.valueOf(paymentStatus));
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.cardExpiryDate = cardExpiryDate;
    }

    @Override
    public void processPayment() {
        System.out.println("Processing credit card payment of amount: $" + amountPaid);
    }

    @Override
    public void validatePayment() {
        if (cardNumber != null && cardNumber.length() == 16) {
            System.out.println("Credit card validation successful.");
        } else {
            System.out.println("Invalid credit card number.");
        }
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
        System.out.println("Card Holder: " + cardHolderName);
        System.out.println("Card Expiry Date: " + cardExpiryDate);
    }
}
