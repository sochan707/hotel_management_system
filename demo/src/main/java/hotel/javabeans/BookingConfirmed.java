package hotel.javabeans;

import java.time.LocalDate;

public class BookingConfirmed extends Booking {

    public BookingConfirmed(String guestName, String phone, int roomNumber,
                            LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice) {
        super(guestName, phone, roomNumber, checkInDate, checkOutDate, bookingPrice);
    }

    @Override
    public double calculateTotalPrice() {
        return bookingPrice + (bookingPrice * TAX_RATE);
    }

}
