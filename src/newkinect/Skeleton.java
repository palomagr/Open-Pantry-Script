package newkinect;

import javafx.geometry.Point3D;

public class Skeleton {
    private Point3D spineBase;
    private Point3D spineMid;
    private Point3D neck;
    private Point3D head;
    private Point3D shoulderLeft;
    private Point3D elbowLeft;
    private Point3D wristLeft;
    private Point3D handLeft;
    private Point3D shoulderRight;
    private Point3D elbowRight;
    private Point3D wristRight;
    private Point3D handRight;
    private Point3D hipLeft;
    private Point3D kneeLeft;
    private Point3D ankleLeft;
    private Point3D footLeft;
    private Point3D hipRight;
    private Point3D kneeRight;
    private Point3D ankleRight;
    private Point3D footRight;
    private Point3D spineShoulder;
    private Point3D handTipLeft;
    private Point3D thumbLeft;
    private Point3D handTipRight;
    private Point3D thumbRight;
    
    public Skeleton(Point3D spineBase, Point3D spineMid, Point3D neck, Point3D head, Point3D shoulderLeft,
            Point3D elbowLeft, Point3D wristLeft, Point3D handLeft, Point3D shoulderRight, 
            Point3D elbowRight, Point3D wristRight, Point3D handRight, Point3D hipLeft, Point3D kneeLeft,
            Point3D ankleLeft, Point3D footLeft, Point3D hipRight, Point3D kneeRight, Point3D ankleRight,
            Point3D footRight, Point3D spineShoulder, Point3D handTipLeft, Point3D thumbLeft, 
            Point3D handTipRight, Point3D thumbRight) {
        
        this.spineBase = spineBase;
        this.spineMid = spineMid;
        this.neck = neck;
        this.head = head;
        this.shoulderLeft = shoulderLeft;
        this.elbowLeft = elbowLeft;
        this.wristLeft = wristLeft;
        this.handLeft = handLeft;
        this.shoulderRight = shoulderRight;
        this.elbowRight = elbowRight;
        this.wristRight = wristRight;
        this.handRight = handRight;
        this.hipLeft = hipLeft;
        this.kneeLeft = kneeLeft;
        this.ankleLeft = ankleLeft;
        this.footLeft = footLeft;
        this.hipRight = hipRight;
        this.kneeRight = kneeRight;
        this.ankleRight = ankleRight;
        this.footRight = footRight;
        this.spineShoulder = spineShoulder;
        this.handTipLeft = handTipLeft;
        this.thumbLeft = thumbLeft;
        this.handTipRight = handTipRight;
        this.thumbRight = thumbRight;
    }
    
    public Skeleton(Point3D spineBase, Point3D spineMid, Point3D neck, Point3D head, Point3D hipLeft, Point3D kneeLeft,
            Point3D ankleLeft, Point3D footLeft, Point3D hipRight, Point3D kneeRight, Point3D ankleRight,
            Point3D footRight, Point3D spineShoulder) {
        
        this.spineBase = spineBase;
        this.spineMid = spineMid;
        this.neck = neck;
        this.head = head;
        this.hipLeft = hipLeft;
        this.kneeLeft = kneeLeft;
        this.ankleLeft = ankleLeft;
        this.footLeft = footLeft;
        this.hipRight = hipRight;
        this.kneeRight = kneeRight;
        this.ankleRight = ankleRight;
        this.footRight = footRight;
        this.spineShoulder = spineShoulder;
    }
    
    public Skeleton(Skeleton original) {
        this.spineBase = original.spineBase;
        this.spineMid = original.spineMid;
        this.neck = original.neck;
        this.head = original.head;
        this.hipLeft = original.hipLeft;
        this.kneeLeft = original.kneeLeft;
        this.ankleLeft = original.ankleLeft;
        this.footLeft = original.footLeft;
        this.hipRight = original.hipRight;
        this.kneeRight = original.kneeRight;
        this.ankleRight = original.ankleRight;
        this.footRight = original.footRight;
        this.spineShoulder = original.spineShoulder;
    }
    
    public double getHeight() {

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
    
    public static Point3D avgPoint(Point3D first, Point3D second) {
        double avgX = (first.getX() + second.getX()) / 2;
        double avgY = (first.getY() + second.getY()) / 2;
        double avgZ = (first.getZ() + second.getZ()) / 2;

        return new Point3D(avgX, avgY, avgZ);
    }

    public Point3D getLocation() {
        return spineMid;
    }
    
    public Point3D getSpineBase() {
        return spineBase;
    }
    
    public Point3D getSpineMid() {
        return spineMid;
    }

    public Point3D getNeck() {
        return neck;
    }
    
    public Point3D getHead() {
        return head;
    }
    
    public Point3D getShoulderLeft() {
        return shoulderLeft;
    }
    
    public Point3D getElbowLeft() {
        return elbowLeft;
    }
    
    public Point3D getWristLeft() {
        return wristLeft;
    }
    
    public Point3D getHandLeft() {
        return handLeft;
    }
    
    public Point3D getShoulderRight() {
        return shoulderRight;
    }

    public Point3D getElbowRight() {
        return elbowRight;
    }
    
    public Point3D getWristRight() {
        return wristRight;
    }
    
    public Point3D getHandRight() {
        return handRight;
    }
    
    public Point3D getHipLeft() {
        return hipLeft;
    }
    
    public Point3D getKneeLeft() {
        return kneeLeft;
    }
    
    public Point3D getAnkleLeft() {
        return ankleLeft;
    }
    
    public Point3D getFootLeft() {
        return footLeft;
    }
    
    public Point3D getHipRight() {
        return hipRight;
    }
    
    public Point3D getKneeRight() {
        return kneeRight;
    }
    
    public Point3D getAnkleRight() {
        return ankleRight;
    }
    
    public Point3D getFootRight() {
        return footRight;
    }
    
    public Point3D getSpineShoulder() {
        return spineShoulder;
    }
    
    public Point3D getHandTipLeft() {
        return handTipLeft;
    }
    
    public Point3D getThumbLeft() {
        return thumbLeft;
    }
    
    public Point3D getHandTipRight() {
        return handTipRight;
    }
    
    public Point3D getThumbRight() {
        return thumbRight;
    }

    @Override
    public boolean equals(Object thatObject) {
        if ( ! (thatObject instanceof Skeleton)) { return false; }
        
        Skeleton that = (Skeleton)thatObject;
        return spineMid.equals(that.spineMid);
        
    }
    @Override
    public int hashCode() {
        return spineMid.hashCode();
    }
}