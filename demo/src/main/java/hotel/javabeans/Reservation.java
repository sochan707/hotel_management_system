package hotel.javabeans;

import java.time.LocalDate;

public class Reservation {

    private String guestName;
    private String phone;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double estimatedPrice;
    private boolean confirmed = false;

    public Reservation(String guestName, String phone, int roomNumber,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       double estimatedPrice) {
        setGuestName(guestName);
        setPhone(phone);
        setRoomNumber(roomNumber);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setEstimatedPrice(estimatedPrice);
    }

    // Confirm reservation → BookingConfirmed
    public BookingConfirmed confirmBooking(double paymentAmount) {
        if (confirmed) throw new IllegalStateException("Reservation already confirmed");
        if (paymentAmount < estimatedPrice) throw new IllegalArgumentException("Insufficient payment");
        confirmed = true;

        return new BookingConfirmed(
                guestName,
                phone,
                roomNumber,
                checkInDate,
                checkOutDate,
                paymentAmount
        );
    }

    // =========================
    // Setters with validation
    // =========================
    public void setGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty())
            throw new IllegalArgumentException("Guest name cannot be empty");
        this.guestName = guestName;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("Phone cannot be empty");
        this.phone = phone;
    }

    public void setRoomNumber(int roomNumber) {
        if (roomNumber <= 0) throw new IllegalArgumentException("Room number must be positive");
        this.roomNumber = roomNumber;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) throw new IllegalArgumentException("Check-in cannot be null");
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate))
            throw new IllegalArgumentException("Check-in must be before check-out");
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null) throw new IllegalArgumentException("Check-out cannot be null");
        if (checkInDate != null && checkOutDate.isBefore(checkInDate))
            throw new IllegalArgumentException("Check-out must be after check-in");
        this.checkOutDate = checkOutDate;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        if (estimatedPrice <= 0) throw new IllegalArgumentException("Price must be positive");
        this.estimatedPrice = estimatedPrice;
    }

    // =========================
    // Getters
    // =========================
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getEstimatedPrice() { return estimatedPrice; }
    public boolean isConfirmed() { return confirmed; }
}