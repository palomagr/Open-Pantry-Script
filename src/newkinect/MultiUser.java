package newkinect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;

public class MultiUser {

    private final static String[] ID_DELIMS = {"!", "@", "#", "$", "%", "^"};
    //filter values
    private final static int MILLIS_BETWEEN_READS = 10;
    private final static int SECONDS_ABSENT_NEW_PERSON = 7;
    private final static int REENTRY_LIMIT_SECONDS = 2*60; 
    private final static double DISTANCE_NEW_PERSON = 1.0; //in meters
    private final static double MAX_PERSON_HEIGHT = 2.0; //in meters
    private final static double ACCEPTABLE_HEIGHT_FLUX = 0.2; //in meters
    private final static double STATIC_OBJECT_MAX_DISTANCE = 0.05;
    private final static int STATIC_OBJECT_MIN_SECONDS = 60;
    private final static double KINECT_BLIND_SPOT_DISTANCE = 0.4;
    //kinect coordinate adjustments
    private final static double KINECT_HEIGHT = 2.75; //in meters
    private final static double KINECT_ANGLE_HORIZONTAL = Math.toRadians(45); 
    private final static double KINECT_ANGLE_VERTICAL = Math.toRadians(80); 
    private final static double COM_FRAC_HEIGHT = 1; 
    //location identifiers
    private final static int SHELF_ID = 0;
    private final static int MICROWAVE_ID = 1;
    private final static int TABLE_ID = 2;
    //room layout
    private final static Cube ROOM = new Cube(Point3D.ZERO, new Point3D(3.14, 2.75, 3.33)); // point coords in meters
    private final static Point3D aboveDoorOrigin = new Point3D(3.1, MAX_PERSON_HEIGHT, 2.22);
    private final static Cube ABOVE_DOOR = new Cube(aboveDoorOrigin, ROOM.max()); 
    private final static Cube[] INVALID_SPACES = { ABOVE_DOOR };
    
