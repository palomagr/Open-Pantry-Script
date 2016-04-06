package newkinect;

import java.time.LocalDateTime;

//mutable
public class Person implements Comparable<Person> {
    private PointInTime startPointInTime;
    private PointInTime endPointInTime;
    private Double height;
    private final double MAX_PERSON_HEIGHT = 2.0;
    
    public Person(PointInTime start) {
        startPointInTime = start;
        checkRep();
    }

    public void checkRep() {
        if(height != null) {
            assert height.doubleValue() > 0 && height.doubleValue() < MAX_PERSON_HEIGHT;
        }
        if(startPointInTime != null && endPointInTime != null) {
            assert startPointInTime.getDateTime().isBefore(endPointInTime.getDateTime());
        }
    }
    
    public void setStartPointInTime(PointInTime start) {
        startPointInTime = start;
        checkRep();
    }
    
    public void setEndPointInTime(PointInTime end) {
        endPointInTime = end;
        checkRep();
    }
    
    public void setHeight(double height) {
        this.height = new Double(height);
        checkRep();
    }
    
    public PointInTime getStartPointInTime() {
        return startPointInTime;
    }
    
    public PointInTime getEndPointInTime() {
        return endPointInTime;
    }
    
    public LocalDateTime getStartDateTime() {
        return startPointInTime.getDateTime();
    }
    
    public LocalDateTime getEndDateTime() {
        return endPointInTime.getDateTime();
    }
    
    public double getHeight() {
        return height.doubleValue();
    }

    @Override
    public int compareTo(Person user) {
        return this.getStartDateTime().compareTo(user.getStartDateTime());
    }
}
