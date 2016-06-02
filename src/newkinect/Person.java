package newkinect;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Represents an individual person as a list of readings of their Skeleton at different times.
 * 
 * Each instance has a determined height that is calculated from the heights of all its Skeleton objects in the UserSkeletonTimestamp objects
 *
 */

public class Person implements Comparable<Person> {
    private UserSkeletonTimestamp startTimeAndLocation;
    private UserSkeletonTimestamp endTimeAndLocation;
    private Double height;
    private int id;
    private List<UserSkeletonTimestamp> locationTimeStamps = new ArrayList<>();
    private final double MAX_PERSON_HEIGHT = 2.0;
    private final DecimalFormat df = new DecimalFormat("#.##");

    //  Abstraction Function:
    //      Takes a list of UserSkeletonTimestamps and represents a single person that was at the locations at the corresponding times given by the UserSkeltonTimestamps
    //      
    //  Representation Invariant:
    //      The person must have a height greater than 0 and less than the MAX_PERSON_HEIGHT value of 2 meters
    //      The first UserSkeletonTimestamp time value must be before the last UserSkeletonTimestamp time value

    /**
     * Create a new person object with a Person ID and a starting UserSkeletonTimestamp giving the starting Skeleton and time values of the person
     * @param id the assigned Person ID
     * @param start the first UserSkeletonTimestamp value of this person
     * This first UserSkeletonTimestamp value is added to the list of this person's UserSkeletonTimestamps
     */
    public Person(int id, UserSkeletonTimestamp start) {
        this.id = id;
        this.startTimeAndLocation = start;
        this.locationTimeStamps.add(0, start);
        this.endTimeAndLocation = start;
        checkRep();
    }

    /**
     * Create a new person object with the same UserSkeletonTimestamps as another person object
     * @param original the other person object
     */
    public Person(Person original) {
        this.locationTimeStamps = new ArrayList<UserSkeletonTimestamp>(original.locationTimeStamps);
        this.startTimeAndLocation = this.locationTimeStamps.get(0);
        this.endTimeAndLocation = this.locationTimeStamps.get(this.locationTimeStamps.size() - 1);
    }

    /**
     * Create a new empty person object with attributes that will later be determined
     */
    public Person() {
    }

    /**
     * Check the representation of this person and make sure it is valid
     */
    public void checkRep() {
        if(height != null) {
            assert height.doubleValue() > 0 && height.doubleValue() < MAX_PERSON_HEIGHT;
        }
        if(startTimeAndLocation != null && endTimeAndLocation != null) {
            assert (startTimeAndLocation.getDateTime().isBefore(endTimeAndLocation.getDateTime()) || startTimeAndLocation.getDateTime().equals(endTimeAndLocation.getDateTime()));
        }
        assert this.startTimeAndLocation.equals(this.locationTimeStamps.get(0));
        assert this.endTimeAndLocation.equals(this.locationTimeStamps.get(this.locationTimeStamps.size()-1));
    }

    /**
     * Set the starting UserSkeletonTimestamp of this person, when he or she is first detected by the Kinect
     * @param start the UserSkeletonTimestamp value to label as this person's starting UserSkeletonTimestamp
     */
    public void setStartLocationTimestamp(UserSkeletonTimestamp start) {
        startTimeAndLocation = start;
        checkRep();
    }

    /**
     * Set the ending UserSkeletonTimestamp of this person, when he or she is last detected by the Kinect
     * @param end the UserSkeletonTimestamp value to label as this person's ending UserSkeletonTimestamp
     */
    public void setEndLocationTimestamp(UserSkeletonTimestamp end) {
        endTimeAndLocation = end;
        checkRep();
    }

    /**
     * Set the height value of this person
     * @param height
     */
    public void setHeight(double height) {
        this.height = new Double(height);
        checkRep();
    }

    /**
     * Get the starting UserSkeletonTimestamp of this person. If the attribute has not been set, get the first value of the list of UserSkeletonTimestamps and set it
     * @return the UserSkeletonTimestamp value representing this person's starting UserSkeletonTimestamp
     */
    public UserSkeletonTimestamp getStartLocationTimestamp() {
        //        if (startTimeAndLocation.equals(null)) {
        this.startTimeAndLocation = this.locationTimeStamps.get(0);
        //        }
        checkRep();
        return startTimeAndLocation;
    }

