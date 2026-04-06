package hotel.javabeans;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import hotel.javabeans.payment.CreditCardPayment;
import hotel.javabeans.payment.Payment;

public class App 
{
    public static void main( String[] args )
    {
        Map<TypeOfRoom, Integer> TypeOfRooms = new EnumMap<>(TypeOfRoom.class);
        TypeOfRooms.put(TypeOfRoom.SINGLE, 1);
        TypeOfRooms.put(TypeOfRoom.DOUBLE, 2);

        Map<TypeOfRoom, Double> roomRates = new EnumMap<>(TypeOfRoom.class);
        roomRates.put(TypeOfRoom.SINGLE, 50.0);
        roomRates.put(TypeOfRoom.DOUBLE, 80.0);

        Invoice invoice = new Invoice(
            "BK001",        // bookingId
            TypeOfRooms,
            roomRates,
            3,              // numberOfNights
            5,              // numberOfGuests
            10.0,           // discount
            40.0,           // deposit already paid
            null            // payment (not paid yet)
        );

        Payment payment = new CreditCardPayment(
            "P001",
            invoice.getTotalAmount(),
            LocalDate.now(),
            Payment.PaymentStatus.COMPLETED,
            "1234567890",
            "John Doe",
            "12/30"
        );

        invoice.payInvoice(payment);
        System.out.println(invoice);
        System.out.println("Total: $" + invoice.getTotalAmount());
        System.out.println("Paid? " + invoice.isPaid());
    }

    
}
    

