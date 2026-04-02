package hotel.javabeans;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class Booking {
    public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE;

        public static RoomType from(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Room type cannot be null or empty.");
            }
            switch (value.trim().toUpperCase()) {
                case "SINGLE": return SINGLE;
                case "DOUBLE": return DOUBLE;
                case "TRIPLE": return TRIPLE;
                default:
                    throw new IllegalArgumentException("Invalid room type: " + value);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    public static final double TAX_RATE = 0.1;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String bookingId;
    private final String reservationId;

    protected String guestName;
    protected String phone;
    protected Map<RoomType, Integer> roomTypes = new EnumMap<>(RoomType.class);
    protected LocalDate checkInDate;
    protected LocalDate checkOutDate;
    protected double bookingPrice;

    // -------------------------------------------------------------------------
    // Constructor 1 — build directly (walk-in guest)
    // -------------------------------------------------------------------------

    public Booking(String reservationId, String guestName, String phone,
                   Map<RoomType, Integer> roomTypes,
                   LocalDate checkInDate, LocalDate checkOutDate,
                   double bookingPrice) {
        this.bookingId     = UUID.randomUUID().toString();
        this.reservationId = Objects.requireNonNull(reservationId, "reservationId is required.");
        setGuestName(guestName);
        setPhone(phone);
        setRoomTypes(roomTypes);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setBookingPrice(bookingPrice);
    }

    // -------------------------------------------------------------------------
    // Constructor 2 — build from a confirmed Reservation
    // Converts Reservation.RoomType → Booking.RoomType by name
    // -------------------------------------------------------------------------

    protected Booking(Reservation reservation, double bookingPrice) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        if (!reservation.isConfirmed()) {
            throw new IllegalStateException("Cannot create a booking from an unconfirmed reservation.");
        }
        this.bookingId     = UUID.randomUUID().toString();
        this.reservationId = reservation.getReservationId();
        setGuestName(reservation.getGuestName());
        setPhone(reservation.getPhone());
        setCheckInDate(reservation.getCheckInDate());
        setCheckOutDate(reservation.getCheckOutDate());
        setBookingPrice(bookingPrice);

        // Convert Reservation.RoomType → Booking.RoomType by matching name
        Map<RoomType, Integer> converted = new EnumMap<>(RoomType.class);
        for (Map.Entry<Reservation.RoomType, Integer> entry : reservation.getRoomTypes().entrySet()) {
            converted.put(RoomType.valueOf(entry.getKey().name()), entry.getValue());
        }
        setRoomTypes(converted);
    }

    // -------------------------------------------------------------------------
    // Abstract method
    // -------------------------------------------------------------------------

    public abstract double calculateTotalPrice();

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
        this.guestName = guestName.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        this.phone = phone.trim();
    }

    public void setRoomTypes(Map<RoomType, Integer> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            throw new IllegalArgumentException("Room types cannot be null or empty.");
        }
        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Room type key cannot be null.");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException(
                    "Quantity for room type " + entry.getKey() + " must be positive.");
            }
        }
        this.roomTypes = new EnumMap<>(roomTypes);
    }

    public void addRoomType(RoomType roomType, int quantity) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.roomTypes.put(roomType, this.roomTypes.getOrDefault(roomType, 0) + quantity);
    }

    public void removeRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (!this.roomTypes.containsKey(roomType)) {
            throw new IllegalArgumentException("Room type " + roomType + " is not in this booking.");
        }
        this.roomTypes.remove(roomType);
        if (this.roomTypes.isEmpty()) {
            throw new IllegalStateException("Booking must have at least one room type.");
        }
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null.");
        }
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check-out date cannot be null.");
        }
        if (checkInDate != null && checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        this.checkOutDate = checkOutDate;
    }

    public void setBookingPrice(double bookingPrice) {
        if (bookingPrice <= 0) {
            throw new IllegalArgumentException("Booking price must be positive.");
        }
        this.bookingPrice = bookingPrice;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getBookingId()                 { return bookingId; }
    public String getReservationId()             { return reservationId; }
    public String getGuestName()                 { return guestName; }
    public String getPhone()                     { return phone; }
    public Map<RoomType, Integer> getRoomTypes() { return new EnumMap<>(roomTypes); }
    public LocalDate getCheckInDate()            { return checkInDate; }
    public LocalDate getCheckOutDate()           { return checkOutDate; }
    public double getBookingPrice()              { return bookingPrice; }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    public double calculateTax() {
        return round2(bookingPrice * TAX_RATE);
    }

    public double totalWithTax() {
        return round2(bookingPrice + calculateTax());
    }

    public long getNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public int getTotalRooms() {
        return roomTypes.values().stream().mapToInt(Integer::intValue).sum();
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Booking{" +
               "bookingId='"       + bookingId            + '\'' +
               ", reservationId='" + reservationId         + '\'' +
               ", guestName='"     + guestName             + '\'' +
               ", phone='"         + phone                 + '\'' +
               ", roomTypes="      + roomTypes             +
               ", totalRooms="     + getTotalRooms()       +
               ", checkInDate="    + checkInDate           +
               ", checkOutDate="   + checkOutDate          +
               ", nights="         + getNights()           +
               ", bookingPrice="   + bookingPrice          +
               ", tax="            + calculateTax()        +
               ", totalWithTax="   + totalWithTax()        +
               ", totalPrice="     + calculateTotalPrice() +
               '}';
    }
}