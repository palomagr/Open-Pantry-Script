package dataAnalysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

public class UsersTest {

    public static void main(String[] args) throws IOException {
        
        
        
        //AllUsers test1 = new AllUsers("com ml 03 13 without 0.txt");
        AllUsers test1 = new AllUsers("src/dataAnalysis/testData.txt");
        //test1.WriteNearToFile("textNearsCreatedNow.txt");


        //test1.SpeedToFile("mov 02 25 u1 won speed results.txt", 1.0, 2.5);
        //test1.SpeedToFile("all 02 25 u1 won speed results.txt", 0.0, 2.5);
        //test1.SpeedToFile("stand 02 25 u1 won speed results.txt", 0.0, 1.0);

        DateTime timeStart = new DateTime(2016, 02, 12, 10, 56, 20, 0);
        DateTime timeEnd = new DateTime(2016, 02, 12, 10, 56, 51, 0);
        
        DateTime timeStart2 = new DateTime(2016, 02, 17, 9, 40, 17, 532);
        DateTime timeEnd2 = new DateTime(2016, 02, 17, 16, 56, 51, 0);
        
        DateTime timeStart3 = new DateTime(2016, 02, 12, 0, 0, 0, 0);
        DateTime timeEnd3 = new DateTime(2016, 02, 13, 0, 0, 0, 0);
        
        DateTime user4Start = new DateTime(2016, 02, 17, 9, 37, 21, 502);
        
        

        
//        List<String> sampleTimes = new ArrayList<String>();
        
//        for (int i = 10; i < 30; i++) {
//            String time = "2016_02_12_10_56_" + i + "_000";
//            sampleTimes.add(time);
//        }
//
//        System.out.println(sampleTimes);
//        
//        List<Integer> firstLastIndices = test1.narrowTimes(sampleTimes, "yyyy_MM_dd_HH_mm_ss_SSS", timeStart, timeEnd); 
//        System.out.println(firstLastIndices);
//
          test1.frequencyDay("src/dataAnalysis/testFrequencyDayOutput.txt", "yyyy_MM_dd_HH_mm_ss_SSS", 2016, 2, 15);
//          test1.frequency("src/dataAnalysis/testFrequencyDayOutput.txt", "yyyy_MM_dd_HH_mm_ss_SSS", timeStart3, timeEnd2);
        
//        test1.UserToFile("src/dataAnalysis/TestOutput.txt", "1","yyyy_MM_dd_HH_mm_ss_SSS");
        
//        test1.UserToFile("src/dataAnalysis/UserTestOutput.txt", "2","yyyy_MM_dd_HH_mm_ss_SSS");
//        test1.UserToFile("src/dataAnalysis/UserTestOutput.txt", "3","yyyy_MM_dd_HH_mm_ss_SSS");
//        test1.UserToFile("src/dataAnalysis/UserTestOutput.txt", "4","yyyy_MM_dd_HH_mm_ss_SSS");
//        test1.UserToFile("src/dataAnalysis/UserTestOutput.txt", "5","yyyy_MM_dd_HH_mm_ss_SSS");


        //test1.UserToFile("ml bench 02 25 u1.txt", "1", "yyyy_MM_dd_HH_mm_ss_SSS");

        //test1.WriteSpeedToFile("test speed results.txt"); 
        //test1.serialize("objectFileCreatedNow.data");
    }



//        @Test
//        public void test() {
//            fail("Not yet implemented");
//        }

    }
