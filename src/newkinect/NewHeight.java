package newkinect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javafx.geometry.Point3D;

/**
 * 
 * Functions that read the Kinect Skeleton data, parse it into user groups per time stamp, and organize it into specific users entering and
 * leaving the room at different times.
 *
 */

public class NewHeight {
    //Paloma's Height: 1.73 m

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
            String rawSkelDataPath = "/Users/Leo/Documents/Open Pantry UROP/PalomaLeoTest/skeleton OpenPantry.txt";
            List<SkeletonGroup> groups = organizeRawSkelData(rawSkelDataPath);
            List<Person> persons = processSkeletonData(groups);
            System.out.println(persons);
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
     * Takes Point3D coordinates read by kinect as well as the kinect's height and angles with respect to coordinate axes
     * to translate coordinates with respect to kinect into real world coordinates with respect to the room. 
     * The x-axis extends along the wall right of the kinect, the z-axis extends along the wall left of the kinect, and the 
     * y-axis runs up from the ground to the ceiling with respect to the corner where the kinect is located
     * @param kinectCoord x,y, and z coordinates read by Kinect
     * @param kinectAngleVert the vertical angle of the kinect
     * @param kinectAngleHoriz the horizontal angle of the kinect
     * @param kinectHeight the height of the kinect's position
     * @return real world coordinates with respect to room
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
     * @return real world coordinates with respect to room
     */
    public static Point3D getRealCoord(final double x, final double y, final double z,
            final double kinectAngleVert, final double kinectAngleHoriz, final double kinectHeight) {
        //calculating x and z using basic trigonometry
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
     * Read, parse, calculate, and return the next coordinates in the Kinect data
     * @param lineTokens the StringTokenizer that tokenizes the next 3 values in the input data (the x,y,z coordinates)
     * @return the real world coordinates computed from the next 3 coordinates read from the Kinect data
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
     * Get head, neck, spine_shoulder, spine-mid, spine-base, hip_left, hip_right, knee_left, knee_right,
     * ankle_left, ankle_right, foot_left, foot_right coordinates at each time stamp in original data. Create a skeleton object using these
     * coordinates and associate it with the time stamp of detection. For each group of skeletons (1 to 6) detected at once by the kinect,
     * place those skeletons in a group associated with the first detected skeleton's time stamp. Create a new text file listing each
     * group detected along with this time stamp.
     * 
     * @param rawDataPath the txt file with skeleton data read by the kinect
     * @return 2 Things: (1) a new txt file listing the groups of skeletons detected by the Kinect, with each followed by the time stamp of the first skeleton
     * in the group. (2) The list of these groups and their associated time stamps
     * @throws IOException
     */
    public static List<SkeletonGroup> organizeRawSkelData(final String rawDataPath) throws IOException {
        System.out.println("Organizing Skeleton Data...");
        // there should be 177 "tokens" per line in the original data
        int NUMBER_OF_TOKENS_PER_READING = 177;
        // create a new empty group
        List<SkeletonGroup> groupsPerTimestamp = new ArrayList<>();
        
        // create readers and writers
        String modifier = "organized";
        FileReader reader = new FileReader(rawDataPath); 
        BufferedReader buffReader = new BufferedReader(reader); 
        BufferedWriter buffWriter = getBufferedWriter(rawDataPath, modifier); 

        // initialize the previous read time stamp to be before all of the time stamps in the data
        LocalDateTime previousTimeStamp = LocalDateTime.MIN;

        String line = buffReader.readLine();
        // start with the expectation that the first user detected has user ID = 0
        int expectedUser = 0;
        // create a new empty list to contain the skeletons (potentially from 1 to 6) detected in a group by the kinect around the same time stamp
        // create a group object to contain this data
        SkeletonGroup currentObservedGroup = new SkeletonGroup(new ArrayList<>());

        while(line != null) { 
            // convert line into tokens
            StringTokenizer lineTokens = new StringTokenizer(line);
            // make sure the line has all of the joints, all of the data (number of tokens is correct)
            if (lineTokens.countTokens() == NUMBER_OF_TOKENS_PER_READING) { 
                LocalDateTime timeStamp = getLocalDateTime(line.substring(line.lastIndexOf("\t")+1));
                String lineWOTimeID = line.substring(0,line.lastIndexOf("\t")-1).trim(); 
                int userID = Character.getNumericValue(line.charAt(line.lastIndexOf("\t")-1));
                // make sure data isn't empty or just zeros
                if(!onlyZeros(lineWOTimeID)) {
                    // Check to see if the user ID of the skeleton coordinates just read is equal to the expected ID.
                    // For each group, the observed skeletons' IDs go from 0 to 5, so we keep expecting the next higher number as the next expected ID observed.
                    // When we see an ID that we don't expect, it means another round of observation has begun and its time for a new group with a new time stamp.
                    if(userID != expectedUser) {
                        buffWriter.write(currentObservedGroup + "\t" + previousTimeStamp + "\n");
                        // Add the observed group of skeletons with their associated time stamp to the list to be returned
                        groupsPerTimestamp.add(currentObservedGroup);
                        // start a new empty group
                        currentObservedGroup = new SkeletonGroup(new ArrayList<>());
                        // refresh the expected user ID back to 0, so we are now dealing with a user with ID 0
                        expectedUser = 0;
                    }
                    // OTHERWISE READ THE LINE AND CREATE SKELETON OBJECT
                    Point3D realSpineBaseCoords = readNextXYZ(lineTokens); // read joint coordinates
                    for (int i =0; i<4; i++) { lineTokens.nextToken(); }   // skip quaternion data
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

                    Skeleton currentSkeleton = new Skeleton(realSpineBaseCoords, realSpineMidCoords, realNeckCoords, realHeadCoords, 
                            realHipLeftCoords, realKneeLeftCoords, realAnkleLeftCoords, realFootLeftCoords, realHipRightCoords, realKneeRightCoords,
                            realAnkleRightCoords, realFootRightCoords, realSpineShoulderCoords);

                    // Assign the created skeleton to the appropriate user ID symbol corresponding to the expected user ID and the read time stamp
                    UserSkeletonTimestamp currentPersonAndTime = new UserSkeletonTimestamp(ID_DELIMS[userID], currentSkeleton, timeStamp);
                    // Add the user Skeleton, ID, time stamp combo (UserSkeletonTimestamp) to the group of observed users with the ~same time stamp reading
                    currentObservedGroup = currentObservedGroup.addUser(currentPersonAndTime);
                    // update to look forward to next expected user ID
                    expectedUser += 1;
                }
                previousTimeStamp = timeStamp;
            }
            line = buffReader.readLine();
        } 
        // don't forget to write the last group observed
        buffWriter.write(currentObservedGroup + "\t" + previousTimeStamp + "\n");
        // Add the last observed group of skeletons with their associated time stamp to the list to be returned
        groupsPerTimestamp.add(currentObservedGroup);
        buffReader.close();
        buffWriter.close();
        return groupsPerTimestamp;
    }

    /**
     * Takes a list of groups of users in room and each group's associated time stamps
     * @param groupsPerTimeStamp the list of groups, each with its associated time stamp
     * @return the list of users (identified by integer ids) with their start times and end times as well as their duration in the room
     */
    public static List<Person> processSkeletonData(List<SkeletonGroup> groupsPerTimeStamp) {
        List<Person> listOfUsers = new ArrayList<>();
        List<Person> currentPersons = new ArrayList<>();
        List<Person> newPersons = new ArrayList<>();
        // add new empty Person objects to the two lists of currently logged persons from the previous group and newly observed persons in the next group from the data
        for (int i = 0; i < 6; i++) {
            newPersons.add(i, new Person());
            currentPersons.add(i, new Person());
        }
        // Initialize an empty group to be the first previous group since no group from the data has been seen yet
        SkeletonGroup previousGroup = new SkeletonGroup(new ArrayList<>());
        int previousGroupSize = 0;
        int currentGroupSize = 0;
        int assignedID = 0;
        // look at each line of the data, looking at each group of users as UserSkeletonTimestamps
        // currentGroup is the next group in the data, consisting of UserSkeletonTimestamps
        // currentPersons is the list of Persons that are currently observed to be in the room
        // newPersons is the list of Persons in currentGroup
        for (SkeletonGroup currentGroup : groupsPerTimeStamp) {
            previousGroupSize = previousGroup.getSize();
            currentGroupSize = currentGroup.getSize();
            // SOMEONE LEFT
            if (currentGroupSize < previousGroupSize) { 
                System.out.println("Someone(s) Exited: (" + previousGroupSize + ") -> (" + currentGroupSize +") at " + currentGroup.getTimeOfGroup());
                // Create a list of possible users that exited (represented by user ID's) that we will narrow down to the correct answer
                List<Integer> usersThatExited = new ArrayList<>(); 
                for (int i = 0; i < previousGroup.getSize(); i++) {
                    usersThatExited.add(i);
                }
                // figure out which of the group members in the new group correspond to members of the old group, since they might have changed
                // For example, user 1 in the old group might now be user 0 in the new group, and we have to account for that
                for (int i = 0; i < currentGroup.getSize(); i++) {
                    // Initialize Person objects in newPersons from currentGroup
                    newPersons.set(i, new Person(assignedID, currentGroup.getSingleUser(i)));
                    if (newPersons.get(i).getStartLocationTimestamp().isSamePerson(currentPersons.get(i).getEndLocationTimestamp())) {
                        // THEY'RE THE SAME PERSON
                        // update currentPersons
                        currentPersons.get(i).addLocationTimeStamp(currentGroup.getSingleUser(i));
                        // remove the user from the list of users that possibly exited the room
                        usersThatExited.remove(i);
                    } else {
                        // NOT THE SAME PERSON...
                        for (int nextRemainingGroupIndex = 0; nextRemainingGroupIndex < previousGroupSize; nextRemainingGroupIndex ++) {
                            // check each remaining person from the previous group (looking at currentPersons)
                            //      to see which one is now the same user in the current group
                            if (newPersons.get(i).getStartLocationTimestamp().isSamePerson(currentPersons.get(nextRemainingGroupIndex).getEndLocationTimestamp())) {
                                currentPersons.set(i, new Person(currentPersons.get(nextRemainingGroupIndex)));
                                currentPersons.get(i).addLocationTimeStamp(currentGroup.getSingleUser(i));
                                usersThatExited.remove(nextRemainingGroupIndex);
                            }}}}
                // for the users that exited, reset currentPersons for those users to new empty persons
                for (Integer element : usersThatExited) {
                    if (element < previousGroup.getSize()) {
                        currentPersons.set(element, new Person());
                        System.out.println("\t Person " + element + " exited!");
                    }}
                previousGroup = currentGroup;
                
             // SOMEBODY JOINED
            } else if (currentGroupSize > previousGroupSize) {   
                // Create a list of possible new users that entered (represented by user ID's) that we will narrow down to the correct answer
                List<Integer> usersThatEntered = new ArrayList<>(); 
                for (int i = 0; i < currentGroup.getSize(); i++) {
                    usersThatEntered.add(i);
                    newPersons.set(i, new Person(assignedID, currentGroup.getSingleUser(i)));
                }
                System.out.println("Someone(s) Entered: (" + previousGroupSize + ") -> (" + currentGroupSize +") at " + currentGroup.getTimeOfGroup());
                // find out which of the previous group members corresponds to which of the newly observed group members
                for (int i = 0; i < previousGroup.getSize(); i++) {
                    // SAME PERSON
                    if (newPersons.get(i).getStartLocationTimestamp().isSamePerson(currentPersons.get(i).getEndLocationTimestamp())) {
                        currentPersons.get(i).addLocationTimeStamp(currentGroup.getSingleUser(i));
                        usersThatEntered.remove(i);
                    }
                    // NOT SAME PERSON
                    else {
                        for (int nextRemainingGroupIndex = 0; nextRemainingGroupIndex < currentGroup.getSize(); nextRemainingGroupIndex ++) {
                            if (newPersons.get(nextRemainingGroupIndex).getStartLocationTimestamp().isSamePerson(currentPersons.get(i).getEndLocationTimestamp())) {
                                currentPersons.set(nextRemainingGroupIndex, new Person(currentPersons.get(i)));
                                currentPersons.get(nextRemainingGroupIndex).addLocationTimeStamp(currentGroup.getSingleUser(i));
                                usersThatEntered.remove(nextRemainingGroupIndex);
                                System.out.println("The next person with id: " + nextRemainingGroupIndex + " is actually the old person with id: " + i);
                            }}}}
                for (Integer element : usersThatEntered) {
                    // For each of the new users that entered, update currentPersons with a new person with a unique assigned ID
                    currentPersons.set(element, new Person(assignedID, currentGroup.getSingleUser(element)));
                    // Updated assignedID
                    assignedID ++;
                    // Add the new users to listOfUsers
                    listOfUsers.add(currentPersons.get(element));
                    System.out.println("\t Person " + element + " entered!");
                }
                previousGroup = currentGroup;

              // either nobody exited or entered, or same number of people exited and entered
            } else if (currentGroupSize == previousGroupSize){
                //check that everyone is the same person
                // TODO there is the chance that a users in the previous group becomes a different in the next group
                //      for example, users 0 and 1 in the previous group can become users 1 and 0 in the next group, respectively
                for (int i = 0; i < currentGroupSize; i++) {
                    //                    if (!(previousGroup.getSingleUser(i).isSamePerson((currentGroup.getSingleUser(i))))) {
                    //                        System.out.println("not same person at: " + currentGroup.getSingleUser(i).getDateTime());
                    //                    } else {
                    currentPersons.get(i).addLocationTimeStamp(currentGroup.getSingleUser(i));
                    //                    }
                }
            }
            previousGroup = currentGroup;
        }
        return listOfUsers;
    }
}