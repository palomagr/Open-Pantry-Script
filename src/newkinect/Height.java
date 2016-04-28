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
import java.util.List;
import java.util.Scanner;
import javafx.geometry.Point3D;

public class Height {
    //
    //Paloma's Height: 1.73 m
    // check gestures, locations of spine, knees, etc
    // can check for sitting
    // check if not moving

    // read speed every second
    //
    // Hospital Kinect location
    //KINECT_HEIGHT = 2.75; //in meters
    //KINECT_ANGLE_HORIZONTAL = Math.toRadians(45); 
    //KINECT_ANGLE_VERTICAL = Math.toRadians(80); 

    // Paloma's Office Kinect Location
    //KINECT_HEIGHT = 1.42; //in meters
    //KINECT_ANGLE_HORIZONTAL = Math.toRadians(0); 
    //KINECT_ANGLE_VERTICAL = Math.toRadians(83); 

    final static double MIN_WALKING_SPEED = 0.0006; // ~2ft/sec
    final static double MAX_PERSON_HEIGHT = 2.1336; //7ft
    final static double HEAD_DIFFERENCE = 0.09; //in meters, since kinect reads center of head for head joint
    final static int SECONDS_ABSENT_NEW_PERSON = 7;
    final static double DISTANCE_NEW_PERSON = 1.0; //in meters
    final static double KINECT_HEIGHT = 2.75; //in meters
    final static double KINECT_ANGLE_HORIZONTAL = Math.toRadians(45); 
    final static double KINECT_ANGLE_VERTICAL = Math.toRadians(80); 
    final static double NUMB_OF_TABS_SKELETON = 176;
    final static String[] ID_DELIMS = {"!", "@", "#", "$", "%", "^"};

