package dataAnalysis;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;





public class AllUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    ArrayList <Point3d> points= new ArrayList<Point3d>();
    ArrayList <Double> dis = new ArrayList<Double>();

    ArrayList <User> users=new ArrayList<User>();
    ArrayList <User> usersById=new ArrayList<User>();
    ArrayList <User> nearUsers=new ArrayList<User>();

    ArrayList <String> ID=new ArrayList<String>();
    ArrayList <String> x =new ArrayList<String>();
    ArrayList <String> y=new ArrayList<String>();
    ArrayList <String> z=new ArrayList<String>();
    ArrayList <String> time=new ArrayList<String>();

    int count = 0;
    User u0 = new User("0");
    User u1 = new User("1");
    User u2 = new User("2");
    User u3 = new User("3");
    User u4 = new User("4");
    User u5 = new User("5");
    User u6 = new User("6");
    User u7 = new User("7");
    User u8 = new User("8");
    User u9 = new User("9");
    User u10 = new User("10");

    double dist = 0;

    // constructor of all users

    public AllUsers(String filePath){
        try {

            FileReader fin = new FileReader(filePath);
            BufferedReader b = new BufferedReader(fin);

            String currentLine= b.readLine();
            User thisUserObject = new User("0");
            usersById.add(u0);
            usersById.add(u1);
            usersById.add(u2);
            usersById.add(u3);
            usersById.add(u4);
            usersById.add(u5);
            usersById.add(u6);
            usersById.add(u7);
            usersById.add(u8);
            usersById.add(u9);
            usersById.add(u10);


            while (currentLine != null) {
                String[] position= currentLine.split("\t");

                //Extract Data from one line

                String thisX = position[0];             
                String thisY = position[1];
                String thisZ = position[2];
                String thisID = position[3];
                //System.out.println(thisID + " users creation");

                String thisTime = position[4];


                //arraylist of points only
                points.add(new Point3d(thisX,thisY,thisZ)); 

                //Create new User Object if this line is not belonging to the previous user
                if (!thisID.equals(thisUserObject.getID())){
                    thisUserObject = new User(thisID);
                    users.add(thisUserObject);
                    count++;
                }

                //Add the positions to the User 
//                thisUserObject.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);

                //for (int i = 1; i <points.size();i++){
                //Point3d p1 = points.get(i);
                //Point3d p2 = points.get(i-1);

                //dist = p1.distanceXY(p2);
                //System.out.println(dist+"this is distance");
                //}
                
                if(thisID.equals("0")){                     
                    u0.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }

                if(thisID.equals("1")){                     
                    u1.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("2")){
                    u2.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("3")){
                    u3.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("4")){
                    u4.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }

                if(thisID.equals("5")){
                    u5.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("6")){
                    u6.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("7")){
                    u7.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("8")){
                    u8.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }

                if(thisID.equals("9")){
                    u9.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }
                if(thisID.equals("10")){
                    u10.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);
                }


                currentLine= b.readLine();
            }///////close the while

            b.close();

            System.out.println("Done with creating all user objects");
            System.out.println(count+ " this is the total of users");

            //Find out near users
            //for (User thisUser : users){              
            //Test all points if they are close enough
            //if (thisUser.UserPointCP(new Point3d(0,0,0)) < 200){
            //nearUsers.add(thisUser);
            //System.out.println(thisUser.getID() + "  this is near user");     
            //}
            //thisUser.getSpeed();
            //}

            //Do something with the near users...

        }
        catch (FileNotFoundException ef) {
            System.out.println("File not found");}
        catch (IOException ei) {
            System.out.println("IO Exception"); 
        }

        //System.out.println(count+ " this is the total of users");
    }




    public void writeob(){
        for (User s : nearUsers)
            System.out.println(s+ "this is the near user");
    }



    public void WriteNearToFile(String fileOut) throws IOException {
        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);
        for (User thisUser : nearUsers){
            bout.write(thisUser.ID);
        }
        bout.close();
        System.out.println("Done writing near users to file");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void serialize(String fileout)throws IOException{         

        try{FileOutputStream fout = new FileOutputStream(fileout);
        ObjectOutputStream oos = new ObjectOutputStream(fout);   
        oos.writeObject(users);
        oos.close();
        System.out.println("Done writing object");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void SpeedToFile(String fileOut, double minS, double maxS) throws IOException {
        ArrayList<Double> speeds= new ArrayList<Double>();
        DecimalFormat df = new DecimalFormat("#.####");
        double totalSpeed = 0 ;
        double totalTime = 0 ;
        int countS = 0;

        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);
        for (User thisUser : users){
            if (thisUser.pointCount()>5){
                double speed = thisUser.getSpeed();
                double duration = thisUser.getDuration();
                if ( !Double.isInfinite(speed) & speed>minS & speed < maxS){    
                    totalSpeed = totalSpeed + speed;
                    totalTime= totalTime + duration;
                    countS = countS + 1;
                    bout.write(df.format(speed) + "\t" + "speed m/s user ID" + "\t" + thisUser.getID() + "\n"); 
                    speeds.add(speed);
                }
            }
        }
        double a =totalSpeed/countS; 
        double b=totalTime/countS;
        bout.write(countS + "\t" + "total users"+"\n");     
        bout.write(df.format(a) + "\t" + "average speed in meter/secs"+"\n");
        bout.write(df.format(Collections.max(speeds))+ "\t" +"max speed"+"\n");
        bout.write(df.format(Collections.min(speeds))+ "\t" +"min speed"+"\n");
        bout.write(df.format(b) + "\t" + "average time");
        bout.close();

        System.out.println("Done writing speed results");
        System.out.println(count+ " this is the total of users");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }





    public double pDistance(){
        double dist = 0;

        for (int i = 1; i <points.size();i++){
            Point3d p1 = points.get(i);
            Point3d p2 = points.get(i-1);
            if(p1.distanceXY(p2)<100 & p1.distanceXY(p2)>0){
                System.out.println(p1.distanceXY(p2));
                dist = p1.distanceXY(p2);
            }
        }
        return dist;
    }

    /**
     * Takes all of the times attributed to a user and narrows it to only times between a given start and
     * end time
     * @param times all of the logged timestamps of the user
     * @param pattern the pattern for recognizing this timestamp
     * @param start the given start time to narrow the times down to timestamps after this time
     * @param end the given end time to narrow the times down to timestamps before this time
     * @return the starting and ending indices for the new start and end times out of the original times
     * if none of the times are between the given start and end times (inclusive), the indices are both -1
     */
    public List<Integer> narrowTimes(ArrayList<String> times, String pattern, DateTime start, DateTime end) {
        int first = -1;
        int last = -1;
        
        for (int i = 0; i < times.size(); i++) {
            DateTime t1 = DateTime.parse(times.get(i), DateTimeFormat.forPattern(pattern));  
            if ((t1.isEqual(start) || t1.isAfter(start)) && !(t1.isAfter(end))){
                first = i;
                break;
            }
        }
        // no valid time found equal to or after the given start time
        if (first == -1) {
            List<Integer> narrowedTimes = Arrays.asList(first, last);
            return narrowedTimes;
        }
        // starting from the found valid start time, find a valid end time before end, if any
        for (int i = first; i < times.size(); i++) {
            DateTime t2 = DateTime.parse(times.get(i), DateTimeFormat.forPattern(pattern));                    
            if (t2.isEqual(end)){
                last = i;
                break;
            }
            else if (t2.isAfter(end)){
                last = i-1;
                break;
            }
        }
        // valid start time between start and end found, but no end time found before or equal to end
        if ((first != -1) && (last == -1)) {
            last = times.size();
        }
        
        List<Integer> narrowedTimes = Arrays.asList(first, last);
        return narrowedTimes;
    }
    
    /**
     * 
     * @param fileOut
     * @param pattern
     * @param day
     * @param month
     * @param year
     * @throws IOException
     */
    public void frequency(String fileOut, String pattern, DateTime timeStart, DateTime timeEnd) throws IOException {
        int count = 0;
        // the speed given by a change in position that signifies a change in individual
        double newPersonSpeed = 1;

        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);  

        for (User thisUser : usersById){      
//            if (!(thisUser.equals(u0))) {
//                break;
//            }
            String currentUserID = thisUser.getID();
            System.out.println(currentUserID);
            ArrayList<String> t= thisUser.getTime();

            ArrayList<Point3d> p= thisUser.getPoints();
            // narrow the list of times to those on the given day
            List<Integer> firstLastIndices = narrowTimes(t, pattern, timeStart, timeEnd);  
            int firstIndex = firstLastIndices.get(0);
            int lastIndex = firstLastIndices.get(1);
            if (!(firstIndex == -1 && lastIndex == -1)) {
                System.out.println("first shows up at: " + DateTime.parse(t.get(0), DateTimeFormat.forPattern(pattern)));

                ArrayList<String> matchedTimes = new ArrayList<String>(t.subList(firstIndex, lastIndex));
                ArrayList<Point3d> matchedPos = new ArrayList<Point3d>(p.subList(firstIndex, lastIndex));
                count += 1;
                System.out.println("count updated to: " + count);
                for (int i = 1; i < matchedPos.size(); i++) {
                    Point3d p1= matchedPos.get(i-1);
                    Point3d p2= matchedPos.get(i);
                    String t1= matchedTimes.get(i-1);                  
                    String t2= matchedTimes.get(i);
                    DateTime currentStart = DateTime.parse(t1, DateTimeFormat.forPattern(pattern));                    
                    DateTime currentEnd = DateTime.parse(t2, DateTimeFormat.forPattern(pattern));
                    Interval currentInterval = new Interval(currentStart, currentEnd);                       
                    Duration duration = currentInterval.toDuration();
                    long secondsDuration = duration.getMillis();
                    
                    double speed = p1.distanceXY(p2) / secondsDuration;
                    if (speed > newPersonSpeed) {
                        System.out.println("WOAH! NEW PERSON!");
                        count += 1;
                    }                    
                }           
            } 
        }
        bout.close();
        System.out.println("Frequency found! It's: " + count);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @param fileOut
     * @param pattern
     * @param day
     * @param month
     * @param year
     * @throws IOException
     */
    public void frequencyDay(String fileOut, String pattern, int year, int month, int day) throws IOException {
        DateTime timeStart = new DateTime(year, month, day, 0, 0, 0, 0);
        DateTime timeEnd = new DateTime(year, month, day + 1, 0, 0, 0, 0);
        
        frequency(fileOut, pattern, timeStart, timeEnd);
    }
    
    public void UserToFile(String fileOut, String testID, String pattern) throws IOException {
        double secs=0;
        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);  
        for (User thisUser : usersById){        
            String currentUserID = thisUser.getID();

            if (currentUserID.equals(testID)){
                ArrayList<String> t= thisUser.getTime();
                ArrayList<Point3d> p= thisUser.getPoints();
                int id = Integer.parseInt(thisUser.getID());

                for (int i = 1;i<p.size();i++){
                    Point3d p1= p.get(i-1);
                    Point3d p2= p.get(i);
                    String t1= t.get(i-1);                  
                    String t2= t.get(i);
                    DateTime start = DateTime.parse(t1, DateTimeFormat.forPattern(pattern));                    
                    DateTime end = DateTime.parse(t2, DateTimeFormat.forPattern(pattern));
                    Interval interval = new Interval(start, end);                       
                    Duration duration = interval.toDuration();
                    long secondsDuration = duration.getMillis();

                    //                    System.out.println("User: " + currentUserID + "; Distance between times: " + t1 + ", " + t2 + ": " + p1.distanceXY(p2));

                    //                    System.out.println(dist + " this is distance");
                    //String pattern = "yyyy_MM_dd_HH_mm_ss_SSS";

                    double speed = p1.distanceXY(p2) / secondsDuration;
                    if (speed > 0.1) {
                        System.out.println("WOAH SO FAST: " + speed + ". at time " + start);
                    }


                    secs= duration.getStandardSeconds();

                    //System.out.println(secs +"this are the secs");
                    if (secs >1 | p1.distanceXY(p2) >0.5){
                        id=id+1;
                        System.out.println(secs +" secs at this index  " + id);

                    }
                    bout.write(p.get(i).getX() + "\t"+ p.get(i).getY() + "\t"+p.get(i).getZ() + "\t"+ id+"\t" + t.get(i)+"\n");
                }
            }
        }
        bout.close();
        System.out.println("Done writing one user");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void SpeedToFileV1(String fileOut) throws IOException {
        DecimalFormat df = new DecimalFormat("#.####");
        double totalSpeed = 0 ;
        int countS = 0;

        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);
        for (User thisUser : users){
            if (thisUser.pointCount()>50){
                double speed = thisUser.getSpeedV1();
                if ( !Double.isInfinite(speed) & speed>0 & speed < 3.0){    
                    totalSpeed = totalSpeed + speed;
                    countS = countS + 1;
                    bout.write(df.format(speed) + "\t" + "speed m/s user ID" + "\t" + thisUser.getID() + "\n");         
                }
            }
        }
        double a =totalSpeed/countS; 

        bout.write(countS + " this is total users"+ "\n");
        bout.write(df.format(a) + " average speed in meter/secs");

        bout.close();

        System.out.println("Done writing speed results");
        System.out.println(count+ " this is the total of users");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void SpeedbyIdToFile(String fileOut) throws IOException {
        DecimalFormat df = new DecimalFormat("#.###");
        double totalSpeed = 0 ;
        int countS = 0;

        try{    FileWriter fout = new FileWriter(fileOut);
        BufferedWriter bout= new BufferedWriter(fout);
        for (User thisUser : users){
            if (thisUser.pointCount()>0){
                double speed = thisUser.getSpeed();
                if ( !Double.isInfinite(speed)& speed>0){   
                    totalSpeed = totalSpeed + speed;
                    countS = countS + 1;
                    bout.write(df.format(speed) + "\t" + "speed m/s user ID" + "\t" + thisUser.getID() + "\n" );
                }           
            }
        }
        double a =totalSpeed/countS; 

        bout.write(countS + " this is total users"+ "\n");
        bout.write(a + " average speed in meter/secs");

        bout.close();

        System.out.println("Done writing speed results by id");
        System.out.println(count+ " this is the total of users by id");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }





}




