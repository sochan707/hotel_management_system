package hotel.javabeans.payment;

import java.time.LocalDate;

public class CreditCardPayment extends Payment implements PaymentMethods {
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiryDate;

    public CreditCardPayment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus, String cardNumber, String cardHolderName, String cardExpiryDate) {
        super(paymentId, amountPaid, paymentDate, paymentStatus);
        setCardNumber(cardNumber);
        setCardHolderName(cardHolderName);
        this.cardExpiryDate = cardExpiryDate;
    }

    public void setCardNumber(String cardNumber) {
        if(cardNumber == null || cardNumber.isEmpty() || cardNumber.length() < 15){
            throw new IllegalArgumentException("Invalid card number!");
        } else {
            this.cardNumber = cardNumber;
        }
    }

    public void setCardHolderName(String cardHolderName) {
        if(cardHolderName.isEmpty() && cardHolderName.length() < 15){
            throw new IllegalArgumentException("Invalid input!");
        } else {
            this.cardHolderName = cardHolderName;
        }
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        if (cardExpiryDate == null || !cardExpiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("Invalid expiry date format! Use MM/YY.");
        }

        String[] parts = cardExpiryDate.split("/");
        int month = Integer.parseInt(parts[0]);  // .parseInt() => convert string to int
        int year = Integer.parseInt(parts[1]) + 2000;

        LocalDate now = LocalDate.now();
        LocalDate expiry = LocalDate.of(year, month, 1).withDayOfMonth(
            LocalDate.of(year, month, 1).lengthOfMonth()
        );

        if (expiry.isBefore(now)) {
            throw new IllegalArgumentException("Card has expired!");
        }

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
