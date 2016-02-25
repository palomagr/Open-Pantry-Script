package newkinect;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Timespan {
    private final LocalDateTime start;
    private final LocalDateTime end;
    
    public Timespan(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("requires start <= end");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
    
    public boolean contains(LocalDateTime now) {
        return now.equals(getStart()) || now.equals(getEnd()) || (now.isAfter(getStart()) && now.isBefore(getEnd()));
    }
    
    public List<Timespan> split() { 
        if(!end.toLocalDate().equals(start.toLocalDate())) {
            List<Timespan> dates = new ArrayList<>();
            Timespan firstDay = new Timespan(start, start.toLocalDate().atTime(23, 59, 59));
            long diffDays = end.getLong(ChronoField.EPOCH_DAY) - start.getLong(ChronoField.EPOCH_DAY);
            
            dates.add(firstDay);
            for(int i = 0; i < diffDays-1; i++) {
                LocalDateTime currDateTime = dates.get(i).getEnd().plusSeconds(1);
                dates.add(new Timespan(currDateTime, currDateTime.toLocalDate().atTime(23, 59, 59)));
            }
            Timespan lastDay = new Timespan(end.toLocalDate().atStartOfDay(), end);
            dates.add(lastDay);
            
            return Collections.unmodifiableList(dates);
        }
        return Arrays.asList(this);
    }
    
    public String toString() {
        return start + " <--> " + end;
    }
    
}
