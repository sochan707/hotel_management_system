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
            "00001111", 
            "thamus", 
            "12/25"
        );

        CreditCardPayment payment2 = new CreditCardPayment(
            "12345", 
            100.0, 
            LocalDate.now(), 
            PaymentStatus.COMPLETED,
            "00002222", 
            "kimmy", 
            "2/08"
        );

        // Calculate the total payment
        // double total = payment.calculateTotal();
        // System.out.println("Total Amount: $" + total);

        // Display payment details
        System.out.println(payment);
        System.out.println(payment2);
    }
}
    