    /**
     * Get the ending UserSkeletonTimestamp of this person
     * @return the UserSkeletonTimestamp vlaue representing this person's ending UserSkeletonTimestamp
     */
    public UserSkeletonTimestamp getEndLocationTimestamp() {
//        if (startTimeAndLocation.equals(null)) {
            this.endTimeAndLocation = this.locationTimeStamps.get(this.locationTimeStamps.size() - 1);
//        }
        checkRep();
        return endTimeAndLocation; 
    }

    /**
     * Get the time of the starting UserSkeletonTimestamp of this person, when he or she is first detected by the Kinect
     * @return the time of the starting UserSkeletonTimestamp of this person
     */
    public LocalDateTime getStartDateTime() {
        return this.getStartLocationTimestamp().getDateTime();
    }

    /**
     * Get the time of the ending UserSkeletonTimestamp of this person, when he or she is last detected by the Kinect
     * @return the time of the ending UserSkeletonTimestamp of this person
     */
    public LocalDateTime getEndDateTime() {
        return this.getEndLocationTimestamp().getDateTime();
    }

    /**
     * Calculate the approximate height of this person using the Skeleton objects in this person's list of UserSkeletonTimestamps
     * @return the calculated approximate height of this person
     */
    public double getHeight() {
        double computedHeight = getUserHeight(this.locationTimeStamps);
        return computedHeight;
    }

    /**
     * Assign this person a Person ID
     * @param newID the new Person ID to be assigned
     */
    public void assignID(int newID) {
        this.id = newID;
    }

    /**
     * Add a UserSkeletonTimestamp to this person's list of UserSkeletonTimestamps
     * @param newLocationTimestamp the new UserSkeletonTimestamp to be added
     */
    public void addLocationTimeStamp(UserSkeletonTimestamp newLocationTimestamp) {
        this.locationTimeStamps.add(newLocationTimestamp);
        if (this.locationTimeStamps.size() == 1) {
            this.startTimeAndLocation = this.locationTimeStamps.get(0);
        }
        this.endTimeAndLocation = this.locationTimeStamps.get(this.locationTimeStamps.size() - 1);
        checkRep();
    }

    /**
     * Places a height value into its respective bin, where each bin spans a measurement range in meters
     * @param currentHeight the height in meters to be placed in a bin
     * @param bin0 Bin containing heights with measurements between 0.5 and 1 meter
     * @param bin1 Bin containing heights with measurements between 0.75 and 1.25 meters
     * @param bin2 Bin containing heights with measurements between 1 and 1.5 meters
     * @param bin3 Bin containing heights with measurements between 1.25 and 1.75 meters
     * @param bin4 Bin containing heights with measurements between 1.5 and 2 meters
     */
    public static void placeHeightInBin(double height, List<Double> bin0, List<Double> bin1, List<Double> bin2,
            List<Double> bin3, List<Double> bin4) {
        double HALF_METER = 0.5;
        double THREE_QUARTER_METER = 0.75;
        double ONE_METER = 1;
        double ONE_AND_QUARTER_METER = 1.25;
        double ONE_AND_HALF_METER = 1.5;
        double ONE_AND_THREE_QUARTER_METER = 1.75;
        double TWO_METER = 2;

        if (height >= HALF_METER && height < ONE_METER) {
            bin0.add(height);
        } else if (height >= THREE_QUARTER_METER && height < ONE_AND_QUARTER_METER) {
            bin1.add(height);
        } else if (height >= ONE_METER && height < ONE_AND_HALF_METER) {
            bin2.add(height);
        } else if (height >= ONE_AND_QUARTER_METER && height < ONE_AND_THREE_QUARTER_METER) {
            bin3.add(height);
        } else if (height >= ONE_AND_HALF_METER && height < TWO_METER) {
            bin4.add(height);
        }
    }