    public static void main(String[] args) {
        try {
            System.out.println("Head Height Data: ");
            String rawHeadDataPath = "src/Hospital_Data/Paloma_Cleaned_Skeleton/sk wo noise 2.txt";
            System.out.println("\nNow for skeleton height data:");
            String rawSkelDataPath = "src/Hospital_Data/Paloma_Cleaned_Skeleton/sk wo noise 2.txt";
            File organizedSkelFile = organizeRawSkelData(rawSkelDataPath);
            List<UserHeightTimestamp> processedSkelData = processOrganizedSkelData(organizedSkelFile, rawSkelDataPath);
            //            double skelHeightFound = getSkeletonHeight(processedSkelData);
            //            System.out.println("Computed height from detected joints:" + skelHeightFound);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create a BufferedWriter object that writes to a new file with the path name ending with an added modifier
     * @param path the path name of the new file to be written to
     * @param modifier the added string added at the end of the path name to make the file name unique
     * @return the created BufferedWriter for this new file
     * @throws IOException
     */
    public static BufferedWriter getBufferedWriter(String path, String modifier) throws IOException {
        File individualFile = new File(path.substring(0, path.lastIndexOf("."))
                + " " + modifier + ".txt");
        individualFile.createNewFile();
        FileWriter writer = new FileWriter(individualFile);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        return buffWriter;
    }

    /**
     * Create LocalDateTime object from string of format "year_month_day_hour_minute_second_millisecond"
     * 2016_03_01_11_48_46_623
     * @param dateTimeStr the date in the specified format
     * @return the LocalDateTime representation of the input date string
     */
    public static LocalDateTime getLocalDateTime(String dateTimeStr) {
        dateTimeStr = dateTimeStr.replace("_", " ");
        Scanner scan = new Scanner(dateTimeStr);
        LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //year, month
                Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //day, hour
                Integer.parseInt(scan.next()), Integer.parseInt(scan.next()), //minute, second
                Integer.parseInt(scan.next())*1000000); //nanosec = millisec*1,000,000
        scan.close();
        return dateTime;
    }



    /**
     * returns true on an empty string
     * @param line the string line being analyzed
     * @return true if string is empty
     */
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

    /**
     * 
     * @param kinectCoord
     * @param kinectAngleVert
     * @param kinectAngleHoriz
     * @param kinectHeight
     * @return
     */
    public static Point3D getRealCoord(final Point3D kinectCoord, final double kinectAngleVert, 
            final double kinectAngleHoriz, final double kinectHeight) {

        return getRealCoord(kinectCoord.getX(),kinectCoord.getY(),kinectCoord.getZ(), kinectAngleVert,
                kinectAngleHoriz, kinectHeight);
    }

    /**
     * Take coordinates read by kinect as well as the kinect's height and angles to translate coordinates
     * with respect to kinect into real world coordinates. The x-axis extends along the wall right of the kinect,
     * the z-axis extends along the wall left of the kinect, and the y-axis runs up from the ground to the ceiling
     * with respect to the corner where the kinect is located
     * @param x original x coordinate
     * @param y original y coordinate
     * @param z original z coordinate
     * @param kinectAngleVert the vertical angle of the kinect
     * @param kinectAngleHoriz the horizontal angle of the kinect
     * @param kinectHeight the height of the kinect's position
     * @return real world coordinates
     */
    public static Point3D getRealCoord(final double x, final double y, final double z,
            final double kinectAngleVert, final double kinectAngleHoriz, final double kinectHeight) {
        //calculating x and z using basic trig
        double angleFromWallHorz = kinectAngleHoriz - Math.atan2(x,z);
        double distanceFromKinectXZ = Math.sqrt(Math.pow(z, 2) + Math.pow(x, 2));
        double realX = distanceFromKinectXZ*Math.sin(angleFromWallHorz); 
        double realZ = distanceFromKinectXZ*Math.cos(angleFromWallHorz);

        //calculating y using law of cosines
        double angleFromWallVert = Math.atan2(y,z) + kinectAngleVert;
        double distanceFromKinectYZ = Math.sqrt(Math.pow(z,2) + Math.pow(y,2));
        double r = Math.sqrt(Math.pow(distanceFromKinectYZ,2) + Math.pow(kinectHeight,2) 
        - 2*distanceFromKinectYZ*kinectHeight*Math.cos(angleFromWallVert));
        double angleOppositeDFK = Math.acos((Math.pow(kinectHeight,2)+Math.pow(r,2)-Math.pow(distanceFromKinectYZ,2))/(2*kinectHeight*r));
        double theta = Math.PI/2.0-angleOppositeDFK;
        double realY = r*Math.sin(theta);

        return new Point3D(realX, realY, realZ);
    }

    /**
     * Return the point in 3d space read by the kinect that is to be read next by the scanner
     * @param scan The scanner reading a line
     * @param kinectAngleVert the vertical angle of the kinect
     * @param kinectAngleHoriz the horizontal angle of the kinect
     * @param kinectHeight the height of the kinect
     * @return the point next read by the scanner
     */
    private static Point3D readNextXYZ(Scanner scan) {
        Point3D realCoords = getRealCoord(Double.parseDouble(scan.next()), 
                Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), 
                KINECT_ANGLE_VERTICAL, KINECT_ANGLE_HORIZONTAL, KINECT_HEIGHT);

        return realCoords;
    }
    /**
     * Returns a midpoint in 3d space between two points in 3d space
     * @param first the first point
     * @param second the second point
     * @return a point located at the computed average distance between the two points
     */
    public static Point3D avgPoint(Point3D first, Point3D second) {
        double avgX = (first.getX() + second.getX()) / 2;
        double avgY = (first.getY() + second.getY()) / 2;
        double avgZ = (first.getZ() + second.getZ()) / 2;

        return new Point3D(avgX, avgY, avgZ);

    }

    /**
     * Get head, neck, spine_shoulder, spine-mid, spine-base, hip_left, hip_right, knee_left, knee_right,
     * ankle_left, ankle_right, foot_left, foot_right coordinates at times of detection
     * @param rawDataPath the txt file with skeleton data read by the kinect
     * @return a new txt file with the user followed by the x,y,z coordinates for their read head, neck, spine_shoulder, spine-mid, spine-base, hip_left, hip_right, knee_left, knee_right,
     *         ankle_left, ankle_right, foot_left, and foot_right and the time they were detected
     * @throws IOException
     */
    public static File organizeRawSkelData(final String rawDataPath) throws IOException {
        System.out.println("Organizing Skeleton Data...");
        // create readers and writers
        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath); 
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 

