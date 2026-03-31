package hotel.javabeans;
import java.time.LocalDate;

import hotel.javabeans.payment.CreditCardPayment;
import hotel.javabeans.payment.Payment.PaymentStatus;
public class App 
{
    public static void main( String[] args )
    {
        CreditCardPayment payment = new CreditCardPayment(
            "00000", 
            100.0, 
            LocalDate.now(), 
            PaymentStatus.COMPLETED,
            "0000011112", 
            "thamus", 
            "12/29"
        );

        CreditCardPayment payment2 = new CreditCardPayment(
            "12345", 
            100.0, 
            LocalDate.now(), 
            PaymentStatus.COMPLETED,
            "0000022222", 
            "kimmy", 
            "02/28"
        );

        // Calculate the total payment
        // double total = payment.calculateTotal();
        // System.out.println("Total Amount: $" + total);

        // Display payment details
        System.out.println(payment);
        System.out.println(payment2);
    }
}
    

