package newkinect;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

/**
 * 
 * A couple of tests.
 * 
 * These are the only data sets that we can run the code on so far because the code requires that the data for Skeleton reads by the Kinect is
 * ordered in the specific sequence of Skeleton joints that the code reads to create Skeleton instances and height, which is also the specific
 * sequence defined by Microsoft on their website.
 *
 */

public class NewHeightTest {
    // Test it on sample data
    @Test
    public void testSampleData() {
        try {
            String rawSkelDataPath = "src/PalomaLeoTest/skeleton exampleV2.txt";
            List<SkeletonGroup> groups = NewHeight.organizeRawSkelData(rawSkelDataPath);
            List<Person> persons = NewHeight.processSkeletonData(groups);

            System.out.println(persons);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Test it on the data from when Leo and Paloma walked around the Graduate Student office
    @Test
    public void testLeoAndPalomaData() {
        try {
            String rawSkelDataPath2 = "src/PalomaLeoTest/skeleton OpenPantry.txt";
            List<SkeletonGroup> groups2 = NewHeight.organizeRawSkelData(rawSkelDataPath2);
            List<Person> persons2 = NewHeight.processSkeletonData(groups2);

            System.out.println("HI \n" + persons2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
