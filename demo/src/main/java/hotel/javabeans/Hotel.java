package hotel.javabeans;

import java.util.ArrayList;
import java.util.List;

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

    // Hotel name and address setters with validation
    public void setHotelName(String hotelName) {
        if(hotelName != null && !hotelName.isEmpty()) {
            this.hotelName = hotelName;
        } else {
            throw new IllegalArgumentException("Hotel name cannot be null or empty.");
        }
    }

    public void setAddress(String address) {
        if(address != null && !address.isEmpty()) {
            this.address = address;
        } else {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
    }
    
    public String getHotelName() {
        return hotelName;
    }

    public String getAddress() {
        return address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public List<Staff> getStaffMembers() {
        return staffMembers;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    // Room management methods
    public void addRoom(Room room) {
        if(room != null) {
            rooms.add(room);
        } else {
            throw new IllegalArgumentException("Room cannot be null.");
        }
    }

    public void removeRoom(Room room) {
        if(room != null && rooms.contains(room)) {
            rooms.remove(room);
        } else {
            throw new IllegalArgumentException("Room not found or is null.");
        }
    }

    public Room findRoomByNumber(String roomNumber) {
        for(Room room : rooms) {
            if(room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for(Room room : rooms) {
            if(room.getStatus().equals(Room.AVAILABLE)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public List<Room> getRoomsByType(String roomType) {
        List<Room> roomsByType = new ArrayList<>();
        for(Room room : rooms) {
            if(room.getRoomType().equals(roomType)) {
                roomsByType.add(room);
            }
        }
        return roomsByType;
    }

    // Reservation management methods
    public void addReservation(Reservation reservation) {
        if(reservation != null) {
            reservations.add(reservation);
        } else {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
    }

    public void removeReservation(Reservation reservation) {
        if(reservation != null && reservations.contains(reservation)) {
            reservations.remove(reservation);
        } else {
            throw new IllegalArgumentException("Reservation not found or is null.");
        }
    }

    public List<Reservation> getReservationsByGuestName(String guestName) {
        List<Reservation> guestReservations = new ArrayList<>();
        for(Reservation reservation : reservations) {
            if(reservation.getGuestName().equals(guestName)) {
                guestReservations.add(reservation);
            }
        }
        return guestReservations;
    }

    // Guest management methods
    public void addGuest(Guest guest) {
        if(guest != null) {
            guests.add(guest);
        } else {
            throw new IllegalArgumentException("Guest cannot be null.");
        }
    }

    public void removeGuest(Guest guest) {
        if(guest != null && guests.contains(guest)) {
            guests.remove(guest);
        } else {
            throw new IllegalArgumentException("Guest not found or is null.");
        }
    }

    public Guest findGuestById(String id) {
        for(Guest guest : guests) {
            if(guest.getId().equals(id)) {
                return guest;
            }
        }
        return null;
    }

    // Staff management methods
    public void addStaff(Staff staff) {
        if(staff != null) {
            staffMembers.add(staff);
        } else {
            throw new IllegalArgumentException("Staff cannot be null.");
        }
    }

    public void removeStaff(Staff staff) {
        if(staff != null && staffMembers.contains(staff)) {
            staffMembers.remove(staff);
        } else {
            throw new IllegalArgumentException("Staff not found or is null.");
        }
    }

    public Staff findStaffById(String id) {
        for(Staff staff : staffMembers) {
            if(staff.getId().equals(id)) {
                return staff;
            }
        }
        return null;
    }

    public List<Staff> getStaffByPosition(String position) {
        List<Staff> staffByPosition = new ArrayList<>();
        for(Staff staff : staffMembers) {
            if(staff.getPosition().equalsIgnoreCase(position)) {
                staffByPosition.add(staff);
            }
        }
        return staffByPosition;
    }

    // Invoice management methods
    public void addInvoice(Invoice invoice) {
        if(invoice != null) {
            invoices.add(invoice);
        } else {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
    }

    public void removeInvoice(Invoice invoice) {
        if(invoice != null && invoices.contains(invoice)) {
            invoices.remove(invoice);
        } else {
            throw new IllegalArgumentException("Invoice not found or is null.");
        }
    }

    public Invoice findInvoiceById(String invoiceId) {
        for(Invoice invoice : invoices) {
            if(invoice.getInvoiceId().equals(invoiceId)) {
                return invoice;
            }
        }
        return null;
    }

    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> unpaidInvoices = new ArrayList<>();
        for(Invoice invoice : invoices) {
            if(!invoice.getIsPaid()) {
                unpaidInvoices.add(invoice);
            }
        }
        return unpaidInvoices;
    }

    public void addPayment(Payment payment) {
        if(payment != null) {
            payments.add(payment);
        } else {
            throw new IllegalArgumentException("Payment cannot be null.");
        }
    }

    public void removePayment(Payment payment) {
        if(payment != null && payments.contains(payment)) {
            payments.remove(payment);
        } else {
            throw new IllegalArgumentException("Payment not found or is null.");
        }
    }

    public Payment findPaymentById(String paymentId) {
        for(Payment payment : payments) {
            if(payment.getPaymentId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public int getTotalGuests() {
        return guests.size();
    }

    public int getTotalStaff() {
        return staffMembers.size();
    }

    public int getTotalReservations() {
        return reservations.size();
    }

    public double getTotalRevenue() {
        double totalRevenue = 0.0;
        for(Invoice invoice : invoices) {
            if(invoice.getIsPaid()) {
                totalRevenue += invoice.getTotalAmount();
            }
        }
        return totalRevenue;
    }
}