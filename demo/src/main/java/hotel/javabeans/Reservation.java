package hotel.javabeans;

import java.time.LocalDate;

public class Reservation {
    private String guestName;
    private String phone;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double bookingPrice;
    static double TAX_RATE = 0.1;


    public Reservation(String guestName, String phone, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double bookingPrice){
        setGuestName(guestName);
        setPhone(phone);
        setRoomNumber(roomNumber);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setBookingPrice(bookingPrice);
        
    }
    public void setGuestName(String guestName){
        if(guestName == null || guestName.trim().isEmpty()){
            throw new IllegalArgumentException("Name can not be empty");
        }
        this.guestName = guestName;
    }
    
    public void setPhone(String phone){
        if(phone == null || phone.trim().isEmpty()){
            throw new IllegalArgumentException("Phone must be number");
        }
        this.phone = phone;
    }
    
    public void setRoomNumber(int roomNumber){
        if(roomNumber<0){
            throw new IllegalArgumentException("Room Can not Negative");
        }
        this.roomNumber = roomNumber;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
            this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate){
        if(checkInDate != null && checkOutDate.isBefore(checkInDate)){
            throw new IllegalArgumentException("Check-out date cannot be null");
        }
        this.checkOutDate = checkOutDate;
    }

    public void setBookingPrice(double bookingPrice){
        if(bookingPrice<= 0.0){
            throw new IllegalArgumentException("Booking-Price cannot 0");
        }
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
