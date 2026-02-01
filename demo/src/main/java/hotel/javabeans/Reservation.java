package hotel.javabeans;

import java.time.LocalDate;

public class Reservation {
    private String guestName;
    private String phone;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double bookingPrice;

    public Reservation(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice){
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingPrice = bookingPrice;
    }

    public String getGuestName(){
        return guestName;
    }

    public String getPhone(){
        return phone;
    }

    public int getRoomNumber(){
        return roomNumber;
    }

    public LocalDate getCheckInDate(){
        return checkInDate;
    }

    public LocalDate getCheckOutDate(){
        return checkOutDate;
    }

    public double getBookingPrice(){
        return bookingPrice;
    }
}
