package hotel.javabeans;

import java.time.LocalDate;

public class Reservation extends Booking {

    public Reservation(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice) {
        super(guestName, phone, roomNumber, checkInDate, checkOutDate, bookingPrice);
    }

    // Override abstract method
    @Override
    public double calculateTotalPrice() {
        double tax = bookingPrice * TAX_RATE;
        return bookingPrice + tax;
    }

    // Method overloading example: discount for longer stay
    public double calculateTotalPrice(double discountRate) {
        double total = calculateTotalPrice();
        return total - (total * discountRate);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "guestName='" + guestName + '\'' +
                ", phone='" + phone + '\'' +
                ", roomNumber=" + roomNumber +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", bookingPrice=" + bookingPrice +
                ", totalPrice=" + calculateTotalPrice() +
                '}';
    }
}