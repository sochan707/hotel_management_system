package hotel.javabeans.payment;

import java.time.LocalDate;

public class CreditCardPayment extends Payment implements PaymentMethods {
    private final String cardNumber;
    private final String cardHolderName;
    private final String cardExpiryDate;

    public CreditCardPayment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus, String cardNumber, String cardHolderName, String cardExpiryDate) {
        super(paymentId, amountPaid, paymentDate, paymentStatus);
        this.cardNumber = validationCardNumber(cardNumber);
        this.cardHolderName = validateCardHolderName(cardHolderName);
        this.cardExpiryDate = validateCardExpiryDate(cardExpiryDate);
    }

    private String validationCardNumber(String cardNumber){
        if(cardNumber == null || cardNumber.isEmpty() || cardNumber.length() != 10)
            throw new IllegalArgumentException("Invalid card number!");
        return cardNumber;
    }

    private String validateCardHolderName(String cardHolderName){
        if(cardHolderName == null || cardHolderName.isEmpty() || cardHolderName.length() < 5)
            throw new IllegalArgumentException("Invalid card holder name!");
        return cardHolderName;
    }

    private String validateCardExpiryDate (String cardExpiryDate) {
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

        return cardExpiryDate;
    }

    @Override
    public void processPayment() {
        System.out.println("Processing credit card payment of amount: $" + amountPaid);
    }

    @Override
    public void validatePayment() {
        if (cardNumber != null && cardNumber.length() == 10) {
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
        return String.format("%s%nCard holder's Name: %s%nCard expiry date: %s%nCard Number: %s%n", 
                             super.toString(), cardHolderName, cardExpiryDate, cardNumber);
    }
}