        String line = buffReader.readLine();
        //        String[] prevLines = new String[ID_DELIMS.length]; Arrays.fill(prevLines,"");
        //        LocalDateTime prevTimeStamp = LocalDateTime.MIN;
        while(line != null) { 

            int numberTabs = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if( c == '\t') {
                    numberTabs += 1;
                }
            }
            if (numberTabs == NUMB_OF_TABS_SKELETON) { 
                LocalDateTime timeStamp = getLocalDateTime(line.substring(line.lastIndexOf("\t")+1));
                String lineWOTimeID = line.substring(0,line.lastIndexOf("\t")-1).trim(); 
                int userID = Character.getNumericValue(line.charAt(line.lastIndexOf("\t")-1));
                //                if(!lineWOTimeID.equals(prevLines[userID])) { //check for noise
                if(!onlyZeros(lineWOTimeID)) { //check for useless data
                    //                        if(!MultiUser.withinReads(prevTimeStamp, timeStamp)) {
                    //                            buffWriter.write(prevTimeStamp + "\n");
                    //                        }
                    Scanner scan = new Scanner(line);

                    Point3D realHeadCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realNeckCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realSpineShoulderCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realSpineMidCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realSpineBaseCoords = readNextXYZ(scan);
                    for (int i =0; i<18; i++) {
                        scan.next();
                    }

                    Point3D realHipRightCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realHipLeftCoords = readNextXYZ(scan);
                    for (int i =0; i<74; i++) {
                        scan.next();
                    }

                    Point3D realKneeRightCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realAnkleRightCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realFootRightCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realKneeLeftCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realAnkleLeftCoords = readNextXYZ(scan);
                    for (int i =0; i<4; i++) {
                        scan.next();
                    }

                    Point3D realFootLeftCoords = readNextXYZ(scan);

                    buffWriter.write("User: " + userID + ";" + "\t" + 
                            realHeadCoords.getX() + "\t" + realHeadCoords.getY() + "\t" + realHeadCoords.getZ() + "\t" +
                            realNeckCoords.getX() + "\t" + realNeckCoords.getY() + "\t" + realNeckCoords.getZ() + "\t" +
                            realSpineShoulderCoords.getX() + "\t" + realSpineShoulderCoords.getY() + "\t" + realSpineShoulderCoords.getZ() + "\t" +
                            realSpineMidCoords.getX() + "\t" + realSpineMidCoords.getY() + "\t" + realSpineMidCoords.getZ() + "\t" +
                            realSpineBaseCoords.getX() + "\t" + realSpineBaseCoords.getY() + "\t" + realSpineBaseCoords.getZ() + "\t" +
                            realHipRightCoords.getX() + "\t" + realHipRightCoords.getY() + "\t" + realHipRightCoords.getZ() + "\t" +
                            realHipLeftCoords.getX() + "\t" + realHipLeftCoords.getY() + "\t" + realHipLeftCoords.getZ() + "\t" +
                            realKneeRightCoords.getX() + "\t" + realKneeRightCoords.getY() + "\t" + realKneeRightCoords.getZ() + "\t" +
                            realAnkleRightCoords.getX() + "\t" + realAnkleRightCoords.getY() + "\t" + realAnkleRightCoords.getZ() + "\t" +
                            realFootRightCoords.getX() + "\t" + realFootRightCoords.getY() + "\t" + realFootRightCoords.getZ() + "\t" +
                            realKneeLeftCoords.getX() + "\t" + realKneeLeftCoords.getY() + "\t" + realKneeLeftCoords.getZ() + "\t" +
                            realAnkleLeftCoords.getX() + "\t" + realAnkleLeftCoords.getY() + "\t" + realAnkleLeftCoords.getZ() + "\t" +
                            realFootLeftCoords.getX() + "\t" + realFootLeftCoords.getY() + "\t" + realFootLeftCoords.getZ() + "\t" +
                            timeStamp + "\n");
                    scan.close();
                }
            }
            line = buffReader.readLine();
        } 

        buffReader.close();
        buffWriter.close();
        return new File(rawDataPath.substring(0, rawDataPath.lastIndexOf("."))
                + " " + modifier + ".txt");
    }

    /**
     * Read the organized txt file of the skeleton joints of the user and both return
     * and create a file containing the calculated height of the user at different timestamps
     * @param organizedData the organized txt file with coordinates of user skeleton joints
     * @param rawDataPath the data path for the new file containing the heights at different times
     * @return the calculated heights of the user at different times
     * @throws IOException
     */
    public static List<UserHeightTimestamp> processOrganizedSkelData(final File organizedData, final String rawDataPath) throws IOException {
        System.out.println("Processing Skeleton Data...");
        // create reader and writer
        String modifier = "skeleton_locations";
        FileReader reader = new FileReader(organizedData);
        BufferedReader buffReader = new BufferedReader(reader);
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier);
        List<UserHeightTimestamp> processedData = new ArrayList<>();

        Point3D lastCom = new Point3D(1000.,1000.,1000.);
        LocalDateTime lastDateTime = LocalDateTime.MIN;

        int m = 0;

        String line = buffReader.readLine(); 
        while(line != null) {
            LocalDateTime dateTime = LocalDateTime.parse(line.substring(line.lastIndexOf("-")-7));

            Scanner scan = new Scanner(line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).trim());
            
            Point3D head = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()) + HEAD_DIFFERENCE,
                    Double.parseDouble(scan.next()));
            Point3D neck = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D spineShoulder = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D spineMid = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D spineBase = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D hipRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D hipLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D kneeRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D ankleRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D footRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D kneeLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D ankleLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D footLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));

