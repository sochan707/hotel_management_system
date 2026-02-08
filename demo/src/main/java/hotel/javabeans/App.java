package hotel.javabeans;

import java.time.LocalDate;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args )
    {
        Scanner scan = new Scanner(System.in);

        //Reservation
        System.out.print("Enter guest name: ");
        String name = scan.nextLine();

        System.out.print("Enter phone number: ");
        String phone = scan.nextLine();

        System.out.print("Enter room number: ");
        int roomNumber = scan.nextInt();

        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scan.next());

        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scan.next());

        System.out.print("Enter booking price: ");
        double price = scan.nextDouble();

        Reservation r = new Reservation(name, phone, roomNumber, checkIn, checkOut, price);

        // display reservation 
        System.out.println("\n--- Reservation Details ---");
        System.out.println("Guest Name: " + r.getGuestName());
        System.out.println("Phone: " + r.getPhone());
        System.out.println("Room Number: " + r.getRoomNumber());
        System.out.println("Check-in Date: " + r.getCheckInDate());
        System.out.println("Check-out Date: " + r.getCheckOutDate());
        System.out.println("Booking Price: $" + r.getBookingPrice());

        scan.close();
    }
}
    

