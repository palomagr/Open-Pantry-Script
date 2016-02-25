package newkinect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;

public class MultiUser {

    final static String[] ID_DELIMS = {"!", "@", "#", "$", "%", "^"};
    final static int TIME_ABSENT_NEW_PERSON = 2; //in seconds
    final static double KINECT_HEIGHT = 2.75; //in meters
    final static double KINECT_ANGLE_HORIZONTAL = Math.PI/4.0; //45 degrees
    final static double KINECT_ANGLE_VERTICAL = Math.PI/3.0; //60 degrees
    final static int SHELF_ID = 0;
    final static int MICROWAVE_ID = 1;
    final static int TABLE_ID = 2;
    
    public static void main(String[] args) {
        try {
            String rawDataPath = ".\\src\\text files\\ml 02 25 u1.txt";
            //File organizedFile = organizeRawData(rawDataPath);
            //List<String>[] processedData = processOrganizedData(organizedFile);
            List<String>[] processedData = processOrganizedData(new File(".\\src\\text files\\ml 02 25 u1 organized.txt"));
            getStats(processedData, rawDataPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static Point3D getRealCoord(final Point3D kinectCoord) {
        return getRealCoord(kinectCoord.getX(),kinectCoord.getY(),kinectCoord.getZ());
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
        return com.getY() / 0.55;
    }
    
    private static File organizeRawData(final String rawDataPath) throws IOException {
        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath);
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 
        
        String line = "";
        String[] prevLines = new String[ID_DELIMS.length]; Arrays.fill(prevLines,"");
        String prevTimeStamp = "";
        while(line != null) {
            line = buffReader.readLine();
            if(line != null) {
                String timeStamp = line.substring(line.indexOf("_")-4);
                String lineWOTime = line.substring(0,line.indexOf("_")-4).trim();
                int userID = Character.getNumericValue(lineWOTime.charAt(lineWOTime.length()-1));
                if(!lineWOTime.equals(prevLines[userID])) { //check for noise
                    if(!onlyZeros(lineWOTime)) { //check for useless data
                        if(!timeStamp.equals(prevTimeStamp) && !prevTimeStamp.equals("")) {
                            buffWriter.write(prevTimeStamp + "\n");
                        }
                        Scanner scan = new Scanner(line);
                        Point3D realCoords = getRealCoord(Double.parseDouble(scan.next()), 
                                Double.parseDouble(scan.next()), Double.parseDouble(scan.next()));
                        buffWriter.write(ID_DELIMS[userID] + "\t" + realCoords.getX() + "\t" 
                                + realCoords.getY() + "\t" + realCoords.getZ() + "\t" + ID_DELIMS[userID] + "\t");
                        scan.close();
                    }
                }
                prevLines[userID] = lineWOTime;
                prevTimeStamp = timeStamp;
            }
        } buffWriter.write(prevTimeStamp + "\n");
       
        buffReader.close();
        buffWriter.close();
        return new File(rawDataPath.substring(0, rawDataPath.lastIndexOf("."))
                + " " + modifier + ".txt");
    }
    
    private static List<String>[] processOrganizedData(final File organizedData) throws IOException {
        FileReader reader = new FileReader(organizedData);
        BufferedReader buffReader = new BufferedReader(reader);
        List<String>[] processedData = new ArrayList[ID_DELIMS.length];
        for (int i = 0; i < processedData.length; i++) {
            processedData[i] = new ArrayList<>();
        }
        
        String line = buffReader.readLine(); 
        while(line != null) {
            for(int i = 0; i < ID_DELIMS.length; i++) {
                if(line.contains(ID_DELIMS[i])) {
                    processedData[i].add(line.substring(line.indexOf(ID_DELIMS[i])+1, line.lastIndexOf(ID_DELIMS[i])).trim() 
                            + "\t" + line.substring(line.indexOf("_")-4));
                }
            }
            line = buffReader.readLine();
        }
        buffReader.close();
        return processedData;
    }
    
    private static void getStats(List<String>[] processedData, String rawDataPath) throws IOException {
        List<List<LocalDateTime>> dateTimes = getIndividualStats(processedData, rawDataPath);
        List<LocalDateTime> startDateTimes = dateTimes.get(0);
        List<LocalDateTime> endDateTimes = dateTimes.get(1);
        getActivityOverTime(startDateTimes, endDateTimes, rawDataPath);
    }
    
    private static List<List<LocalDateTime>> getIndividualStats(List<String>[] processedData, String rawDataPath) throws IOException {
        List<LocalDateTime> startDateTimes = new ArrayList<LocalDateTime>();
        List<LocalDateTime> endDateTimes = new ArrayList<LocalDateTime>(); 
        
        for(List<String> singleUserData : processedData) {
            if(!singleUserData.isEmpty()) {
                startDateTimes.add(getLocalDateTime(singleUserData.get(0).substring(singleUserData.get(0).indexOf("_")-4)));
                for(int i = 1; i < singleUserData.size(); i++) {
                    LocalDateTime dateTime = getLocalDateTime(singleUserData.get(i).substring(singleUserData.get(i).indexOf("_")-4));
                    LocalDateTime prevDateTime = getLocalDateTime(singleUserData.get(i-1).substring(singleUserData.get(i-1).indexOf("_")-4));
                    if (prevDateTime.plusSeconds(TIME_ABSENT_NEW_PERSON).isBefore(dateTime)) {
                        endDateTimes.add(prevDateTime);
                        startDateTimes.add(dateTime);
                    }
                }
                endDateTimes.add(getLocalDateTime(singleUserData.get(singleUserData.size()-1).substring(singleUserData.get(singleUserData.size()-1).indexOf("_")-4)));
            }
        }
        
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, "individual");
        for(int i = 0; i < startDateTimes.size(); i++) {
            buffWriter.write("Person " + (i+1) + "\t" + startDateTimes.get(i) + "\t" + endDateTimes.get(i) + "\n");
        }
        buffWriter.close();
        
        List<List<LocalDateTime>> dateTimes = Arrays.asList(startDateTimes, endDateTimes);
        return dateTimes;
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
                if(date.contains(startDateTimes.get(j)) || date.contains(endDateTimes.get(j))) {
                    numPeople.set(i, numPeople.get(i)+1);
                }
            }
        }
        
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, "activity");
        for(int i = 0; i < numPeople.size(); i++) {
            buffWriter.write(dates.get(i).getStart().toLocalDate() + "\t" + numPeople.get(i).intValue() + "\n");
        }
        buffWriter.close();
        
        System.out.println(numPeople);
    }
    
    private static BufferedWriter getBufferedWriter(String path, String modifier) throws IOException {
        File individualFile = new File(path.substring(0, path.lastIndexOf("."))
                + " " + modifier + ".txt");
        individualFile.createNewFile();
        FileWriter writer = new FileWriter(individualFile);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        return buffWriter;
    }
    
    private static String getTimeDiff(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime timeDiff = endTime.minusHours(startTime.getHour());
        timeDiff = timeDiff.minusMinutes(startTime.getMinute());
        timeDiff = timeDiff.minusSeconds(startTime.getSecond());
        timeDiff = timeDiff.minusNanos(startTime.getNano());
        
        return timeDiff.getHour() + ":" + timeDiff.getMinute() + ":" + timeDiff.getSecond() 
               + "." + (timeDiff.get(ChronoField.MILLI_OF_SECOND)); 
    }
    
    private static void timeNearObject(int id) {
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
                Integer.parseInt(scan.next())*1000000); //nanosec = millisec*1000000
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
