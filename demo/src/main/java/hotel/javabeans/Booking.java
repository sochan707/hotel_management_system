package hotel.javabeans;

import java.time.LocalDate;

public abstract class Booking {
    protected String guestName;
    protected String phone;
    protected int roomNumber;
    protected LocalDate checkInDate;
    protected LocalDate checkOutDate;
    protected double bookingPrice;
    static final double TAX_RATE = 0.1;

    public Booking(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice) {
        setGuestName(guestName);
        setPhone(phone);
        setRoomNumber(roomNumber);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setBookingPrice(bookingPrice);
    }

    // Abstract method 
    public abstract double calculateTotalPrice();

    // Getters and setters
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.guestName = guestName;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("Phone cannot be empty");
        this.phone = phone;
    }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) {
        if (roomNumber < 0)
            throw new IllegalArgumentException("Room number cannot be negative");
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) throw new IllegalArgumentException("Check-in date cannot be null");
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate))
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkInDate != null && checkOutDate.isBefore(checkInDate))
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        this.checkOutDate = checkOutDate;
    }

    public double getBookingPrice() { return bookingPrice; }
    public void setBookingPrice(double bookingPrice) {
        if (bookingPrice <= 0.0) throw new IllegalArgumentException("Booking price must be positive");
        this.bookingPrice = bookingPrice;
    }


}
