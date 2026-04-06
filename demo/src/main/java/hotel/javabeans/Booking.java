package hotel.javabeans;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class Booking {

    public enum BookingStatus {
        UPCOMING,
        CHECKIN,
        CHECKOUT,
        CANCELLED;

        public static BookingStatus from(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Booking status cannot be null or empty.");
            }
            switch (value.trim().toUpperCase()) {
                case "UPCOMING": return UPCOMING;
                case "CHECKIN": return CHECKIN;
                case "CHECKOUT": return CHECKOUT;
                case "CANCELLED": return CANCELLED;
                default:
                    throw new IllegalArgumentException("Invalid status: " + value);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String bookingId;
    private final String reservationId;

    protected final String guestName;
    protected String phone;
    protected Map<TypeOfRoom, Integer> roomTypes = new EnumMap<>(TypeOfRoom.class); // how many room for each type
    protected LocalDate checkInDate;
    protected LocalDate checkOutDate;

    private LocalDate bookingCreationDate;
    private String specialRequests;
    private BookingStatus bookingStatus;
    private Map<TypeOfRoom, List<Integer>> actualRoomAssignments = new EnumMap<>(TypeOfRoom.class); // which room number for each type

    // -------------------------------------------------------------------------
    // Constructor 1 — build directly (walk-in guest)
    // -------------------------------------------------------------------------

    public Booking(String reservationId, String guestName, String phone,
                   Map<TypeOfRoom, Integer> roomTypes,
                   LocalDate checkInDate, LocalDate checkOutDate, String specialRequests) {
        this.bookingId     = UUID.randomUUID().toString();
        this.reservationId = validateString(reservationId, "Reservation ID");
        this.guestName = validateString(guestName, "Guest name"); // use this so I can set guestName to final
        setPhone(phone);
        setTypeOfRooms(roomTypes);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        this.bookingCreationDate = LocalDate.now();
        setSpecialRequests(specialRequests);
        this.bookingStatus = BookingStatus.UPCOMING;

    }

    // -------------------------------------------------------------------------
    // Constructor 2 — build from a confirmed Reservation
    // Converts TypeOfRoom → Booking.TypeOfRoom by name
    // -------------------------------------------------------------------------

    protected Booking(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        if (!reservation.isConfirmed()) {
            throw new IllegalStateException("Cannot create a booking from an unconfirmed reservation.");
        }

        this.bookingId     = UUID.randomUUID().toString();
        this.reservationId = reservation.getReservationId();
        this.guestName = validateString(reservation.getGuestName(), "Guest name");
        setPhone(reservation.getPhone());
        setCheckInDate(reservation.getCheckInDate());
        setCheckOutDate(reservation.getCheckOutDate());

        this.bookingStatus = BookingStatus.UPCOMING;
        this.bookingCreationDate = LocalDate.now();
        setSpecialRequests(null);

        // Convert TypeOfRoom → Booking.TypeOfRoom by matching name
        Map<TypeOfRoom, Integer> converted = new EnumMap<>(TypeOfRoom.class);
        for (Map.Entry<TypeOfRoom, Integer> entry : reservation.getTypeOfRooms().entrySet()) {
            converted.put(TypeOfRoom.valueOf(entry.getKey().name()), entry.getValue());
        }
        setTypeOfRooms(converted);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------
    
    private String validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        return value.trim();
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }

        String cleaned = phone.replaceAll("[\\s-]", "").trim();

        if (!cleaned.matches("^\\+?\\d{8,15}$")) {
            throw new IllegalArgumentException("Invalid phone format. Must be 8–15 digits, optional '+' at start.");
        }

        this.phone = cleaned;

        // About ("\\+?\\d{8,15}$")
        // ^    : start of string
        // \\+? : optional + (0 or 1 time)
        // \\d+ : one or more digits
        // $    : end of string

        // [\\s-]: 123-456-789 -> 123456789
    }

    public void setTypeOfRooms(Map<TypeOfRoom, Integer> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            throw new IllegalArgumentException("Room types cannot be null or empty.");
        }
        for (Map.Entry<TypeOfRoom, Integer> entry : roomTypes.entrySet()) {
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

    public void addTypeOfRoom(TypeOfRoom roomType, int quantity) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.roomTypes.put(roomType, this.roomTypes.getOrDefault(roomType, 0) + quantity);
    }

    public void removeTypeOfRoom(TypeOfRoom roomType) {
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

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = 
            (specialRequests == null || specialRequests.trim().isEmpty())? 
            "No special request" : specialRequests.trim();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getBookingId()                 { return bookingId; }
    public String getReservationId()             { return reservationId; }
    public String getGuestName()                 { return guestName; }
    public String getPhone()                     { return phone; }
    public Map<TypeOfRoom, Integer> getTypeOfRooms() { return new EnumMap<>(roomTypes); }
    public LocalDate getCheckInDate()            { return checkInDate; }
    public LocalDate getCheckOutDate()           { return checkOutDate; }
    public BookingStatus getBookingStatus()      {return bookingStatus;}
    public LocalDate getBookingCreationDate()    {return bookingCreationDate;}

    public Map<TypeOfRoom, List<Integer>> getActualRoomAssignments() {
        Map<TypeOfRoom, List<Integer>> copy = new EnumMap<>(TypeOfRoom.class);

        for (Map.Entry<TypeOfRoom, List<Integer>> entry : actualRoomAssignments.entrySet()) {
            copy.put(entry.getKey(), new java.util.ArrayList<>(entry.getValue()));
        }

        return copy;
    }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    public long getNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public int getTotalRooms() {
        return roomTypes.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void assignRoom(TypeOfRoom type, int roomNumber) {
        if (bookingStatus != BookingStatus.UPCOMING) {
            throw new IllegalStateException("Room assignment only allowed for UPCOMING bookings.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive.");
        }

        // prevent duplicate across ALL types
        for (List<Integer> rooms : actualRoomAssignments.values()) {
            if (rooms.contains(roomNumber)) {
                throw new IllegalArgumentException("Room " + roomNumber + " already assigned.");
            }
        }

        actualRoomAssignments.computeIfAbsent(type, k -> new java.util.ArrayList<>())
                             .add(roomNumber);
    }

    public void checkIn() {
        if (bookingStatus != BookingStatus.UPCOMING) {
            throw new IllegalStateException("Only upcoming bookings can check in.");
        }
        bookingStatus = BookingStatus.CHECKIN;
    }

    public void checkOut() {
        if (bookingStatus != BookingStatus.CHECKIN) {
            throw new IllegalStateException("Must check in before check out.");
        }
        bookingStatus = BookingStatus.CHECKOUT;
    }

    public void cancel() {
        if (bookingStatus == BookingStatus.CHECKOUT) {
            throw new IllegalStateException("Cannot cancel after checkout.");
        }
        bookingStatus = BookingStatus.CANCELLED;
    }

    public void removeAssignedRoom(TypeOfRoom type, int roomNumber) {
        if (bookingStatus != BookingStatus.UPCOMING) {
            throw new IllegalStateException("Cannot modify rooms unless booking is UPCOMING.");
        }

        List<Integer> rooms = actualRoomAssignments.get(type);
        if (rooms == null || !rooms.remove(Integer.valueOf(roomNumber))) {
            throw new IllegalArgumentException("Room not assigned.");
        }

        if (rooms.isEmpty()) {
            actualRoomAssignments.remove(type);
        }
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "\n================= BOOKING RECEIPT =================\n" +
            "Booking ID        : " + bookingId + "\n" +
            "Reservation ID    : " + reservationId + "\n" +
            "Guest Name        : " + guestName + "\n" +
            "Phone             : " + phone + "\n" +
            "---------------------------------------------------\n" +
            "Room Types        : " + roomTypes + "\n" +
            "Total Rooms       : " + getTotalRooms() + "\n" +
            "Check-In Date     : " + checkInDate + "\n" +
            "Check-Out Date    : " + checkOutDate + "\n" +
            "Number of Nights  : " + getNights() + "\n" +
            "---------------------------------------------------\n" +
            "Booking Status    : " + bookingStatus + "\n" +
            "Created On        : " + bookingCreationDate + "\n" +
            "Special Requests  : " + (specialRequests != null ? specialRequests : "None") + "\n" +
            "---------------------------------------------------\n" +
            "Room Assignments  : " + actualRoomAssignments + "\n" +
            "===================================================\n";
    }
}