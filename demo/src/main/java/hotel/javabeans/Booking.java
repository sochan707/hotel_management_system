package hotel.javabeans;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

public abstract class Booking {

    public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE
    }

    protected String guestName;
    protected String phone;
    protected Map<RoomType, Integer> roomTypes = new EnumMap<>(RoomType.class);
    protected LocalDate checkInDate;
    protected LocalDate checkOutDate;
    protected double bookingPrice;

    public static final double TAX_RATE = 0.1;

    public Booking(String guestName, String phone, Map<RoomType, Integer> roomTypes,
                   LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice) {
        setGuestName(guestName);
        setPhone(phone);
        setRoomTypes(roomTypes);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setBookingPrice(bookingPrice);
    }

    public abstract double calculateTotalPrice();

    // Setters
    public void setGuestName(String guestName) {
        if (guestName != null && !guestName.trim().isEmpty()) {
            this.guestName = guestName;
        } else {
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
    }

    public void setPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        } else {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
    }

    public void setRoomTypes(Map<RoomType, Integer> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            throw new IllegalArgumentException("Room types cannot be null or empty.");
        }
        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            if (entry.getValue() <= 0) {
                throw new IllegalArgumentException("Each room type must have a positive quantity.");
            }
        }
        this.roomTypes = new EnumMap<>(roomTypes);
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null)
            throw new IllegalArgumentException("Check-in cannot be null");
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate))
            throw new IllegalArgumentException("Check-in must be before check-out");
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null)
            throw new IllegalArgumentException("Check-out cannot be null");
        if (checkInDate != null && checkOutDate.isBefore(checkInDate))
            throw new IllegalArgumentException("Check-out must be after check-in");
        this.checkOutDate = checkOutDate;
    }

    public void setBookingPrice(double bookingPrice) {
        if (bookingPrice > 0) {
            this.bookingPrice = bookingPrice;
        } else {
            throw new IllegalArgumentException("Booking price must be positive.");
        }
    }

    // Getters
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public Map<RoomType, Integer> getRoomTypes() { return new EnumMap<>(roomTypes); }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getBookingPrice() { return bookingPrice; }

    @Override
    public String toString() {
        return "guestName='" + guestName + "'" +
               ", phone='" + phone + "'" +
               ", roomTypes=" + roomTypes +
               ", checkInDate=" + checkInDate +
               ", checkOutDate=" + checkOutDate +
               ", bookingPrice=" + bookingPrice +
               ", totalPrice=" + calculateTotalPrice();
    }
}