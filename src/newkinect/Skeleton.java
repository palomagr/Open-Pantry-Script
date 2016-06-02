package newkinect;

import javafx.geometry.Point3D;
/**
 * Represents a skeleton observed by the Microsoft Kinect in 3D space
 * 
 * It can be represented by either:
 *      1) All 25 joints recognized by the Kinect
 *      2) The 13 joints needed to compute the skeleton's approximate height, which are:
 *          spineBase, spineMid, neck, head, hipLeft, kneeLeft, ankleLeft, footLeft, hipRight, kneeRight, ankleRight, footRight, and spineShoulder
 * 
 */
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
    
    /**
     * Make a Skeleton from the 25 joints recognized by the Kinect
     * @param spineBase the bottom spine joint
     * @param spineMid the middle spine joint
     * @param neck the neck joint
     * @param head the head joint
     * @param shoulderLeft the left shoulder joint
     * @param elbowLeft the left elbow joint
     * @param wristLeft the left wrist joint
     * @param handLeft the left hand joint
     * @param shoulderRight the right shoulder joint
     * @param elbowRight the right elbow joint
     * @param wristRight the right wrist joint
     * @param handRight the right hand joint
     * @param hipLeft the left hip joint
     * @param kneeLeft the left knee joint
     * @param ankleLeft the left ankle joint
     * @param footLeft the left foot joint
     * @param hipRight the right hip joint
     * @param kneeRight the right knee joint
     * @param ankleRight the right ankle joint
     * @param footRight the right foot joint
     * @param spineShoulder the top spine joint
     * @param handTipLeft the tip of the left hand joint
     * @param thumbLeft the left thumb joint
     * @param handTipRight the tip of the right hand joint
     * @param thumbRight the right thumb joint
     */
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
    
    /**
    Make a Skeleton from the 13 joints recognized by the Kinect that are needed to calculate the approximate height of the skeleton
    * @param spineBase the bottom spine joint
    * @param spineMid the middle spine joint
    * @param neck the neck joint
    * @param head the head joint
    * @param hipLeft the left hip joint
    * @param kneeLeft the left knee joint
    * @param ankleLeft the left ankle joint
    * @param footLeft the left foot joint
    * @param hipRight the right hip joint
    * @param kneeRight the right knee joint
    * @param ankleRight the right ankle joint
    * @param footRight the right foot joint
    * @param spineShoulder the top spine joint
    */
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
    
    /**
     * Create a new Skeleton with the same 13 joint (the joints needed to calculate approximate height) locations as another Skeleton
     * @param original the Skeleton with the original joint locations that are to be used in constructing the new Skeleton
     */
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
    
    /**
     * Calculate the approximate height of the skeleton by summing distances between appropriate joints
     * @return the approximate height of the skeleton
     */
    public double getHeight() {
        // calculate approximate torso height by adding the distances between appropriate joints
        double torsoHeight = head.distance(neck) + neck.distance(spineShoulder) + spineShoulder.distance(spineMid) +
                spineMid.distance(spineBase) + spineBase.distance(avgPoint(hipRight, hipLeft));
        // calculate approximate right leg height by adding the distances between appropriate joints
        double rightLegHeight = hipRight.distance(kneeRight) + kneeRight.distance(ankleRight) +
                ankleRight.distance(footRight);
        // calculate approximate left leg height by adding the distances between appropriate joints
        double leftLegHeight = hipLeft.distance(kneeLeft) + kneeLeft.distance(ankleLeft) + 
                ankleLeft.distance(footLeft);
        // take average of leg heights and add to torso height to get total height
        double totalHeight = torsoHeight + ((rightLegHeight + leftLegHeight)/2);

        return totalHeight;
    }
    
    /**
     * Compute the location in 3D space of the midpoint between two points
     * @param first the first point in 3D space
     * @param second the second point in 3D space
     * @return the midpoint between the two points
     */
    public static Point3D avgPoint(Point3D first, Point3D second) {
        double avgX = (first.getX() + second.getX()) / 2;
        double avgY = (first.getY() + second.getY()) / 2;
        double avgZ = (first.getZ() + second.getZ()) / 2;

        return new Point3D(avgX, avgY, avgZ);
    }

    /**
     * Get the approximate general location of the skeleton, given by the coordinates of its spineMid joint
     * @return the approximate general location of the skeleton
     */
    public Point3D getLocation() {
        return spineMid;
    }
    
    /**
     * Get the location of the skeleton's spineBase joint
     * @return the 3D space location of the skeleton's spineBase joint
     */
    public Point3D getSpineBase() {
        return spineBase;
    }
    
    /**
     * Get the location of the skeleton's spineMid joint
     * @return the 3D space location of the skeleton's spineMid joint
     */
    public Point3D getSpineMid() {
        return spineMid;
    }

    /**
     * Get the location of the skeleton's neck joint
     * @return the 3D space location of the skeleton's neck joint
     */
    public Point3D getNeck() {
        return neck;
    }
    
    /**
     * Get the location of the skeleton's head joint
     * @return the 3D space location of the skeleton's head joint
     */
    public Point3D getHead() {
        return head;
    }
    
    /**
     * Get the location of the skeleton's shoulderLeft joint
     * @return the 3D space location of the skeleton's shoulderLeft joint
     */
    public Point3D getShoulderLeft() {
        return shoulderLeft;
    }
    
    /**
     * Get the location of the skeleton's elbowLeft joint
     * @return the 3D space location of the skeleton's elbowLeft joint
     */
    public Point3D getElbowLeft() {
        return elbowLeft;
    }
    
    /**
     * Get the location of the skeleton's wristLeft joint
     * @return the 3D space location of the skeleton's wristLeft joint
     */
    public Point3D getWristLeft() {
        return wristLeft;
    }
    
    /**
     * Get the location of the skeleton's handLeft joint
     * @return the 3D space location of the skeleton's handLeft joint
     */
    public Point3D getHandLeft() {
        return handLeft;
    }
    
    /**
     * Get the location of the skeleton's shoulderRight joint
     * @return the 3D space location of the skeleton's shoulderRight joint
     */
    public Point3D getShoulderRight() {
        return shoulderRight;
    }

    /**
     * Get the location of the skeleton's elbowRight joint
     * @return the 3D space location of the skeleton's elbowRight joint
     */
    public Point3D getElbowRight() {
        return elbowRight;
    }
    
    /**
     * Get the location of the skeleton's wristRight joint
     * @return the 3D space location of the skeleton's wristRight joint
     */
    public Point3D getWristRight() {
        return wristRight;
    }
    
    /**
     * Get the location of the skeleton's handRight joint
     * @return the 3D space location of the skeleton's handRight joint
     */
    public Point3D getHandRight() {
        return handRight;
    }
    
    /**
     * Get the location of the skeleton's hipLeft joint
     * @return the 3D space location of the skeleton's hipLeft joint
     */
    public Point3D getHipLeft() {
        return hipLeft;
    }
    
    /**
     * Get the location of the skeleton's kneeLeft joint
     * @return the 3D space location of the skeleton's kneeLeft joint
     */
    public Point3D getKneeLeft() {
        return kneeLeft;
    }
    
    /**
     * Get the location of the skeleton's ankleLeft joint
     * @return the 3D space location of the skeleton's ankleLeft joint
     */
    public Point3D getAnkleLeft() {
        return ankleLeft;
    }
    
    /**
     * Get the location of the skeleton's footLeft joint
     * @return the 3D space location of the skeleton's footLeft joint
     */
    public Point3D getFootLeft() {
        return footLeft;
    }
    
    /**
     * Get the location of the skeleton's hipRight joint
     * @return the 3D space location of the skeleton's hipRight joint
     */
    public Point3D getHipRight() {
        return hipRight;
    }
    
    /**
     * Get the location of the skeleton's kneeRight joint
     * @return the 3D space location of the skeleton's kneeRight joint
     */
    public Point3D getKneeRight() {
        return kneeRight;
    }
    
    /**
     * Get the location of the skeleton's ankleRight joint
     * @return the 3D space location of the skeleton's ankleRight joint
     */
    public Point3D getAnkleRight() {
        return ankleRight;
    }
    
    /**
     * Get the location of the skeleton's footRight joint
     * @return the 3D space location of the skeleton's footRight joint
     */
    public Point3D getFootRight() {
        return footRight;
    }
    
    /**
     * Get the location of the skeleton's spineShoulder joint
     * @return the 3D space location of the skeleton's spineShoulder joint
     */
    public Point3D getSpineShoulder() {
        return spineShoulder;
    }
    
    /**
     * Get the location of the skeleton's handTipLeft joint
     * @return the 3D space location of the skeleton's handTipLeft joint
     */
    public Point3D getHandTipLeft() {
        return handTipLeft;
    }
    
    /**
     * Get the location of the skeleton's thumbLeft joint
     * @return the 3D space location of the skeleton's thumbLeft joint
     */
    public Point3D getThumbLeft() {
        return thumbLeft;
    }
    
    /**
     * Get the location of the skeleton's handTipRight joint
     * @return the 3D space location of the skeleton's handTipRight joint
     */
    public Point3D getHandTipRight() {
        return handTipRight;
    }
    
    /**
     * Get the location of the skeleton's thumbRight joint
     * @return the 3D space location of the skeleton's thumbRight joint
     */
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