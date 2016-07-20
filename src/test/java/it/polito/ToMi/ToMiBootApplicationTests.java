package it.polito.ToMi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.polito.ToMi.clustering.SimplexNoise;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToMiBootApplication.class)
@WebAppConfiguration
public class ToMiBootApplicationTests {

	//	@Test
	//	public void contextLoads() {
	//	}

	//	@Test
	//	public void coordiantes(){
	//		double lat=45.0648903;
	//		double lng=7.6615567;
	//		
	//		//radius in meter
	//		int radius = 100;
	//		
	//		//radius approximation in degree
	//		double radiusDegree=radius/111300d;
	//		
	//		Random random = new Random();
	//		double u = random.nextDouble();
	//		double v = random.nextDouble();
	//		
	//		double w = radiusDegree * Math.sqrt(u);
	//		double t = 2*Math.PI*v;
	//		
	//		double xP = w * Math.cos(t);
	////		double x = xP/Math.cos(lng);
	//		double y = w * Math.sin(t);
	//		
	//		System.out.println((xP+lat)+", "+(y+lng));
	//	}

	@Test
	public void noise(){
		final int REPETITION = 3;
		final double DEGREE_DISPLACEMENT=0.0002;
		double origLat=45.064537;
		double minOrigLng=7.660935;
		double maxOrigLng=minOrigLng+REPETITION*DEGREE_DISPLACEMENT;

		double latScale = 110574d;
		double lngScale = 111320d;
		BufferedWriter bw=null, bwN=null;
		try{
			
			File fileOriginal = new File("/home/m04ph3u5/points.txt");
			File fileWithNoise = new File("/home/m04ph3u5/pointsWithNoise.txt");
	
			// if file doesnt exists, then create it
			if (!fileOriginal.exists()) {
				fileOriginal.createNewFile();
			}
	
			// if file doesnt exists, then create it
			if (!fileWithNoise.exists()) {
				fileWithNoise.createNewFile();
			}
			FileWriter fw = new FileWriter(fileOriginal.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			FileWriter fwN = new FileWriter(fileWithNoise.getAbsoluteFile());
			bwN = new BufferedWriter(fwN);
			
			JSONArray array = new JSONArray();
			JSONArray arrayWithNoise = new JSONArray();
			
			for(int i=0; i<REPETITION; i++){
				for(int j=0; j<REPETITION; j++){
					double lng;
					double lat = origLat + i*DEGREE_DISPLACEMENT;
					if(i%2==0){
						lng = minOrigLng + j*DEGREE_DISPLACEMENT;
					}
					else
						lng = maxOrigLng - j*DEGREE_DISPLACEMENT;
					
					
	
//					JSONObject obj = new JSONObject();
//					obj.put("lat", lat);
//					obj.put("lng", lng);
//					array.put(obj);
					bw.write("["+lat+","+lng+"],");
					bw.newLine();
					
					double latMeter = lat*latScale;
					double lngMeter = lng*lngScale * Math.cos((lat*Math.PI)/180);
	
					double noise = SimplexNoise.noise(latMeter, lngMeter);
//					double noise = SimplexNoise.noise(lat, lng);

					System.out.println(noise);
					int radius = 100;
	
					//radius approximation in degree
					double radiusDegree=radius/111320d;
					double w = radiusDegree * Math.sqrt(Math.abs(noise));
					double t = 2*Math.PI*noise;
	
					double xP = w * Math.cos(t);
					//		double x = xP/Math.cos(lng);
					double y = w * Math.sin(t);
//					System.out.println((xP+lat)+", "+(y+lng));
//					JSONObject objWithNoise = new JSONObject();
//					objWithNoise.put("lat", lat+xP);
//					objWithNoise.put("lng", lng+y);
//					arrayWithNoise.put(objWithNoise);
					bwN.write("["+(lat+xP)+","+(lng+y)+"],");
					bwN.newLine();
				}
			}	
			
			
//			bw.write(array.toString());
//			bwN.write(arrayWithNoise.toString());
		}catch(IOException e){
			System.out.println("EXCEPTION: "+e.getMessage());
		} finally{
			try {
				bw.close();
				bwN.close();
			} catch (IOException e) {
				System.out.println("EXCEPTION CLOSING: "+e.getMessage());
			}
		}
	}
}
