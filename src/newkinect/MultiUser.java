package newkinect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;

public class MultiUser {

    final static String[] ID_DELIMS= {"!", "@", "#", "$", "%", "^"};
    final static int TIME_ABSENT_NEW_PERSON = 2; //in seconds
    final static double KINECT_HEIGHT = 3.5; //in meters
    final static double KINECT_ANGLE_FROM_WALL = Math.PI/3.0; //60 degrees
    
    public static void main(String[] args) {
        try {
            //System.out.println(new File(".").getAbsolutePath());
            File organizedFile = organizeRawData(".\\src\\text files\\ml 02 25 u1.txt");
            List<String>[] processedData = processOrganizedData(organizedFile);
            getStats(processedData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static Point3D getRealCoord(final Point3D kinectCoord) {
        return getRealCoord(kinectCoord.getX(),kinectCoord.getY(),kinectCoord.getZ());
    }
    
    private static Point3D getRealCoord(final double x, final double y, final double z) {
        double angleFromWall = Math.atan2(y,z) + KINECT_ANGLE_FROM_WALL;
        double distanceFromKinect = Math.sqrt(Math.pow(z,2) + Math.pow(y,2));
        double r = Math.sqrt(Math.pow(distanceFromKinect,2) + Math.pow(KINECT_HEIGHT,2) 
                - 2*distanceFromKinect*KINECT_HEIGHT*Math.cos(angleFromWall));
        double angleOppositeDFK = Math.acos((Math.pow(KINECT_HEIGHT,2)+Math.pow(r,2)-Math.pow(distanceFromKinect,2))/(2*KINECT_HEIGHT*r));
        double theta = Math.PI/2.0-angleOppositeDFK;
        return new Point3D(x,r*Math.sin(theta),r*Math.cos(theta));
    }
    
    private static File organizeRawData(final String rawDataPath) throws IOException {
        FileReader reader = new FileReader(rawDataPath);
        BufferedReader buffReader = new BufferedReader(reader);
        File organizedFile = new File(rawDataPath.substring(0, rawDataPath.lastIndexOf(".")) 
                + " organized.txt");
        organizedFile.createNewFile();
        FileWriter writer = new FileWriter(organizedFile);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        
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
        return organizedFile;
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
    
    private static void getStats(List<String>[] processedData) {
        List<LocalDateTime> startDateTime = new ArrayList<LocalDateTime>();
        List<LocalDateTime> endDateTime = new ArrayList<LocalDateTime>();    
                
        for(List<String> singleUserData : processedData) {
            if(!singleUserData.isEmpty()) {
                startDateTime.add(getLocalDateTime(singleUserData.get(0).substring(singleUserData.get(0).indexOf("_")-4)));
                for(int i = 1; i < singleUserData.size(); i++) {
                    LocalDateTime dateTime = getLocalDateTime(singleUserData.get(i).substring(singleUserData.get(i).indexOf("_")-4));
                    LocalDateTime prevDateTime = getLocalDateTime(singleUserData.get(i-1).substring(singleUserData.get(i-1).indexOf("_")-4));
                    if (prevDateTime.plusSeconds(TIME_ABSENT_NEW_PERSON).isBefore(dateTime)) {
                        endDateTime.add(prevDateTime);
                        startDateTime.add(dateTime);
                    }
                }
                endDateTime.add(getLocalDateTime(singleUserData.get(singleUserData.size()-1).substring(singleUserData.get(singleUserData.size()-1).indexOf("_")-4)));
            }
        }

        for(int i = 0; i < startDateTime.size(); i++) { 
            System.out.println("Person " + (i+1) + " came in at " + startDateTime.get(i) 
                                + " and left at " + endDateTime.get(i) + " for a total time of " 
                                + getTimeDiff(startDateTime.get(i),endDateTime.get(i)));
        }
    }
    
    private static String getTimeDiff(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime timeDiff = endTime.minusHours(startTime.getHour());
        timeDiff = timeDiff.minusMinutes(startTime.getMinute());
        timeDiff = timeDiff.minusSeconds(startTime.getSecond());
        timeDiff = timeDiff.minusNanos(startTime.getNano());
        
        return timeDiff.getHour() + ":" + timeDiff.getMinute() + ":" + timeDiff.getSecond() 
               + "." + (timeDiff.getNano()/1000000); //millisec = nanosec / 1000000
    }
    
    private static void timeNearObject(int object) {
        Rectangle2D rect = new Rectangle2D(-object, object, object, object);
        Point3D point = new Point3D(object, object+1, object+2);
        rect.contains(point.getX(), point.getZ());
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
