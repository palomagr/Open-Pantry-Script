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
import java.util.StringTokenizer;

import javafx.geometry.Point3D;

public class NewHeight {
    //
    //Paloma's Height: 1.73 m
    // check gestures, locations of spine, knees, etc
    // can check for sitting
    // check if not moving

    // Hospital Kinect location
    //KINECT_HEIGHT = 2.75; //in meters
    //KINECT_ANGLE_HORIZONTAL = Math.toRadians(45); 
    //KINECT_ANGLE_VERTICAL = Math.toRadians(80); 

    // Paloma's Office Kinect Location
    //KINECT_HEIGHT = 1.42; //in meters
    //KINECT_HEIGHT_LEFT_SIDE = 1
    //KINECT_ANGLE_HORIZONTAL = Math.toRadians(0); 
    //KINECT_ANGLE_VERTICAL = Math.toRadians(83); 

    final static double MIN_WALKING_SPEED = 0.0006; // ~2ft/sec
    final static double MAX_PERSON_HEIGHT = 2.1336; //7ft
    final static double HEAD_DIFFERENCE = 0.09; //in meters, since kinect reads center of head for head joint
    final static int SECONDS_ABSENT_NEW_PERSON = 7;
    final static double DISTANCE_NEW_PERSON = 1.0; //in meters
    final static double KINECT_HEIGHT = 2.75; //in meters
    final static double KINECT_HEIGHT_LEFT_SIDE = 1.37;
    final static double KINECT_ANGLE_HORIZONTAL = Math.toRadians(45); 
    final static double KINECT_ANGLE_VERTICAL = Math.toRadians(80); 
    final static double NUMB_OF_TABS_SKELETON = 176;
    final static int MILLIS_BETWEEN_READS = 10;
    final static String[] ID_DELIMS = {"!", "@", "#", "$", "%", "^"};

