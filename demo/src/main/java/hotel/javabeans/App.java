package hotel.javabeans;
import hotel.javabeans.payment.CreditCardPayment;
public class App 
{
    public static void main( String[] args )
    {
        CreditCardPayment payment = new CreditCardPayment(
            "P12345",             // paymentId
            100.0,                // amountPaid
            "2026-03-25",         // paymentDate
            "COMPLETED",          // paymentStatus (this matches an enum in PaymentStatus)
            "1234567812345678",   // cardNumber (example)
            "John Doe",           // cardHolderName
            "12/28"               // cardExpiryDate
        );

        // Process the payment
        payment.processPayment();

        // Validate the payment
        payment.validatePayment();

        // Calculate the total payment
        double total = payment.calculateTotal();
        System.out.println("Total Amount: $" + total);

        // Display payment details
        payment.displayPaymentDetails();
    }
}
    

