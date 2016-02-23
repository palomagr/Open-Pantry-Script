package oldkinect;

import java.io.Serializable;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	String ID;
	ArrayList <Point3d> p;
	ArrayList <String> time;

	public User(String ID) {
		super();
		this.ID = ID;
		this.p = new ArrayList <Point3d>();
		this.time = new ArrayList <String>();
	}

	public void addPositions(Point3d thisPoint, String thisTime) {
		this.p.add(thisPoint);
		this.time.add(thisTime);
	}

	public double UserPointCP(Point3d targetPoint) {
		double minDistance = p.get(0).distanceXY(targetPoint);
		for (Point3d eachPoint : p){
			double thisDistance = eachPoint.distanceXY(targetPoint);
			if (thisDistance<minDistance) minDistance = thisDistance;
		}
		return minDistance;
	}

	public int pointCount() {
		return p.size();
	}

	public String getID() {
		return ID;
	}

	public ArrayList<String> getTime() {
		return time;
	}

	public ArrayList<Point3d> getPoints() {
		return p;
	}

	public void setTime(ArrayList<String> time) {
		this.time = time;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////test functions

	public double getSpeed() {			
		double dist= this.cumDist();	
		double dur = this.getDuration(); 	
		double speed= dist/dur;
		System.out.println(speed + " this is the speed in m/s, this is the ID " + ID + " \n");
		return speed;
	}

	public double getSpeedV1() {			
		double dist= this.cumDist();	
		double dur = this.getPeriod(); 	
		double speed= dist/dur;
		System.out.println(speed + " this is the speed in m/s, this is the ID " + ID + " \n");
		return speed;
	}


	public double cumDist() {		
		double cumD =0;
		for (int i = 1; i < p.size(); i++){
			double tempD = p.get(i-1).distanceXY(p.get(i));
			cumD = cumD + tempD;			
		}		
		System.out.println("cumulative sum distance from user " + cumD + " user ID  " + ID);		
		return cumD;
	}

	public double cumDist100(ArrayList<Point3d> p2 ) {	
		double cumD =0;
		for (int i = 1; i < p2.size(); i++){
			double tempD = p2.get(i-1).distanceXY(p2.get(i));
			cumD = cumD + tempD;			
		}		
		//System.out.println("cumulative sum distance from user " + cumD + " user ID  " + ID);		
		return cumD;
	}


	public double getDuration100(DateTime start, DateTime end) {
    	Interval interval = new Interval(start, end);
		Duration duration = interval.toDuration();		
		//System.out.print(duration.getMillis()/1000.0 +"  this is duration in milliseconds from user" + "\n");
		return duration.getMillis()/1000.0;	
	}


	public double getDuration() {

		//ArrayList<String> userT = time;
		ArrayList<DateTime> jodat = new ArrayList<DateTime>();

		for (int i = 0; i< time.size(); i++){
			String input = time.get(i);           
			String pattern = "yyyy_MM_dd_HH_mm_ss_SSS";
			DateTime d = DateTime.parse(input, DateTimeFormat.forPattern(pattern));
			jodat.add(d);
		}
		DateTime start = new DateTime(jodat.get(0));
		DateTime end = new DateTime(jodat.get(jodat.size()-1));

		Interval interval = new Interval(start, end);
		Duration duration = interval.toDuration();		
		System.out.print(duration.getMillis() +"  this is duration in milliseconds from user" + "\n"); 

		return duration.getMillis()/1000.0;	
	}

	public double getPeriod() {
		//ArrayList<String> userT = time;

		ArrayList<String> millis= new ArrayList<String>();
		double secs=0;

		for (int i = 0; i< time.size(); i++){
			String input = time.get(i);			
			String[] parts = input.split("_");
			String m = parts[6];			
			millis.add(m);
		}

		for(int i =1;i< millis.size();i++) {
			double m1 = Double.parseDouble(millis.get(i-1));
			double m2 = Double.parseDouble(millis.get(i));

			double m3 = m2-m1;
			secs = secs + m3;
		}

		System.out.print( secs + "  this is period in seconds from user" + "\n"); 
		return secs/1000;		
	}
}