    public static void main(String[] args) {
        try {
            //            String rawSkelDataPath = "src/height_test_data/SkeletonTextData/skeleton OpenPantry_3_Leo.txt";
            String rawSkelDataPath = "/Users/Leo/Documents/Open Pantry UROP/PalomaLeoTest/skeleton OpenPantry.txt";
//            String rawSkelDataPath = "/Users/Leo/Documents/Open Pantry UROP/hospitalTest/skeletonData.txt";
            List<SkeletonGroup> groups = organizeRawSkelData(rawSkelDataPath);
            List<Person> persons = processSkeletonData(groups);

            System.out.println(persons);
            
//          Point3D point1 = new Point3D(1.4375797150385736, 1.099510534685023, 1.7361516925915683);
//          Point3D point2 = new Point3D(1.4373637575565315, 1.099592633159295, 1.7366367749145297);
//          System.out.println(point1.distance(point2));
            
//            Skeleton testSkeleton = new Skeleton(point1, point2, point1, point2, point1, point2, point1, point2, point1, point1, point1, point1, point1);
//            LocalDateTime testTime = LocalDateTime.of(2016, 04, 19, 17, 36, 56, 738);
//            List<Person> testPersonList = new ArrayList<>();
//            Person person0 = new Person(0, new UserSkeletonTimestamp("0", testSkeleton, testTime));
//            testPersonList.add(person0);
//            System.out.println("1: " + testPersonList);
//            testPersonList.add(new Person());
//            System.out.println("2:" + testPersonList);
//            System.out.println("3:" + testPersonList);
            
            //            List<UserSkeletonTimestamp> processedSkelData = processOrganizedSkelData(groups, rawSkelDataPath);
            //            double skelHeightFound = getUserHeight(processedSkelData);
            //            System.out.println("Computed height from detected joints: " + skelHeightFound);

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
     * Return the point in 3d space observed by the kinect that is to be read next by the scanner
     * @param scan The scanner reading a line
     * @param kinectAngleVert the vertical angle of the kinect
     * @param kinectAngleHoriz the horizontal angle of the kinect
     * @param kinectHeight the height of the kinect
     * @return the point next read by the scanner
     */
    private static Point3D readNextXYZ(StringTokenizer lineTokens) {

        Point3D realCoords = getRealCoord(Double.parseDouble(lineTokens.nextToken()), 
                Double.parseDouble(lineTokens.nextToken()), Double.parseDouble(lineTokens.nextToken()), 
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
     * 
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    private static boolean withinReads(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (startDateTime.until(endDateTime, ChronoUnit.DAYS) < 1) &&
                (startDateTime.until(endDateTime, ChronoUnit.HOURS) < 1) &&
                (startDateTime.until(endDateTime, ChronoUnit.MINUTES) < 1) &&
                (startDateTime.until(endDateTime, ChronoUnit.MILLIS) < MILLIS_BETWEEN_READS);
    }

    /**
     * Get head, neck, spine_shoulder, spine-mid, spine-base, hip_left, hip_right, knee_left, knee_right,
     * ankle_left, ankle_right, foot_left, foot_right coordinates at times of detection
     * @param rawDataPath the txt file with skeleton data read by the kinect
     * @return a new txt file with the user followed by the x,y,z coordinates for their read head, neck, spine_shoulder, spine-mid, spine-base, hip_left, hip_right, knee_left, knee_right,
     *         ankle_left, ankle_right, foot_left, and foot_right and the time they were detected
     * @throws IOException
     */
    public static List<SkeletonGroup> organizeRawSkelData(final String rawDataPath) throws IOException {
        int NUMBER_OF_TOKENS_PER_READING = 177;
        int index = 0;
        List<SkeletonGroup> groupsPerTimestamp = new ArrayList<>();
        System.out.println("Organizing Skeleton Data...");

        // create readers and writers
        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath); 
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 

        //        String[] prevLines = new String[ID_DELIMS.length]; Arrays.fill(prevLines,"");
        LocalDateTime previousTimeStamp = LocalDateTime.MIN;

        String line = buffReader.readLine();
        int expectedUser = 0;
        List<UserSkeletonTimestamp> userReadingsInGroup = new ArrayList<>();
        SkeletonGroup currentObservedGroup = new SkeletonGroup(userReadingsInGroup);
        while(line != null) { 
            // count tokens in line
            StringTokenizer lineTokens = new StringTokenizer(line);
            // ensure it's a full reading with all joints
            if (lineTokens.countTokens() == NUMBER_OF_TOKENS_PER_READING) { 
                LocalDateTime timeStamp = getLocalDateTime(line.substring(line.lastIndexOf("\t")+1));
                String lineWOTimeID = line.substring(0,line.lastIndexOf("\t")-1).trim(); 
                int userID = Character.getNumericValue(line.charAt(line.lastIndexOf("\t")-1));
                //                if(!lineWOTimeID.equals(prevLines[userID])) { //check for noise
                if(!onlyZeros(lineWOTimeID)) { //check for useless data
                    //                    if(!withinReads(prevTimeStamp, timeStamp)) {

                    // If kinect finishes observing total number of users in room at given time, restart with first observed user at new time stamp
                    if(userID != expectedUser) {
                        buffWriter.write(currentObservedGroup + "\t" + previousTimeStamp + "\n");
                        // Update
                        groupsPerTimestamp.add(currentObservedGroup);
                        currentObservedGroup = new SkeletonGroup(userReadingsInGroup);
                        // refresh
                        expectedUser = 0;
                    }
                    //READ LINE AND CREATE SKELETON OBJECT
                    Point3D realSpineBaseCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realSpineMidCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realNeckCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realHeadCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<60; i++) { lineTokens.nextToken(); }
                    Point3D realHipLeftCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realKneeLeftCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realAnkleLeftCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realFootLeftCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realHipRightCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realKneeRightCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realAnkleRightCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realFootRightCoords = readNextXYZ(lineTokens);
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }
                    Point3D realSpineShoulderCoords = readNextXYZ(lineTokens);

                    Skeleton currentPerson = new Skeleton(realSpineBaseCoords, realSpineMidCoords, realNeckCoords, realHeadCoords, 
                            realHipLeftCoords, realKneeLeftCoords, realAnkleLeftCoords, realFootLeftCoords, realHipRightCoords, realKneeRightCoords,
                            realAnkleRightCoords, realFootRightCoords, realSpineShoulderCoords);

                    // Assign user Skeleton to user ID and timestamp
                    UserSkeletonTimestamp currentPersonAndTime = new UserSkeletonTimestamp(ID_DELIMS[userID], currentPerson, timeStamp);
                    // Add the user Skeleton, ID, time stamp combo to the group of observed users with the same(~) time stamp reading
                    currentObservedGroup = currentObservedGroup.addUser(currentPersonAndTime);
                    // Look forward to next user in room at one time stamp
                    expectedUser += 1;
                }
                previousTimeStamp = timeStamp;
            }
            line = buffReader.readLine();
        } 
        buffReader.close();
        buffWriter.close();
        return groupsPerTimestamp;

        //        return new File(rawDataPath.substring(0, rawDataPath.lastIndexOf("."))
        //                + " " + modifier + ".txt");
    }

    /**
     * Takes a listing of groups of users in room at specific times
     * @param groupsPerTimeStamp
     * @return the list of users (identified by integer ids) with their start times and end times as well as their times
     */
    public static List<Person> processSkeletonData(List<SkeletonGroup> groupsPerTimeStamp) {
        List<Person> listOfUsers = new ArrayList<>();
        Person person0 = new Person();
        Person person1 = new Person();
        Person person2 = new Person();
        Person person3 = new Person();
        Person person4 = new Person();
        Person person5 = new Person();

        SkeletonGroup previousGroup = new SkeletonGroup(new ArrayList<>());
        int previousGroupSize = 0;
        int currentGroupSize = 0;
        int assignedID = 0;
        int groupsSeen = 0;

        for (SkeletonGroup currentGroup : groupsPerTimeStamp) {
            previousGroupSize = previousGroup.getSize();
            currentGroupSize = currentGroup.getSize();
            groupsSeen += 1;

            if (currentGroupSize < previousGroupSize) {
                //Someone left
                System.out.println("Someone(s) Leave: (" + previousGroupSize + ") -> (" + currentGroupSize +") at " + currentGroup.getTimeOfGroup());
                List<Integer> usersThatLeft = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5)); 
                for (int i = 0; i < currentGroup.getSize(); i++) {
                    if (i == 0) {
                        Person oldPerson0 = person0;
                        Person currentPerson0 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (currentPerson0.getStartLocationTimestamp().isSamePerson(oldPerson0.getEndLocationTimestamp())) {
                            // THEY'RE THE SAME PERSON THANK GOD
//                            System.out.println("woah same person at: " + currentPerson0.getStartLocationTimestamp().getDateTime());
                            person0.addLocationTimeStamp(currentGroup.getSingleUser(i));
                            usersThatLeft.remove(0);
                        } else {
                            // NOT THE SAME PERSON...
//                            System.out.println("SWITCHAROO0");
                            for (int previousGroupIndex = i + 1; previousGroupIndex < previousGroupSize; previousGroupIndex += 1) {
                                // check each remaining person from previous group to see which one is now person0 in current group
                                if (previousGroupIndex == 1) {
                                    if (currentPerson0.getStartLocationTimestamp().isSamePerson(person1.getEndLocationTimestamp())) {
                                        person0 = new Person(person1);
                                    }
                                }
                            }

                        }
                    }
                    if (i == 1) {
                        Person oldPerson1 = person1;
                        Person currentPerson1 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (!(currentPerson1.getStartLocationTimestamp().isSamePerson(oldPerson1.getEndLocationTimestamp()))) {
                            System.out.println("SWITCHAROO");
                        }
                    }
                    else if (i == 2) {
                        Person oldPerson2 = person2;
                        Person currentPerson2 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (!(currentPerson2.getStartLocationTimestamp().isSamePerson(oldPerson2.getEndLocationTimestamp()))) {
                            System.out.println("1");
                        }
                    }
                    else if (i == 3) {
                        Person oldPerson3 = person3;
                        Person currentPerson3 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (!(currentPerson3.getStartLocationTimestamp().isSamePerson(oldPerson3.getEndLocationTimestamp()))) {
                            System.out.println("SWITCHAROO3");
                        }
                    }
                    else if (i == 4) {
                        Person oldPerson4 = person4;
                        Person currentPerson4 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (!(currentPerson4.getStartLocationTimestamp().isSamePerson(oldPerson4.getEndLocationTimestamp()))) {
                            System.out.println("SWITCHAROO4");
                        }
                    }
                    else if (i == 5) {
                        Person oldPerson4 = person0;
                        Person currentPerson4 = new Person(assignedID, currentGroup.getSingleUser(i));
                        if (!(currentPerson4.getStartLocationTimestamp().isSamePerson(oldPerson4.getEndLocationTimestamp()))) {
                            System.out.println("SWITCHAROO5");
                        }
                    }
                }
                for (Integer element : usersThatLeft) {
                    if (element == 0) {
                        person0 = new Person();
                    }
                    else if (element == 1) {
                        person1 = new Person();
                    }
                    else if (element == 2) {
                        person2 = new Person();
                    }
                    else if (element == 3) {
                        person3 = new Person();
                    }
                    else if (element == 4) {
                        person4 = new Person();
                    }
                    else if (element == 5) {
                        person5 = new Person();
                    }
                }

                previousGroup = currentGroup;

            } else if (currentGroupSize > previousGroupSize) {
                // Somebody joined
                System.out.println("Someone(s) Joins: (" + previousGroupSize + ") -> (" + currentGroupSize +") at " + currentGroup.getTimeOfGroup());
                // make a new person array
                int numberNewUsers = currentGroupSize - previousGroupSize;
                // create new instances for newly detected users
                for (int i = previousGroupSize; i < currentGroupSize; i++) {
                    if (i == 0) {
                        person0 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person0);
                    }
                    if (i == 1) {
                        person1 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person1);
                    }
                    if (i == 2) {
                        person2 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person2);
                    }
                    if (i == 3) {
                        person3 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person3);
                    }
                    if (i == 4) {
                        person4 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person4);
                    }
                    if (i == 5) {
                        person5 = new Person(assignedID, currentGroup.getSingleUser(i));
                        assignedID += 1;
                        listOfUsers.add(person5);
                    }
                }
                previousGroup = currentGroup;

            } else if (currentGroupSize == previousGroupSize){
                //check everyone is the same person
                for (int i = 0; i < currentGroupSize; i++) {
                    //                    System.out.println("first: " + previousGroup + "; second: " + currentGroup);
                    if (!(previousGroup.getSingleUser(i).isSamePerson((currentGroup.getSingleUser(i))))) {
                        //                        double distance = previousGroup.getSingleUser(i).getLocation().distance(currentGroup.getSingleUser(i).getLocation());
                        //                        double time = previousGroup.getSingleUser(i).getDateTime().until(currentGroup.getSingleUser(i).getDateTime(), ChronoUnit.MILLIS);
                        //                        double speed = distance / time;
                        continue;
                    } else {
                        if (i == 0) {
                            person0.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                        if (i == 1) {
                            person1.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                        if (i == 2) {
                            person2.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                        if (i == 3) {
                            person3.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                        if (i == 4) {
                            person4.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                        if (i == 5) {
                            person5.addLocationTimeStamp(currentGroup.getSingleUser(i));
                        }
                    }
                }
                //                System.out.println("Equal to:" + r);
            }
            previousGroup = currentGroup;
        }
//        for (Person person : listOfUsers) {
//            System.out.println(person);
//        }
        return listOfUsers;
    }

    /**
     * Read the organized txt file of the skeleton joints of the user and both return
     * and create a file containing the calculated height of the user at different timestamps
     * @param organizedData the organized txt file with coordinates of user skeleton joints
     * @param rawDataPath the data path for the new file containing the heights at different times
     * @return the calculated heights of the user at different times
     * @throws IOException
     */
    public static List<UserSkeletonTimestamp> processOrganizedSkelData(final File organizedData, final String rawDataPath) throws IOException {
        System.out.println("Processing Skeleton Data...");
        // create reader and writer
        String modifier = "skeleton_locations";
        FileReader reader = new FileReader(organizedData);
        BufferedReader buffReader = new BufferedReader(reader);
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier);
        List<UserSkeletonTimestamp> processedData = new ArrayList<>();
        //        List<UserSkeletonTimestamp>[] processedData = new ArrayList[ID_DELIMS.length];

        Point3D lastCom = new Point3D(1000.,1000.,1000.);
        LocalDateTime lastDateTime = LocalDateTime.MIN;

        int m = 0;

        String line = buffReader.readLine(); 
        while(line != null) {
            LocalDateTime dateTime = LocalDateTime.parse(line.substring(line.lastIndexOf("-")-7));
            StringTokenizer lineTokens = new StringTokenizer(line);
            int numberUsersDetected = lineTokens.countTokens();
            System.out.println(numberUsersDetected);
            Scanner scan = new Scanner(line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).trim());

            Point3D spineMid = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D spineBase = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D head = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()) + HEAD_DIFFERENCE,
                    Double.parseDouble(scan.next()));
            Point3D neck = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D hipLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D kneeLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D ankleLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D footLeft = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D hipRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D kneeRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D ankleRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));
            Point3D footRight = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));

            Point3D spineShoulder = new Point3D(Double.parseDouble(scan.next()), Double.parseDouble(scan.next()),
                    Double.parseDouble(scan.next()));

            Point3D currentCom = avgPoint(spineMid, spineBase);
            Skeleton currentSkeleton = new Skeleton(spineBase, spineMid, neck, head, hipLeft, kneeLeft, 
                    ankleLeft, footLeft, hipRight, kneeRight, ankleRight, footRight, spineShoulder);

            if (m>0) {
                double speed = lastCom.distance(currentCom) / (lastDateTime.until(dateTime, ChronoUnit.MILLIS));
                // make sure spinebase is higher than knees
                if (speed < MIN_WALKING_SPEED && 
                        spineBase.getY() > ((kneeRight.getY() + kneeLeft.getY()) /2)) {

                    double totalHeight = currentSkeleton.getHeight();

                    //                    processedData.add(new UserSkeletonTimestamp("user", totalHeight, dateTime));

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
}