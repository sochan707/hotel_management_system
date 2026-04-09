package hotel.javabeans;

public enum TypeOfRoom {
    SINGLE("Single", 1),
    DOUBLE("Double", 2),
    TRIPLE("Triple", 3);

    private final String displayName;
    private final int capacity;   // number of people the room taht can stay

  
    TypeOfRoom(String displayName, int capacity) {
        this.displayName = displayName;
        this.capacity = capacity;
    }
    public String getDisplayName() {
        return displayName;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
