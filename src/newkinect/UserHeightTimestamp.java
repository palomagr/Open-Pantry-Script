package newkinect;

import java.time.LocalDateTime;

public class UserHeightTimestamp {
    private double height;
    private LocalDateTime dateTime;
    private String user;
    
    public UserHeightTimestamp(String user, double height, LocalDateTime dateTime) {
        this.user = user;
        this.height = height;
        this.dateTime = dateTime;
    }
    
    public double getHeight() {
        return height;
    }
    
    public String getUser() {
        return user;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime.minusNanos(0);
    }
    
    public String toString() {
        return height + " at " + dateTime;
    }
}
