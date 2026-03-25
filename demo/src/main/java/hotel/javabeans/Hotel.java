package hotel.javabeans;

import java.util.ArrayList;
import java.util.List;

import Person.Guest;
import Person.Staff;
import hotel.javabeans.payment.Payment;

public class Hotel {
    private String hotelName;
    private String address;
    private List<Room> rooms;
    private List<Reservation> reservations;
    private List<Guest> guests;
    private List<Staff> staffMembers;
    private List<Invoice> invoices;
    private List<Payment> payments;

    public Hotel(String hotelName, String address) {
        setHotelName(hotelName);
        setAddress(address);
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.staffMembers = new ArrayList<>();
        this.invoices = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public void setHotelName(String hotelName) {
        if (hotelName != null && !hotelName.isEmpty()) this.hotelName = hotelName;
        else throw new IllegalArgumentException("Hotel name cannot be null or empty.");
    }

    public void setAddress(String address) {
        if (address != null && !address.isEmpty()) this.address = address;
        else throw new IllegalArgumentException("Address cannot be null or empty.");
    }

    public String getHotelName() { return hotelName; }
    public String getAddress()   { return address; }

    // --- Room ---
    public void addRoom(Room room) {
        if (room != null) rooms.add(room);
        else throw new IllegalArgumentException("Room cannot be null.");
    }

    public void removeRoom(Room room) {
        if (room != null && rooms.contains(room)) rooms.remove(room);
        else throw new IllegalArgumentException("Room not found or is null.");
    }

    public Room findRoomByNumber(String roomNumber) {
        for (Room room : rooms)
            if (room.getRoomNumber().equals(roomNumber)) return room;
        return null;
    }

    public List<Room> getRooms()          { return rooms; }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms)
            if (room.getStatus().equals(Room.AVAILABLE)) available.add(room);
        return available;
    }

    public List<Room> getRoomsByType(String roomType) {
        List<Room> byType = new ArrayList<>();
        for (Room room : rooms)
            if (room.getRoomType().equals(roomType)) byType.add(room);
        return byType;
    }

    public int getTotalRooms() { return rooms.size(); }

    // --- Reservation ---
    public void addReservation(Reservation reservation) {
        if (reservation != null) reservations.add(reservation);
        else throw new IllegalArgumentException("Reservation cannot be null.");
    }

    public void removeReservation(Reservation reservation) {
        if (reservation != null && reservations.contains(reservation)) reservations.remove(reservation);
        else throw new IllegalArgumentException("Reservation not found or is null.");
    }

    public List<Reservation> getReservations() { return reservations; }

    public List<Reservation> getReservationsByGuestName(String guestName) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations)
            if (r.getGuestName().equals(guestName)) result.add(r);
        return result;
    }

    public int getTotalReservations() { return reservations.size(); }

    // --- Guest ---
    public void addGuest(Guest guest) {
        if (guest != null) guests.add(guest);
        else throw new IllegalArgumentException("Guest cannot be null.");
    }

    public void removeGuest(Guest guest) {
        if (guest != null && guests.contains(guest)) guests.remove(guest);
        else throw new IllegalArgumentException("Guest not found or is null.");
    }

    public Guest findGuestById(String id) {
        for (Guest guest : guests)
            if (guest.getId().equals(id)) return guest;
        return null;
    }

    public List<Guest> getGuests()  { return guests; }
    public int getTotalGuests()     { return guests.size(); }

    // --- Staff ---
    public void addStaff(Staff staff) {
        if (staff != null) staffMembers.add(staff);
        else throw new IllegalArgumentException("Staff cannot be null.");
    }

    public void removeStaff(Staff staff) {
        if (staff != null && staffMembers.contains(staff)) staffMembers.remove(staff);
        else throw new IllegalArgumentException("Staff not found or is null.");
    }

    public Staff findStaffById(String id) {
        for (Staff staff : staffMembers)
            if (staff.getId().equals(id)) return staff;
        return null;
    }

    public List<Staff> getStaffMembers() { return staffMembers; }

    public List<Staff> getStaffByPosition(String position) {
        List<Staff> result = new ArrayList<>();
        for (Staff staff : staffMembers)
            if (staff.getPosition().equalsIgnoreCase(position)) result.add(staff);
        return result;
    }

    public int getTotalStaff() { return staffMembers.size(); }

    // --- Invoice ---
    public void addInvoice(Invoice invoice) {
        if (invoice != null) invoices.add(invoice);
        else throw new IllegalArgumentException("Invoice cannot be null.");
    }

    public void removeInvoice(Invoice invoice) {
        if (invoice != null && invoices.contains(invoice)) invoices.remove(invoice);
        else throw new IllegalArgumentException("Invoice not found or is null.");
    }

    public Invoice findInvoiceById(String invoiceId) {
        for (Invoice invoice : invoices)
            if (invoice.getInvoiceId().equals(invoiceId)) return invoice;
        return null;
    }

    public List<Invoice> getInvoices() { return invoices; }

    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> unpaid = new ArrayList<>();
        for (Invoice invoice : invoices)
            if (!invoice.getIsPaid()) unpaid.add(invoice);
        return unpaid;
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (Invoice invoice : invoices)
            if (invoice.getIsPaid()) total += invoice.getTotalAmount();
        return total;
    }

    // --- Payment ---
    public void addPayment(Payment payment) {
        if (payment != null) payments.add(payment);
        else throw new IllegalArgumentException("Payment cannot be null.");
    }

    public void removePayment(Payment payment) {
        if (payment != null && payments.contains(payment)) payments.remove(payment);
        else throw new IllegalArgumentException("Payment not found or is null.");
    }

    public Payment findPaymentById(String paymentId) {
        for (Payment payment : payments)
            if (payment.getPaymentId().equals(paymentId)) return payment;
        return null;
    }

    public List<Payment> getPayments()  { return payments; }
}