//            buffWriter.write(ID_DELIMS[userID] + "\t" + 
//                  realSpineBaseCoords.getX() + "\t" + realSpineBaseCoords.getY() + "\t" + realSpineBaseCoords.getZ() + "\t" +
//                  realSpineMidCoords.getX() + "\t" + realSpineMidCoords.getY() + "\t" + realSpineMidCoords.getZ() + "\t" +
//                  realNeckCoords.getX() + "\t" + realNeckCoords.getY() + "\t" + realNeckCoords.getZ() + "\t" +
//                  realHeadCoords.getX() + "\t" + realHeadCoords.getY() + "\t" + realHeadCoords.getZ() + "\t" +
//                  realHipLeftCoords.getX() + "\t" + realHipLeftCoords.getY() + "\t" + realHipLeftCoords.getZ() + "\t" +
//                  realKneeLeftCoords.getX() + "\t" + realKneeLeftCoords.getY() + "\t" + realKneeLeftCoords.getZ() + "\t" +
//                  realAnkleLeftCoords.getX() + "\t" + realAnkleLeftCoords.getY() + "\t" + realAnkleLeftCoords.getZ() + "\t" +
//                  realFootLeftCoords.getX() + "\t" + realFootLeftCoords.getY() + "\t" + realFootLeftCoords.getZ() + "\t" +
//                  realHipRightCoords.getX() + "\t" + realHipRightCoords.getY() + "\t" + realHipRightCoords.getZ() + "\t" +
//                  realKneeRightCoords.getX() + "\t" + realKneeRightCoords.getY() + "\t" + realKneeRightCoords.getZ() + "\t" +
//                  realAnkleRightCoords.getX() + "\t" + realAnkleRightCoords.getY() + "\t" + realAnkleRightCoords.getZ() + "\t" +
//                  realFootRightCoords.getX() + "\t" + realFootRightCoords.getY() + "\t" + realFootRightCoords.getZ() + "\t" +
//                  realSpineShoulderCoords.getX() + "\t" + realSpineShoulderCoords.getY() + "\t" + realSpineShoulderCoords.getZ() + "\t" +
//                  timeStamp + "\n");
            
            Point3D currentCom = avgPoint(spineMid, spineBase);
            Skeleton currentSkeleton = new Skeleton(spineBase, spineMid, neck, head, hipLeft, kneeLeft, 
                    ankleLeft, footLeft, hipRight, kneeRight, ankleRight, footRight, spineShoulder);
            
            if (m>0) {
                double speed = lastCom.distance(currentCom) / (lastDateTime.until(dateTime, ChronoUnit.MILLIS));

                if (speed < MIN_WALKING_SPEED && 
                        spineBase.getY() > ((kneeRight.getY() + kneeLeft.getY()) /2) * 1.4) {

                    double totalHeight = currentSkeleton.getHeight();

                    processedData.add(new UserHeightTimestamp("user", totalHeight, dateTime));

                    scan.close();
                    buffWriter.write("" + totalHeight + "\n");
                }
            }
            lastDateTime = dateTime;
            m += 1;
            lastCom = new Point3D(currentCom.getX(), currentCom.getY(), currentCom.getZ());
            line = buffReader.readLine();
        }

        buffReader.close();
        buffWriter.close();
        return processedData; 
    }

    /**
     * Takes 13 skeleton joint locations in their respective order in terms of the Kinect joint type enumeration
     * 
     * spineBase:0, spineMid:1, neck:2, head:3, hipLeft:12, kneeLeft:13, ankleLeft:14, footLeft:15
     * hipRight:16, kneeRight:17, ankleRight:18, footRight:19, spineShoulder:20, 
     * 
     * @param spineBase
     * @param spineMid
     * @param neck
     * @param head
     * @param hipLeft
     * @param kneeLeft
     * @param ankleLeft
     * @param footLeft
     * @param hipRight
     * @param kneeRight
     * @param ankleRight
     * @param footRight
     * @param spineShoulder
     * 
     * @return returns the calculate height of the skeleton from joint measurements and distance calculations
     */
    public static double calculateInstanceSkeletonHeight(Point3D spineBase, Point3D spineMid, Point3D neck,
            Point3D head, Point3D hipLeft, Point3D kneeLeft, Point3D ankleLeft, Point3D footLeft,
            Point3D hipRight, Point3D kneeRight, Point3D ankleRight, Point3D footRight, Point3D spineShoulder) {
        
        double torsoHeight = head.distance(neck) + neck.distance(spineShoulder) + spineShoulder.distance(spineMid) +
                spineMid.distance(spineBase) + spineBase.distance(avgPoint(hipRight, hipLeft));

        double rightLegHeight = hipRight.distance(kneeRight) + kneeRight.distance(ankleRight) +
                ankleRight.distance(footRight);

        double leftLegHeight = hipLeft.distance(kneeLeft) + kneeLeft.distance(ankleLeft) + 
                ankleLeft.distance(footLeft);

        // take average of leg heights
        double totalHeight = torsoHeight + ((rightLegHeight + leftLegHeight)/2);
        
        return totalHeight;
    }
    
    /**
     * Calculate the estimated height of the user from the observed skeleton heights over the time span of detection
     * @param processedData the calculated heights of the user at different times
     * @return the calculated estimated height of the user
     */
    public static double getSkeletonHeight(final List<UserHeightTimestamp> processedData) {
        // Accounting for every single height measurement observed
        double totalHeightSum = 0.0;
        double totalObservationCount = processedData.size();
        // Accounting for the most frequently observed heights within a measurement range
        double mostFrequentHeightSum = 0.0;
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
        
        double HALF_METER = 0.5;
        double THREE_QUARTER_METER = 0.75;
        double ONE_METER = 1;
        double ONE_AND_QUARTER_METER = 1.25;
        double ONE_AND_HALF_METER = 1.5;
        double ONE_AND_THREE_QUARTER_METER = 1.75;
        double TWO_METER = 2;
             

        for (UserHeightTimestamp instance: processedData) {
            double currentHeight = instance.getHeight();
            heightsObserved += 1;
            totalHeightSum += currentHeight;
            // add first observed heights to 
            if (heightsObserved <= FIRST_HEIGHTS_OBSERVED) {
                startHeights.add(currentHeight);
            }
            if (currentHeight >= HALF_METER && currentHeight < ONE_METER) {
                bin0.add(currentHeight);
            } else if (currentHeight >= THREE_QUARTER_METER && currentHeight < ONE_AND_QUARTER_METER) {
                bin1.add(currentHeight);
            } else if (currentHeight >= ONE_METER && currentHeight < ONE_AND_HALF_METER) {
                bin2.add(currentHeight);
            } else if (currentHeight >= ONE_AND_QUARTER_METER && currentHeight < ONE_AND_THREE_QUARTER_METER) {
                bin3.add(currentHeight);
            } else if (currentHeight >= ONE_AND_HALF_METER && currentHeight < TWO_METER) {
                bin4.add(currentHeight);
            }
        }

        List<List<Double>> bins = Arrays.asList(bin0, bin1, bin2, bin3, bin4);
        // find bin with most height observations
        int maxBin = 0;
        for (int i = 1; i < bins.size(); i++){
            double binSize = bins.get(i).size();
            if (binSize > bins.get(maxBin).size()){
                maxBin = i;
            }
        }
        // take average of heights in biggest bin
        for (double height : bins.get(maxBin)) {
            mostFrequentHeightSum += height;
        }
        double freqHeightAvg = mostFrequentHeightSum / bins.get(maxBin).size();

        // account for heights read when person first appears, since likely to be their true height
        for (double height : startHeights) {
            firstObservedSum += height;
        }
        double startHeightAvg = firstObservedSum / startHeights.size();

        // the naively computed average of all heights observed
        double allHeightAvg = totalHeightSum / totalObservationCount;

        // if the first observed heights are similar to the most observed heights, take the average
        double heightAvg = 0.0;
        if (Math.abs(freqHeightAvg - startHeightAvg) < START_VS_FREQ_DIFF) {
            heightAvg = (freqHeightAvg + startHeightAvg) / 2;
        } else {
            heightAvg = freqHeightAvg;
        }

        //        System.out.println("Average of frequently observed heights is: " + freqHeightAvg);
        //        System.out.println("Average of first observed heights is: " + startHeightAvg);
        //        System.out.println("The naive average: " + allHeightAvg);
        //        System.out.println("The revised average: " + heightAvg);

        return heightAvg;
    }


    /**
     * 
     * @param rawDataPath
     * @return
     * @throws IOException
     */
    public static File organizeRawHeadData(final String rawDataPath) throws IOException {
        System.out.println("Organizing Head Data...");

        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath); 
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 

        String line = "";

        while(line != null) { 
            line = buffReader.readLine();
            if(line != null && line.length() > 220) { 
                LocalDateTime timeStamp = getLocalDateTime(line.substring(line.lastIndexOf("\t")+1));
                String lineWOTimeID = line.substring(0,line.lastIndexOf("\t")-1).trim(); 
                int userID = Character.getNumericValue(line.charAt(line.lastIndexOf("\t")-1));

                if(!onlyZeros(lineWOTimeID)) { //check for useless data
                    Scanner scan = new Scanner(line);
                    Point3D realHeadCoords = getRealCoord(Double.parseDouble(scan.next()), 
                            Double.parseDouble(scan.next()), Double.parseDouble(scan.next()), 
                            KINECT_ANGLE_VERTICAL, KINECT_ANGLE_HORIZONTAL, KINECT_HEIGHT);
                    buffWriter.write("User: " + userID + ";" + "\t" + realHeadCoords.getX() + "\t" 
                            + realHeadCoords.getY() + "\t" + realHeadCoords.getZ() + "\t" + timeStamp + "\n");
                    scan.close();
                }
            }
        } 

        buffReader.close();
        buffWriter.close();
        return new File(rawDataPath.substring(0, rawDataPath.lastIndexOf("."))
                + " " + modifier + ".txt");
    }

    public static List<UserHeightTimestamp>[] processOrganizedHeadData(final String rawDataPath) throws IOException {
        System.out.println("Processing Head Data...");

        String modifier = "head_locations";
        FileReader reader = new FileReader(rawDataPath);
        BufferedReader buffReader = new BufferedReader(reader);
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier);
        List<UserHeightTimestamp>[] processedData = new ArrayList[ID_DELIMS.length];
        for (int i = 0; i < processedData.length; i++) {
            processedData[i] = new ArrayList<>();
        }

        buffReader.readLine(); // skip first timestamp
        String line = buffReader.readLine(); 
        while(line != null) {
            // read the time stamp of the line of data
            LocalDateTime dateTime = LocalDateTime.parse(line.substring(line.lastIndexOf("-")-7));
            // Check if line has user x data; if so, add x, x's height, and time stamp to list and write to file
            for(int i = 0; i < ID_DELIMS.length; i++) {
                if(line.contains(ID_DELIMS[i])) {
                    Scanner scan = new Scanner(line.substring(line.indexOf(ID_DELIMS[i])+1, line.lastIndexOf(ID_DELIMS[i])).trim());
                    Point3D point = new Point3D(Double.parseDouble(scan.next()), 
                            Double.parseDouble(scan.next()) + HEAD_DIFFERENCE, 
                            Double.parseDouble(scan.next()));
                    // add the user, read head location, and the time stamp to the list
                    processedData[i].add(new UserHeightTimestamp(ID_DELIMS[i], point.getY(), dateTime));
                    scan.close();
                    // write the user, read height, and the time stamp it was read out to a new file
                    buffWriter.write("User " + ID_DELIMS[i] + ": " + point.getY() + "\t");
                }
            }
            buffWriter.write("\n");
            line = buffReader.readLine();
        }

        buffReader.close();
        buffWriter.close();
        return processedData; 
    }

    public static List<Double> getHeadHeight(final List<UserHeightTimestamp>[] processedData, final String rawDataPath) {
        System.out.println("Computing User Heights");
        List<LocalDateTime> startDateTimes = new ArrayList<>();
        List<LocalDateTime> endDateTimes = new ArrayList<>(); 
        List<List<Double>> subjectHeights = new ArrayList<>();

        for(List<UserHeightTimestamp> singleUserData : processedData) {
            System.out.println("Looking at new user UserHeightTimestamp data");
            if(!singleUserData.isEmpty()) {
                List<Double> userHeights = new ArrayList<>();
                startDateTimes.add(singleUserData.get(0).getDateTime());
                for(int i = 1; i < singleUserData.size(); i++) {
                    LocalDateTime dateTime = singleUserData.get(i).getDateTime();
                    LocalDateTime prevDateTime = singleUserData.get(i).getDateTime();
                    Point3D currentLocation = singleUserData.get(i).getLocation();
                    Point3D prevLocation = singleUserData.get(i-1).getLocation();
                    double prevHeight = prevLocation.getY();

                    if (prevHeight < MAX_PERSON_HEIGHT) //filter heights/noise
                        userHeights.add(prevHeight);   

                    // check if next value of time stamp is longer than certain time period after current time stamp
                    // this would suggest a different user is now being detected
                    //                    if (false) {
                    if (prevDateTime.plusSeconds(SECONDS_ABSENT_NEW_PERSON).isBefore(dateTime)) { 
                        if(prevDateTime.plusMinutes(2).isAfter(dateTime)) { //re-entry check
                            Point3D vector = currentLocation.subtract(prevLocation);
                            // add this user's detected heights, entry time, departure time to data and reset values
                            if(!userHeights.isEmpty()) {
                                subjectHeights.add(userHeights);
                                userHeights = new ArrayList<>();
                                endDateTimes.add(prevDateTime);
                                startDateTimes.add(dateTime);
                            }
                        }
                    }
                    else {
                        if(!userHeights.isEmpty()) {
                            subjectHeights.add(userHeights);
                            userHeights = new ArrayList<>();

                            endDateTimes.add(prevDateTime);
                            startDateTimes.add(dateTime);
                        }
                    }
                }
                subjectHeights.add(userHeights);
                endDateTimes.add(singleUserData.get(singleUserData.size()-1).getDateTime());
            }
        }

        List<Double> usersHeights = new ArrayList<>();
        //look at each user
        for(int i = 0; i < subjectHeights.size(); i++) {
            System.out.println("subjectHeights.size: " + subjectHeights.size());
            //            System.out.println("Calculating height of new user!");
            double startHeightSum = 0.0;
            double startTotalNumber = 0.0;
            double freqHeightSum = 0.0;
            double freqTotalNumber = 0.0;
            double allHeightSum = 0.0;
            double allTotalNumber = subjectHeights.get(i).size();

            double START_VS_FREQ_DIFF = 0.2;
            double FIRST_HEIGHTS_SEEN = 30;  
            double heightsSeen = 0.0;
            // look at a detected user's total consecutively detected heights
            List<Double> currentUserHeights = subjectHeights.get(i);
            for(double currentHeight : currentUserHeights) {
                heightsSeen += 1;
                allHeightSum += currentHeight;

                if (heightsSeen <= FIRST_HEIGHTS_SEEN) {
                    startHeights.add(currentHeight);
                }
                if (currentHeight >= 0.5 && currentHeight < 1) {
                    twoToThree.add(currentHeight);
                } else if (currentHeight >= 0.75 && currentHeight < 1.25) {
                    threeToFour.add(currentHeight);
                } else if (currentHeight >= 1 && currentHeight < 1.5) {
                    fourToFive.add(currentHeight);
                } else if (currentHeight >= 1.25 && currentHeight < 1.75) {
                    fiveToSix.add(currentHeight);
                } else if (currentHeight >= 1.5 && currentHeight < MAX_PERSON_HEIGHT) {
                    sixToSeven.add(currentHeight);
                }
            }

            List<List<Double>> bins = Arrays.asList(twoToThree, threeToFour, fourToFive, fiveToSix,
                    sixToSeven);
            // find bin of heights with most readings
            int maxIndex = 0;
            for (int binNumb = 0; binNumb < bins.size(); binNumb++){
                double newNumber = bins.get(binNumb).size();
                if (newNumber > bins.get(maxIndex).size()){
                    maxIndex = binNumb;
                }
            }
            // take average of heights in biggest bin
            int biggestBin = maxIndex;
            for (double heightValue : bins.get(biggestBin)) {
                freqHeightSum += heightValue;
                freqTotalNumber += 1;
            }
            double freqHeightAvg = freqHeightSum / freqTotalNumber;

            // account for heights read when person first appears, since likely to be their true height
            for (double height : startHeights) {
                startHeightSum += height;
                startTotalNumber += 1;
            }
            double startHeightAvg = startHeightSum / startTotalNumber;

            // the naively computed average of all heights observed
            double allHeightAvg = allHeightSum / allTotalNumber;

            // if the first observed heights are similar to the most observed heights, take the average
            double heightAvg = 0.0;
            if (Math.abs(freqHeightAvg - startHeightAvg) < START_VS_FREQ_DIFF) {
                heightAvg = (freqHeightAvg + startHeightAvg) / 2;
            } else {
                heightAvg = freqHeightAvg;
            }
            //          System.out.println("Average of frequently observed heights is: " + freqHeightAvg);
            //        System.out.println("Average of first observed heights is: " + startHeightAvg);
            //        System.out.println("The naive average: " + allHeightAvg);
            //        System.out.println("The revised average: " + heightAvg);

            usersHeights.add(heightAvg);
        }
        // each user has been looked at
        for(int j = 0; j < startDateTimes.size(); j++) {
            System.out.println("User " + j + "; Entry time: " + startDateTimes.get(j) + "; height: " + usersHeights.get(j));
        }
        return usersHeights;
    }
}


    double startHeightSum = 0.0;
    double startTotalNumber = 0.0;
    double freqHeightSum = 0.0;
    double freqTotalNumber = 0.0;
    double allHeightSum = 0.0;
    double allTotalNumber = singleUserData.size();

    double START_VS_FREQ_DIFF = 0.2;
    double FIRST_HEIGHTS_SEEN = 30;  
    double heightsSeen = 0.0;



    for (UserHeightTimestamp location: processedData) {
        double currentHeight = location.getHeight();
        heightsSeen += 1;
        allHeightSum += currentHeight;

        if (heightsSeen <= FIRST_HEIGHTS_SEEN) {
            startHeights.add(currentHeight);
        }
        if (currentHeight >= 0.5 && currentHeight < 1) {
            twoToThree.add(currentHeight);
        } else if (currentHeight >= 0.75 && currentHeight < 1.25) {
            threeToFour.add(currentHeight);
        } else if (currentHeight >= 1 && currentHeight < 1.75) {
            fourToFive.add(currentHeight);
        } else if (currentHeight >= 1.5 && currentHeight < 2) {
            fiveToSix.add(currentHeight);
        } else if (currentHeight >= 1.75 && currentHeight < 2.5) {
            sixToSeven.add(currentHeight);
        }
    }

    List<List<Double>> bins = Arrays.asList(twoToThree, threeToFour, fourToFive, fiveToSix,
            sixToSeven);
    // find bin of heights with most readings
    int maxIndex = 0;
    for (int i = 1; i < bins.size(); i++){
        double newNumber = bins.get(i).size();
        if (newNumber > bins.get(maxIndex).size()){
            maxIndex = i;
        }
    }
    // take average of heights in biggest bin
    int biggestBin = maxIndex;
    for (double height : bins.get(biggestBin)) {
        freqHeightSum += height;
        freqTotalNumber += 1;
    }
    double freqHeightAvg = freqHeightSum / freqTotalNumber;

    // account for heights read when person first appears, since likely to be their true height
    for (double height : startHeights) {
        startHeightSum += height;
        startTotalNumber += 1;
    }
    double startHeightAvg = startHeightSum / startTotalNumber;

    // the naively computed average of all heights observed
    double allHeightAvg = allHeightSum / allTotalNumber;
//
//    // if the first observed heights are similar to the most observed heights, take the average
//    double heightAvg = 0.0;
//    if (Math.abs(freqHeightAvg - startHeightAvg) < START_VS_FREQ_DIFF) {
//        heightAvg = (freqHeightAvg + startHeightAvg) / 2;
//    } else {
//        heightAvg = freqHeightAvg;
//    }
//
//    //        System.out.println("Average of frequently observed heights is: " + freqHeightAvg);
//    //        System.out.println("Average of first observed heights is: " + startHeightAvg);
//    //        System.out.println("The naive average: " + allHeightAvg);
//    //        System.out.println("The revised average: " + heightAvg);
//
//    return heightAvg;
//}
//}
//
