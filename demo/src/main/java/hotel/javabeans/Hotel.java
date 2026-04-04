package hotel.javabeans;

import java.util.ArrayList;
import java.util.List;

import hotel.javabeans.Person.Guest;
import hotel.javabeans.Person.Staff;
import hotel.javabeans.payment.Payment;

/**
 * Central aggregate for the hotel. All add/remove/find operations throw
 * {@link HotelException} for domain-level errors instead of returning null.
 */
public class Hotel {

    // ── Custom exception ──────────────────────────────────────────────────────
    public static class HotelException extends RuntimeException {
        public HotelException(String message)            { super(message); }
        public HotelException(String message, Throwable cause) { super(message, cause); }
    }

    // ── Fields ────────────────────────────────────────────────────────────────
    private String         hotelName;
    private String         address;
    private List<Room>        rooms        = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private List<Guest>       guests       = new ArrayList<>();
    private List<Staff>       staffMembers = new ArrayList<>();
    private List<Invoice>     invoices     = new ArrayList<>();
    private List<Payment>     payments     = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────
    public Hotel(String hotelName, String address) {
        setHotelName(hotelName);
        setAddress(address);
    }

    // ── Hotel setters/getters ─────────────────────────────────────────────────
    public void setHotelName(String hotelName) {
        if (hotelName == null || hotelName.trim().isEmpty())
            throw new HotelException("Hotel name cannot be null or empty.");
        this.hotelName = hotelName.trim();
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty())
            throw new HotelException("Address cannot be null or empty.");
        this.address = address.trim();
    }

    public String getHotelName() { return hotelName; }
    public String getAddress()   { return address; }

    // =========================================================================
    // ROOMS
    // =========================================================================

    public void addRoom(Room room) {
        if (room == null)
            throw new HotelException("Room cannot be null.");
        if (findRoomByNumber(room.getRoomNumber()) != null)
            throw new HotelException("Room with number '" + room.getRoomNumber() + "' already exists.");
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        if (room == null)
            throw new HotelException("Room cannot be null.");
        if (!rooms.remove(room))
            throw new HotelException("Room '" + room.getRoomNumber() + "' not found in the hotel.");
    }

    /** @return the room, or {@code null} if not found (use {@link #getRoom} for a throwing version). */
    public Room findRoomByNumber(String roomNumber) {
        if (roomNumber == null) return null;
        for (Room r : rooms)
            if (r.getRoomNumber().equalsIgnoreCase(roomNumber.trim())) return r;
        return null;
    }

    /** @throws HotelException if the room does not exist. */
    public Room getRoom(String roomNumber) {
        Room r = findRoomByNumber(roomNumber);
        if (r == null) throw new HotelException("Room '" + roomNumber + "' not found.");
        return r;
    }

    public List<Room> getRooms()          { return new ArrayList<>(rooms); }
    public int        getTotalRooms()     { return rooms.size(); }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms)
            if (Room.AVAILABLE.equals(r.getStatus())) available.add(r);
        return available;
    }

    public List<Room> getRoomsByType(RoomType type) {
        if (type == null) throw new HotelException("Room type cannot be null.");
        List<Room> result = new ArrayList<>();
        for (Room r : rooms)
            if (r.getRoomType() == type) result.add(r);
        return result;
    }

    // =========================================================================
    // RESERVATIONS
    // =========================================================================

    public void addReservation(Reservation reservation) {
        if (reservation == null)
            throw new HotelException("Reservation cannot be null.");
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (reservation == null || !reservations.remove(reservation))
            throw new HotelException("Reservation not found or is null.");
    }

    public Reservation findReservationById(String reservationId) {
        if (reservationId == null) return null;
        for (Reservation r : reservations)
            if (r.getReservationId().equals(reservationId.trim())) return r;
        return null;
    }

    public Reservation getReservation(String reservationId) {
        Reservation r = findReservationById(reservationId);
        if (r == null) throw new HotelException("Reservation '" + reservationId + "' not found.");
        return r;
    }

    public List<Reservation> getReservations()                     { return new ArrayList<>(reservations); }
    public int               getTotalReservations()               { return reservations.size(); }

    public List<Reservation> getReservationsByGuestName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new HotelException("Guest name cannot be null or empty.");
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations)
            if (r.getGuestName().equalsIgnoreCase(name.trim())) result.add(r);
        return result;
    }

    // =========================================================================
    // GUESTS
    // =========================================================================

    public void addGuest(Guest guest) {
        if (guest == null)
            throw new HotelException("Guest cannot be null.");
        if (findGuestById(guest.getId()) != null)
            throw new HotelException("Guest with ID '" + guest.getId() + "' already exists.");
        guests.add(guest);
    }

    public void removeGuest(Guest guest) {
        if (guest == null || !guests.remove(guest))
            throw new HotelException("Guest not found or is null.");
    }

    public Guest findGuestById(String id) {
        if (id == null) return null;
        for (Guest g : guests)
            if (g.getId().equals(id.trim())) return g;
        return null;
    }

    public Guest getGuest(String id) {
        Guest g = findGuestById(id);
        if (g == null) throw new HotelException("Guest with ID '" + id + "' not found.");
        return g;
    }

    public List<Guest> getGuests()  { return new ArrayList<>(guests); }
    public int         getTotalGuests() { return guests.size(); }

    // =========================================================================
    // STAFF
    // =========================================================================

    public void addStaff(Staff staff) {
        if (staff == null)
            throw new HotelException("Staff cannot be null.");
        if (findStaffById(staff.getId()) != null)
            throw new HotelException("Staff with ID '" + staff.getId() + "' already exists.");
        staffMembers.add(staff);
    }

    public void removeStaff(Staff staff) {
        if (staff == null || !staffMembers.remove(staff))
            throw new HotelException("Staff not found or is null.");
    }

    public Staff findStaffById(String id) {
        if (id == null) return null;
        for (Staff s : staffMembers)
            if (s.getId().equals(id.trim())) return s;
        return null;
    }

    public Staff getStaff(String id) {
        Staff s = findStaffById(id);
        if (s == null) throw new HotelException("Staff with ID '" + id + "' not found.");
        return s;
    }

    public List<Staff> getStaffMembers() { return new ArrayList<>(staffMembers); }
    public int         getTotalStaff()   { return staffMembers.size(); }

    public List<Staff> getStaffByPosition(String position) {
        if (position == null || position.trim().isEmpty())
            throw new HotelException("Position cannot be null or empty.");
        List<Staff> result = new ArrayList<>();
        for (Staff s : staffMembers)
            if (s.getPosition().equalsIgnoreCase(position.trim())) result.add(s);
        return result;
    }

    // =========================================================================
    // INVOICES
    // =========================================================================

    public void addInvoice(Invoice invoice) {
        if (invoice == null)
            throw new HotelException("Invoice cannot be null.");
        invoices.add(invoice);
    }

    public void removeInvoice(Invoice invoice) {
        if (invoice == null || !invoices.remove(invoice))
            throw new HotelException("Invoice not found or is null.");
    }

    public Invoice findInvoiceById(String invoiceId) {
        if (invoiceId == null) return null;
        for (Invoice inv : invoices)
            if (inv.getInvoiceId().equals(invoiceId.trim())) return inv;
        return null;
    }

    public Invoice getInvoice(String invoiceId) {
        Invoice inv = findInvoiceById(invoiceId);
        if (inv == null) throw new HotelException("Invoice '" + invoiceId + "' not found.");
        return inv;
    }

    public List<Invoice>  getInvoices()    { return new ArrayList<>(invoices); }

    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> unpaid = new ArrayList<>();
        for (Invoice inv : invoices)
            if (!inv.isPaid()) unpaid.add(inv);
        return unpaid;
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (Invoice inv : invoices)
            if (inv.isPaid()) total += inv.getTotalAmount();
        return total;
    }

    // =========================================================================
    // PAYMENTS
    // =========================================================================

    public void addPayment(Payment payment) {
        if (payment == null)
            throw new HotelException("Payment cannot be null.");
        payments.add(payment);
    }

    public void removePayment(Payment payment) {
        if (payment == null || !payments.remove(payment))
            throw new HotelException("Payment not found or is null.");
    }

    public Payment findPaymentById(String paymentId) {
        if (paymentId == null) return null;
        for (Payment p : payments)
            if (p.getPaymentId().equals(paymentId.trim())) return p;
        return null;
    }

    public Payment getPayment(String paymentId) {
        Payment p = findPaymentById(paymentId);
        if (p == null) throw new HotelException("Payment '" + paymentId + "' not found.");
        return p;
    }

    public List<Payment> getPayments() { return new ArrayList<>(payments); }

    // ── toString ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Hotel{name='" + hotelName + "', address='" + address +
               "', rooms=" + rooms.size() + ", guests=" + guests.size() +
               ", staff=" + staffMembers.size() + '}';
    }
}