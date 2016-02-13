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
import java.util.Collections;

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
				thisUserObject.addPositions(new Point3d(thisX,thisY,thisZ), thisTime);

				//for (int i = 1; i <points.size();i++){
				//Point3d p1 = points.get(i);
				//Point3d p2 = points.get(i-1);

				//dist = p1.distanceXY(p2);
				//System.out.println(dist+"this is distance");
				//}


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
		try{ 	FileWriter fout = new FileWriter(fileOut);
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

		try{ 	FileWriter fout = new FileWriter(fileOut);
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




	public void UserToFile(String fileOut, String testID, String pattern) throws IOException {
		double secs=0;
		try{ 	FileWriter fout = new FileWriter(fileOut);
		BufferedWriter bout= new BufferedWriter(fout);	
		for (User thisUser : usersById){		

			if (thisUser.getID().equals(testID)){
				ArrayList<String> t= thisUser.getTime();
				ArrayList<Point3d> p= thisUser.getPoints();
				int id = Integer.parseInt(thisUser.getID());

				for (int i = 1;i<p.size();i++){
					Point3d p1= p.get(i-1);
					Point3d p2= p.get(i);
					
					
					System.out.println( dist + " this is distance ");
					String t1= t.get(i-1);					
					String t2= t.get(i);
					//String pattern = "yyyy_MM_dd_HH_mm_ss_SSS";
					DateTime start = DateTime.parse(t1, DateTimeFormat.forPattern(pattern));					
					DateTime end = DateTime.parse(t2, DateTimeFormat.forPattern(pattern));
					Interval interval = new Interval(start, end);						
					Duration duration = interval.toDuration();

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

		try{ 	FileWriter fout = new FileWriter(fileOut);
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

		try{ 	FileWriter fout = new FileWriter(fileOut);
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




