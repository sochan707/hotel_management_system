package hotel.javabeans;
import java.time.LocalDate;

import hotel.javabeans.payment.CreditCardPayment;
import hotel.javabeans.payment.Payment.PaymentStatus;
public class App 
{
    public static void main( String[] args )
    {
        CreditCardPayment payment = new CreditCardPayment(
            "12345", 
            100.0, 
            LocalDate.now(), 
            PaymentStatus.COMPLETED,
            "4111111111111111", 
            "John Doe", 
            "12/25"
        );

        CreditCardPayment payment2Payment = new CreditCardPayment(
            "12345", 
            100.0, 
            LocalDate.now(), 
            PaymentStatus.COMPLETED,
            "4111111111111111", 
            "John Doe", 
            "12/25"
        );

        // Calculate the total payment
        // double total = payment.calculateTotal();
        // System.out.println("Total Amount: $" + total);

        // Display payment details
        System.out.println(payment);
    }
}
    

