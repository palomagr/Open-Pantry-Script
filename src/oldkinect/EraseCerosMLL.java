package oldkinect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EraseCerosMLL {
	public static void main(String[] args) {

		try {
			FileReader fin = new FileReader("11 22 com.txt");


			BufferedReader b = new BufferedReader(fin);
			FileWriter fout = new FileWriter("11 22 com wo 0.txt");
			BufferedWriter bout= new BufferedWriter(fout);
			//ArrayList <Point3d> points = new ArrayList<Point3d>();
			String currentLine= b.readLine();



			while (currentLine != null ) {	
				String[] words=currentLine.split("\t");

				for(int i=0;i<3;i++){
					double c1= Double.parseDouble(words[0]);
					double c2= Double.parseDouble(words[1]);
					double c3= Double.parseDouble(words[2]);

					Point3d p1= new Point3d(c1,c2,c3);
					Point3d p0=new Point3d(0.0,0.0,0.0);


					if(p1.distance(p0) > 40 && Math.abs(c1) > 0.0 || Math.abs(c2) > 0.0  || Math.abs(c3) > 0.0 ){
						bout.write(words[i]+"\t");
						System.out.println("the distance is more than 40");	
						
						if (i == 2){
							bout.write(words[3] +"\t" + words[9] + "\n");
						}
					}

					

				}




				currentLine= b.readLine();


			}


			b.close();
			bout.close();
			System.out.println("Done I am so happy");

		}


		catch (FileNotFoundException ef) {
			System.out.println("File not found");}
		catch (IOException ei) {
			System.out.println("IO Exception"); }
	}
}

