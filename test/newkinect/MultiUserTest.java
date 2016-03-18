package newkinect;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class MultiUserTest {
    
    private final static String[] ID_DELIMS = {"!", "@", "#", "$", "%", "^"};
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testCombineUsers() {
        List<PointInTime>[] processedData = new ArrayList[ID_DELIMS.length];
        assertEquals(ID_DELIMS.length, processedData.length);
    }
}