    public static void main(String[] args) {
        try {
            String rawDataPath = ".\\src\\text files\\OP head wo 0.txt";
            File organizedFile = organizeRawData(rawDataPath);
            List<PointInTime>[] processedData = processOrganizedData(organizedFile, rawDataPath);
            //List<PointInTime>[] processedData = processOrganizedData(new File(".\\src\\text files\\com 02-12 to 02-17 organized.txt"), rawDataPath);
            getStats(processedData, rawDataPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //with respect to the corner where the kinect is located
    //the x-axis extends along the wall right of the kinect 
    //the z-axis extends along the wall left of the kinect
    //the y-axis runs up from the ground to the ceiling
    private static Point3D getRealCoord(final double x, final double y, final double z) {
        //calculating x and z using basic trig
        double angleFromWallHorz = KINECT_ANGLE_HORIZONTAL - Math.atan2(x,z);
        double distanceFromKinectXZ = Math.sqrt(Math.pow(z, 2) + Math.pow(x, 2));
        double realX = distanceFromKinectXZ*Math.sin(angleFromWallHorz); 
        double realZ = distanceFromKinectXZ*Math.cos(angleFromWallHorz);
        
        //calculating y using law of cosines
        double angleFromWallVert = Math.atan2(y,z) + KINECT_ANGLE_VERTICAL;
        double distanceFromKinectYZ = Math.sqrt(Math.pow(z,2) + Math.pow(y,2));
        double r = Math.sqrt(Math.pow(distanceFromKinectYZ,2) + Math.pow(KINECT_HEIGHT,2) 
                - 2*distanceFromKinectYZ*KINECT_HEIGHT*Math.cos(angleFromWallVert));
        double angleOppositeDFK = Math.acos((Math.pow(KINECT_HEIGHT,2)+Math.pow(r,2)-Math.pow(distanceFromKinectYZ,2))/(2*KINECT_HEIGHT*r));
        double theta = Math.PI/2.0-angleOppositeDFK;
        double realY = r*Math.sin(theta);
        
        return new Point3D(realX, realY, realZ);
    }
    
    private static double getHeightFromCOM(Point3D com) {
        return com.getY() / COM_FRAC_HEIGHT;
    }
    
    private static File organizeRawData(final String rawDataPath) throws IOException {
        System.out.println("Organizing Data...");
        
        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath); 
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 
        
        String line = "";
        String[] prevLines = new String[ID_DELIMS.length]; Arrays.fill(prevLines,"");
        LocalDateTime prevTimeStamp = LocalDateTime.MIN;
        while(line != null) { 
            line = buffReader.readLine();
            if(line != null) { 
                LocalDateTime timeStamp = getLocalDateTime(line.substring(line.lastIndexOf("\t")+1));
                String lineWOTimeID = line.substring(0,line.lastIndexOf("\t")-1).trim(); 
                int userID = Character.getNumericValue(line.charAt(line.lastIndexOf("\t")-1));
                if(!lineWOTimeID.equals(prevLines[userID])) { //check for noise
                    if(!onlyZeros(lineWOTimeID)) { //check for useless data
                        if(!withinReads(prevTimeStamp, timeStamp)) {
                            buffWriter.write(prevTimeStamp + "\n");
                        }
                        Scanner scan = new Scanner(line);
                        Point3D realCoords = getRealCoord(Double.parseDouble(scan.next()), 
                                Double.parseDouble(scan.next()), Double.parseDouble(scan.next()));
                        if(inInvalidSpace(realCoords)) { 
                            scan.close(); 
                            continue; 
                        }
                        buffWriter.write(ID_DELIMS[userID] + "\t" + realCoords.getX() + "\t" 
                                + realCoords.getY() + "\t" + realCoords.getZ() + "\t" + ID_DELIMS[userID] + "\t");
                        scan.close();
                    }
                }
                prevLines[userID] = lineWOTimeID;
                prevTimeStamp = timeStamp;
            }
        } buffWriter.write(prevTimeStamp + "\n");
       
        buffReader.close();
        buffWriter.close();
        return new File(rawDataPath.substring(0, rawDataPath.lastIndexOf("."))
                + " " + modifier + ".txt");
    }
    
    //checks if two times are similar enough to be considered the same 
    private static boolean withinReads(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (startDateTime.until(endDateTime, ChronoUnit.DAYS) < 1) &&
               (startDateTime.until(endDateTime, ChronoUnit.HOURS) < 1) &&
               (startDateTime.until(endDateTime, ChronoUnit.MINUTES) < 1) &&
               (startDateTime.until(endDateTime, ChronoUnit.MILLIS) < MILLIS_BETWEEN_READS);
    }
    
    private static List<PointInTime>[] processOrganizedData(final File organizedData, final String rawDataPath) throws IOException {
        System.out.println("Processing Data...");
        
        FileReader reader = new FileReader(organizedData);
        BufferedReader buffReader = new BufferedReader(reader);
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, "number_people_in_room");
        @SuppressWarnings("unchecked")
        List<PointInTime>[] processedData = new ArrayList[ID_DELIMS.length];
        for (int i = 0; i < processedData.length; i++) {
            processedData[i] = new ArrayList<>();
        }
        
        String line = buffReader.readLine(); 
        while(line != null) {
            LocalDateTime dateTime = LocalDateTime.parse(line.substring(line.lastIndexOf("-")-7));
            int numPeople = 0;
            for(int i = 0; i < ID_DELIMS.length; i++) {
                if(line.contains(ID_DELIMS[i])) {
                    numPeople++;
                    Scanner scan = new Scanner(line.substring(line.indexOf(ID_DELIMS[i])+1, line.lastIndexOf(ID_DELIMS[i])).trim());
                    Point3D point = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), Double.parseDouble(scan.next()));
                    processedData[i].add(new PointInTime(point, dateTime));
                    scan.close();
                }
            }
            buffWriter.write(numPeople + "\t" + dateTime.toString().replace("T", "_").replace(":", "_").replace("-", "_") + "\n");
            line = buffReader.readLine();
        }
        
