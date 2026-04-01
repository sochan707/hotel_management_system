package hotel.javabeans;

import java.time.LocalDate;

public abstract class Booking{
    protected String guestName;
    protected String phone;
    protected int roomNumber;
    protected LocalDate checkInDate;
    protected LocalDate checkOutDate;
    protected double bookingPrice;

    public static final double TAX_RATE = 0.1;


    public Booking(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice) {

    public Booking(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice){
        setGuestName(guestName);
        setPhone(phone);
        setRoomNumber(roomNumber);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setBookingPrice(bookingPrice);
    }

    public abstract double calculateTotalPrice();

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
    
    //setters and getter
    public void setGuestName(String guestName){
        if(guestName != null && !guestName.trim().isEmpty()){
            this.guestName = guestName;
        }else{
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
    }

    public void setPhone(String phone){
        if(phone != null && !phone.trim().isEmpty()){
            this.phone = phone;
        }else{
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
    }

    public void setRoomNumber(int roomNumber){
        if(roomNumber>0){
            this.roomNumber = roomNumber;
        }else{
            throw new IllegalArgumentException("Room Number must be positive.");
        }
    }

    public void setCheckInDate(LocalDate checkInDate){
        if (checkInDate == null)
            throw new IllegalArgumentException("Check-in cannot be null");
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate))
            throw new IllegalArgumentException("Check-in must be before check-out");
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate){
        if (checkOutDate == null)
            throw new IllegalArgumentException("Check-out cannot be null");
        if (checkInDate != null && checkOutDate.isBefore(checkInDate))
            throw new IllegalArgumentException("Check-out must be after check-in");
        this.checkOutDate = checkOutDate;
    }


    public void setBookingPrice(double bookingPrice){
        if(bookingPrice > 0){
            this.bookingPrice = bookingPrice;
        }else{
            throw new IllegalArgumentException("Booking price must be positive.");
        }
    }

    // Getters
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

        @Override
    public String toString(){
        return "Booking{" +
                "guestName='" + guestName + '\'' +
                ", phone='" + phone + '\'' +
                ", roomNumber=" + roomNumber +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", bookingPrice=" + bookingPrice +
                ", totalPrice=" + calculateTotalPrice() +
                '}';
    }
    

}