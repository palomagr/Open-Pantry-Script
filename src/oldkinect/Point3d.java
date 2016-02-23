package oldkinect;

import java.io.Serializable;

public class Point3d implements Serializable {

	private static final long serialVersionUID = 1L;
	double X;
	double Y;
	double Z;

	public Point3d(double x, double y, double z) {
		super();
		X = x;
		Y = y;
		Z = z;
	}

	public Point3d(String x, String y, String z) {
		super();
		X = Double.parseDouble(x);
		Y = Double.parseDouble(y);
		Z = Double.parseDouble(z);
	}

	public double distanceXY (Point3d anotherPoint){
		return Math.sqrt(Math.pow((this.X-anotherPoint.X),2) + Math.pow((this.Z-anotherPoint.Z),2));
	}

	public double distance (Point3d anotherPoint){
		return Math.sqrt(Math.pow((this.X-anotherPoint.X),2) + Math.pow((this.Y-anotherPoint.Y),2)+ Math.pow((this.Z-anotherPoint.Z),2));
	}
	
	public double getX() {
		return X;
	}
	
	public double getY() {
		return Y;
	}
	
	public double getZ() {
		return Z;
	}
}