package newkinect;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//mutable
public class Person implements Comparable<Person> {
    private UserSkeletonTimestamp startTimeAndLocation;
    private UserSkeletonTimestamp endTimeAndLocation;
    private Double height;
    private int id;
    private List<UserSkeletonTimestamp> locationTimeStamps = new ArrayList<>();
    private final double MAX_PERSON_HEIGHT = 2.0;
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    public Person(int id, UserSkeletonTimestamp start) {
        this.id = id;
        this.startTimeAndLocation = start;
        checkRep();
    }
    
    public Person(Person original) {
        this.locationTimeStamps = new ArrayList<UserSkeletonTimestamp>(original.locationTimeStamps);
    }
    
    public Person() {
    }

    public void checkRep() {
        if(height != null) {
            assert height.doubleValue() > 0 && height.doubleValue() < MAX_PERSON_HEIGHT;
        }
        if(startTimeAndLocation != null && endTimeAndLocation != null) {
            assert startTimeAndLocation.getDateTime().isBefore(endTimeAndLocation.getDateTime());
        }
    }
    
    public void setStartLocationTimestamp(UserSkeletonTimestamp start) {
        startTimeAndLocation = start;
        checkRep();
    }
    
    public void setEndLocationTimestamp(UserSkeletonTimestamp end) {
        endTimeAndLocation = end;
        checkRep();
    }
    
    public void setHeight(double height) {
        this.height = new Double(height);
        checkRep();
    }
    
    public UserSkeletonTimestamp getStartLocationTimestamp() {
        return startTimeAndLocation;
    }
    
    public UserSkeletonTimestamp getEndLocationTimestamp() {
        return this.locationTimeStamps.get(this.locationTimeStamps.size() - 1);
    }
    
    public LocalDateTime getStartDateTime() {
        return startTimeAndLocation.getDateTime();
    }
    
    public LocalDateTime getEndDateTime() {
        return this.locationTimeStamps.get(this.locationTimeStamps.size() - 1).getDateTime();
    }
    
    public double getHeight() {
        double computedHeight = NewHeight.getUserHeight(this.locationTimeStamps);
        return computedHeight;
    }

    public void assignID(int newID) {
        this.id = newID;
    }
    
    public void addLocationTimeStamp(UserSkeletonTimestamp newLocationTimestamp) {
        this.locationTimeStamps.add(newLocationTimestamp);
    }
    
    @Override
    public int compareTo(Person user) {
        return this.getStartDateTime().compareTo(user.getStartDateTime());
    }
    
    @Override
    public String toString() { 
        StringBuilder returnString = new StringBuilder();
        returnString.append("User " + this.id + "; height: "+ df.format(this.getHeight()) + " from (" + this.getStartDateTime() + " to " + this.getEndDateTime() + ")\n");
        return returnString.toString();
    }
}
