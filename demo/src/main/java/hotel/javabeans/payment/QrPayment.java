package hotel.javabeans.payment;

import java.time.LocalDate;

public class QrPayment extends Payment implements PaymentMethods {
    private final String accountNumber;
    private final String bank;

    public QrPayment(String paymentId, double amountPaid, LocalDate paymentDate, PaymentStatus paymentStatus, String accountNumber, String bank) {
        super(paymentId, amountPaid, paymentDate, paymentStatus);
        this.accountNumber = validateAccountNumber(accountNumber);
        this.bank = validateBank(bank);
    }

    private String validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty.");
        }

        // Trim any leading/trailing whitespace
        accountNumber = accountNumber.trim();

        // Check if it contains exactly 9 digits
        if (!accountNumber.matches("\\d{9}")) {
            throw new IllegalArgumentException("Account number must be exactly 9 digits.");
        }

        return accountNumber;
    }

    private String validateBank (String bank){
        if(bank == null || bank.isEmpty())
            throw new IllegalArgumentException("Bank cannot be empty!");

        return bank;
    }

    @Override
    public void processPayment() {
        System.out.println("Processing QR payment of amount: $" + amountPaid);
    }

    @Override
    public void validatePayment() {
        System.out.println("QR payment validated.");
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
