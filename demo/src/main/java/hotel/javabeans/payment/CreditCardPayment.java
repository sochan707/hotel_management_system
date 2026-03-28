package hotel.javabeans.payment;

import java.time.LocalDate;

public class CreditCardPayment extends Payment implements PaymentMethods {
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiryDate;

    public CreditCardPayment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus, String cardNumber, String cardHolderName, String cardExpiryDate) {
        super(paymentId, amountPaid, paymentDate, paymentStatus);
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
    public String toString() {
        return super.toString() + '\n' +
        "Card holder's Name: " + cardHolderName + '\n' +
        "Card expiry date: " + cardExpiryDate + '\n' +
        "Card Number: " + cardNumber;
    }
}