        //for(int i = 0; i < 1000; i++)
        //    System.out.println(processedData[0].get(i));
        buffReader.close();
        buffWriter.close();
        return processedData; 
    }
    
    private static void getStats(List<PointInTime>[] processedData, String rawDataPath) throws IOException {
        System.out.println("Getting Individual Stats...");
        List<List<LocalDateTime>> dateTimes = getIndividualStats(processedData, rawDataPath);
        List<LocalDateTime> startDateTimes = dateTimes.get(0);
        List<LocalDateTime> endDateTimes = dateTimes.get(1);
        System.out.println("Getting Activity Over Time...");
        getActivityOverTime(startDateTimes, endDateTimes, rawDataPath);
        System.out.println("\nDone!");
    }
    
    private static List<List<LocalDateTime>> getIndividualStats(List<PointInTime>[] processedData, String rawDataPath) throws IOException {
        List<PointInTime> startPointsInTime = new ArrayList<>();
        List<PointInTime> endPointsInTime = new ArrayList<>(); 
        List<List<Double>> subjectHeights = new ArrayList<>();
        
        for(List<PointInTime> singleUserData : processedData) {
            if(!singleUserData.isEmpty()) {
                List<Double> heights = new ArrayList<>();
                startPointsInTime.add(singleUserData.get(0));
                for(int i = 1; i < singleUserData.size(); i++) {
                    PointInTime currPointInTime = singleUserData.get(i);
                    PointInTime prevPointInTime = singleUserData.get(i-1);
                    
                    double height = getHeightFromCOM(prevPointInTime.getPoint());
                    //filter heights
                    if(height > 0 && height < MAX_PERSON_HEIGHT) 
                        heights.add(height);   
                    
                    long reentryTime = prevPointInTime.getDateTime().until(currPointInTime.getDateTime(), ChronoUnit.SECONDS);
                    //looking like possible new person
                    if (reentryTime > SECONDS_ABSENT_NEW_PERSON) {  
                        Point3D vector = currPointInTime.getPoint().subtract(prevPointInTime.getPoint());
                        double distanceXZ = Math.sqrt(Math.pow(vector.getX(),2) + Math.pow(vector.getZ(),2)); 
                        //re-entry check
                        if(reentryTime > REENTRY_LIMIT_SECONDS || (reentryTime <= REENTRY_LIMIT_SECONDS && distanceXZ > DISTANCE_NEW_PERSON)) { 
                            subjectHeights.add(heights);
                            heights = new ArrayList<>();
                            endPointsInTime.add(prevPointInTime);
                            startPointsInTime.add(currPointInTime);
                        }
                    }
                }
                subjectHeights.add(heights);
                endPointsInTime.add(singleUserData.get(singleUserData.size()-1));
            }
        }
        
        //average subject heights
        List<Double> avgHeights = new ArrayList<>();
        for(int i = 0; i < subjectHeights.size(); i++) {
            List<Double> heights = subjectHeights.get(i);
            if(heights.isEmpty()) {
                startPointsInTime.remove(i);
                endPointsInTime.remove(i);
            }
            else {
                double sum = 0;
                for(double height : heights) {
                    sum += height;
                }
                avgHeights.add(sum/heights.size());
            }
        }
        
        //filter blips
        for(int i = 0; i < startPointsInTime.size(); i++) { 
            //filter blips
            if(startPointsInTime.get(i).getDateTime().until(endPointsInTime.get(i).getDateTime(), ChronoUnit.SECONDS) < SECONDS_ABSENT_NEW_PERSON) {
                startPointsInTime.remove(i);
                endPointsInTime.remove(i);
                avgHeights.remove(i);
                i--;
            }
        }
        
        //combine users 
        for(int i = 1; i < startPointsInTime.size(); i++) { 
            PointInTime endPointInTime = endPointsInTime.get(i-1);
            PointInTime startPointInTime = startPointsInTime.get(i);
            long reentryTime = endPointInTime.getDateTime().until(startPointInTime.getDateTime(), ChronoUnit.SECONDS);
            Point3D vector = startPointInTime.getPoint().subtract(endPointInTime.getPoint());
            double distanceXZ = Math.sqrt(Math.pow(vector.getX(),2) + Math.pow(vector.getZ(),2)); 
            
            if(reentryTime <= REENTRY_LIMIT_SECONDS && 
                    distanceXZ <= DISTANCE_NEW_PERSON && 
                    withinHeightFlux(Arrays.asList(avgHeights.get(i-1), avgHeights.get(i)))) {
                endPointsInTime.remove(i-1);
                startPointsInTime.remove(i);
                avgHeights.set(i-1, (avgHeights.get(i-1) + avgHeights.get(i)) / 2.0);
                i--;
            }
        }
        
        //write to file
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, "individual");
        for(int i = 0; i < startPointsInTime.size(); i++) { //filter blips
            buffWriter.write("Person " + (i+1) + "\t" + startPointsInTime.get(i).getDateTime().toString().replace("T", "_").replace(":", "_").replace("-", "_") 
                             + "\t" + endPointsInTime.get(i).getDateTime().toString().replace("T", "_").replace(":", "_").replace("-", "_") + "\t" + "------\t" 
                             + "------\t"  + "-------\t" + startPointsInTime.get(i).getDateTime().until(endPointsInTime.get(i).getDateTime(), ChronoUnit.SECONDS) 
                             + "\tsec\t" + avgHeights.get(i) + "\n");
        }
        buffWriter.close();

        //return LocalDateTimes for fourther processing
        List<List<LocalDateTime>> dateTimes = Arrays.asList(getDateTimes(startPointsInTime), getDateTimes(endPointsInTime));
        return dateTimes;
    }
    
    private static List<LocalDateTime> getDateTimes(List<PointInTime> pointsInTime) {
        List<LocalDateTime> dateTimes = new ArrayList<>();
        for(PointInTime pointInTime : pointsInTime) {
            dateTimes.add(pointInTime.getDateTime());
        }
        return dateTimes;
    }
    
    private static boolean staticObject(List<PointInTime>[] processedData, PointInTime startPointInTime, PointInTime endPointInTime) {
        for(List<PointInTime> singleUserData : processedData) {
            if(singleUserData.contains(startPointInTime) && singleUserData.contains(endPointInTime)) {
                int startIndex = -1;
                int endIndex = -1;
                for(int i = 0; i < singleUserData.size(); i++) {
                    if(startPointInTime.equals(singleUserData.get(i).getDateTime())) {
                        startIndex = i;
                    }
                    else if(endPointInTime.equals(singleUserData.get(i).getDateTime())) {
                        endIndex = i;
                    }
                }
                PointInTime startPoint = singleUserData.get(startIndex);
                for(int i = startIndex+1; i <= endIndex; i++) {
                    if(startPoint.getPoint().distance(singleUserData.get(i).getPoint()) < STATIC_OBJECT_MAX_DISTANCE) {
                        if(startPoint.getDateTime().until(singleUserData.get(i).getDateTime(), ChronoUnit.SECONDS) > STATIC_OBJECT_MIN_SECONDS) {
                           return true;
                        }
                    }
                }
                break;
            }
        }
        return false;
    }
    
    private static boolean withinHeightFlux(List<Double> heights) {
        if(heights.size() > 1) {
            for(int i = 1; i < heights.size(); i++) {
                double height1 = heights.get(i);
                double height2 = heights.get(i-1);
                if (Math.abs(height1 - height2) > ACCEPTABLE_HEIGHT_FLUX) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static void getActivityOverTime(List<LocalDateTime> startDateTimes, List<LocalDateTime> endDateTimes, String rawDataPath) throws IOException {
        LocalDateTime maxEndDate = endDateTimes.get(0);
        for(LocalDateTime endDate : endDateTimes) {
            if(endDate.isAfter(maxEndDate)) {
                maxEndDate = endDate;
            }
        }
        
        Timespan totalTimeCollection = new Timespan(startDateTimes.get(0), maxEndDate);
        List<Timespan> dates = totalTimeCollection.split();
        List<Integer> numPeople = new ArrayList<Integer>(Collections.nCopies(dates.size(), 0));
        for(int i = 0; i < dates.size(); i++) {
            for (int j = 0; j < startDateTimes.size(); j++) {
                Timespan date = dates.get(i);
                if(date.contains(startDateTimes.get(j))) {
                    numPeople.set(i, numPeople.get(i)+1);
                }
            }
        }
        
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, "activity");
        for(int i = 0; i < numPeople.size(); i++) {
            buffWriter.write(dates.get(i).getStart().toLocalDate() + "\t" + numPeople.get(i).intValue() + "\n");
        }
        buffWriter.close();
    }
    
    private static BufferedWriter getBufferedWriter(String path, String modifier) throws IOException {
        File individualFile = new File(path.substring(0, path.lastIndexOf("."))
                + " " + modifier + ".txt");
        individualFile.createNewFile();
        FileWriter writer = new FileWriter(individualFile);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        return buffWriter;
    }
    
    private static boolean inInvalidSpace(Point3D point) {
        for(Cube space : INVALID_SPACES) {
            if(space.contains(point)) {
                return true;
            }
        }
        Point3D kinectLocation = new Point3D(0,0, KINECT_HEIGHT);
        if(kinectLocation.distance(point) <= KINECT_BLIND_SPOT_DISTANCE) {
            return true;
        }
        return false;
    }
    
    private static void timeNearObject(final int id) {
        Rectangle2D[] objects = { new Rectangle2D(0, 0, 0, 0), new Rectangle2D(0, 0, 0, 0),
                new Rectangle2D(0, 0, 0, 0) };
        
        Point3D point = new Point3D(0, 0, 0);
        objects[id].contains(point.getX(), point.getZ());
    }
    
    private static LocalDateTime getLocalDateTime(String dateTimeStr) {
        dateTimeStr = dateTimeStr.replace("_", " ");
        Scanner scan = new Scanner(dateTimeStr);
        LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //year, month
                Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //day, hour
                Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //minute, second
                Integer.parseInt(scan.next())*1000000); //nanosec = millisec*1,000,000
        scan.close();
        return dateTime;
    }
    
    //returns true on empty string
    private static boolean onlyZeros(String line) {
        Scanner scan = new Scanner(line.trim());
        while(scan.hasNext()) {
            if(!scan.next().equals("0")) {
                scan.close();
                return false;
            }
        }
        scan.close();
        return true;
    }
}
