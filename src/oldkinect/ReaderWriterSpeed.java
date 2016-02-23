package oldkinect;


import java.io.*;

public class ReaderWriterSpeed {
	public static void main(String[] args) {

		try {
			FileReader fin = new FileReader("com ml 02 25.txt");
			BufferedReader b = new BufferedReader(fin); 
			FileWriter fout = new FileWriter("ml 02 25.txt"); //"k140 day_5_10.txt");
			BufferedWriter bout= new BufferedWriter(fout);
			//String preSec= "00";
			String currentLine= b.readLine();

			while (currentLine != null ) {	
				String[] words=currentLine.split("\t");
				String  Date=words[9];
				String[] DateParts=Date.split("_");
				String y=DateParts[0];

				String m=DateParts[1];
				String d=DateParts[2];
				String h=DateParts[3];
				String min=DateParts[4];
				String sec=DateParts[5];
				String mill=DateParts[6];				

				System.out.println(Date);
				if (m.length() ==1) {
					m = "0"+m;	
				}
				if (d.length() == 1) {
					d = "0"+d;					
				}
				if (h.length() == 1) {
					h = "0"+h;					
				}
				if (min.length() == 1) {
					min = "0"+min;					
				}
				if (sec.length() == 1) {
					sec = "0"+sec;					
				}



				if (mill.length() ==1) {
					mill = "00"+mill;					
				}
				if (mill.length() ==2) {
					mill = "0"+mill;					
				}

/*
				if (mill.length() ==3){
					mill = "000000"+mill;
				}	

				if (mill.length() ==4){
					mill = "00000"+mill;	
				}



				System.out.println(y);


				if (mill.length() ==5){
					mill = "0000"+mill;					
				}
				if (mill.length() ==6){
					mill = "000"+mill;					
				}
				if (mill.length() ==7){
					mill = "000"+mill;					
				}
				if (mill.length() ==8){
					mill = "00"+mill;					
				}
				if (mill.length() ==9){
					mill = "0"+mill;					
				}
*/
				String Date2= y+"_"+m+"_"+d+"_"+h+"_"+min+"_"+sec+"_"+ mill;
				bout.write(words[0]+ "\t" + words[1]+ "\t" +words[2]+ "\t" + 
							words[8]+ "\t" + Date2 +"\n");
				currentLine= b.readLine();
			}

			b.close();
			bout.close();
			System.out.println("Done");
			
		} catch (FileNotFoundException ef) {
			System.out.println("File not found");
		} catch (IOException ei) {
			System.out.println("IO Exception"); 
		}
	}
}