    /**
     * Finds the measurement range with the most observed heights and the average of the heights
     * that fall in that range
     * @param bin0 Bin containing heights with measurements between 0.5 and 1 meter
     * @param bin1 Bin containing heights with measurements between 0.75 and 1.25 meters
     * @param bin2 Bin containing heights with measurements between 1 and 1.5 meters
     * @param bin3 Bin containing heights with measurements between 1.25 and 1.75 meters
     * @param bin4 Bin containing heights with measurements between 1.5 and 2 meters
     * @return the average of the heights that fall in the measurement range with most recorded height observations
     */
    public static double calculateMostFrequentHeightAverage(List<Double> bin0, List<Double> bin1, List<Double> bin2,
            List<Double> bin3, List<Double> bin4) {

        double mostFrequentHeightSum = 0.0;
        // find bin (measurement range) with most height observations
        List<List<Double>> bins = Arrays.asList(bin0, bin1, bin2, bin3, bin4);
        int maxBin = 0;
        for (int i = 1; i < bins.size(); i++){
            double binSize = bins.get(i).size();
            if (binSize > bins.get(maxBin).size()){
                maxBin = i;
            }
        }
        // take average of heights in bin with most height observations
        for (double height : bins.get(maxBin)) {
            mostFrequentHeightSum += height;
        }
        double freqHeightAvg = mostFrequentHeightSum / (double) bins.get(maxBin).size();
        return freqHeightAvg;
    }

    /**
     * Calculate the estimated height of the user from the observed skeleton heights over the time span of detection
     * @param processedData the calculated heights of the user at different times
     * @return the calculated estimated height of the user
     */
    public static double getUserHeight(final List<UserSkeletonTimestamp> processedData) {
        // Accounting for every single height measurement observed
        double totalHeightSum = 0.0;
        double totalObservationCount = processedData.size();
        // Accounting for the first FIRST_HEIGHTS_SEEN observed heights
        // Placing extra weight on these observation because people are most likely in a normal stature
        // when they first enter a room (when the kinect first tracks them in this case)
        double firstObservedSum = 0.0;
        // maximum level of discrepancy allowed between first observed height average and most frequently observed height average
        double START_VS_FREQ_DIFF = 0.2;
        // the number of first heights seen that are given more weight
        double FIRST_HEIGHTS_OBSERVED = 30;  
        int heightsObserved = 0;

        final List<Double> startHeights = new ArrayList<>();
        final List<Double> bin0 = new ArrayList<>();
        final List<Double> bin1 = new ArrayList<>();
        final List<Double> bin2 = new ArrayList<>();
        final List<Double> bin3 = new ArrayList<>();
        final List<Double> bin4 = new ArrayList<>();

        for (UserSkeletonTimestamp instance: processedData) {
            double currentHeight = instance.getHeight();
            heightsObserved += 1;
            totalHeightSum += currentHeight;
            // record first FIRST_HEIGHTS_OBSERVED heights
            if (heightsObserved <= FIRST_HEIGHTS_OBSERVED) {
                startHeights.add(currentHeight);
            }
            // place the current height in its respective measurement bin
            placeHeightInBin(currentHeight, bin0, bin1, bin2, bin3, bin4);
        }
        // Accounting for the most frequently observed heights within a measurement range,
        // find the bin with most height measurements and calculate the average height from them
        double mostFrequentHeightAverage = calculateMostFrequentHeightAverage(bin0, bin1, bin2, bin3, bin4);

        // get average of first FIRST_HEIGHTS_OBSERVED heights
        for (double height : startHeights) {
            firstObservedSum += height;
        }
        double startHeightAvg = firstObservedSum / startHeights.size();

        // if the first observed heights are similar to the most observed heights, take the average
        // if not, disregard the first observed heights
        double heightAvg = 0.0;
        if (Math.abs(mostFrequentHeightAverage - startHeightAvg) < START_VS_FREQ_DIFF) {
            heightAvg = (mostFrequentHeightAverage + startHeightAvg) / 2;
        } else {
            heightAvg = mostFrequentHeightAverage;
        }

        return heightAvg;
    }
    
    @Override
    public int compareTo(Person user) {
        return this.getStartDateTime().compareTo(user.getStartDateTime());
    }

    @Override
    public String toString() { 
        StringBuilder returnString = new StringBuilder();
        returnString.append("User " + this.id + "; height: "+ df.format(this.getHeight()) + " from (" + this.getStartDateTime() + " to " + this.getEndDateTime() + ")\n");
//        returnString.append("User " + this.id + "; height: "+ df.format(this.getHeight()) + " times: " + this.locationTimeStamps);
        return returnString.toString();
    }
}
