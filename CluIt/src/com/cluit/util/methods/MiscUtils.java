package com.cluit.util.methods;

import java.io.IOException;
import java.util.ArrayList;

import com.cluit.util.dataTypes.Entry;
import com.cluit.util.drawing.BMP_Image;
import com.cluit.util.drawing.RGB_Pixel;

/**Another hepler class wich contains various methods that doesn't belong somewhere specific or is general enough to 
 * be used thorughout the program.
 * 
 * @author Simon
 *
 */
public class MiscUtils {
	/**Returns a link to a code position
	 * Intended to be inserted in an error message. 
	 * 
	 * The "depth" decides how many stack-images up you want displayed.
	 * <br><b>Example:</b>
	 * <br>I call a method foo() and in foo() i call method bar()
	 * from bar(), I want a message with a link to foo().
	 * In that case, depth is 1 (since foo() is one image
	 * 	"up" from bar() ). If I wanted it to point to the
	 *  line which caused the error in bar(), depth is 0.
	 * @return 
	 */
	public static String getStackPos(){
		String out = "   ";
		StackTraceElement[] e = new Exception().getStackTrace();
		
		for( int i = 1; i < e.length && i < 5; i++){
			String s = e[i].toString();
			int f = s.indexOf("(");
			int l = s.lastIndexOf(")")+1;
			out += s.substring( f , l)+" ";
		}
		return out;
	}
	
	public static Entry[] entriesFromFeatureMatrix(double[][] data){
		ArrayList<Entry> points = new ArrayList<Entry>();
		for(int i = 0; i < data.length; i++)
			points.add( new Entry( i, data[i] ) );
		return points.toArray( new Entry[0] );
	}	

	private static String filename = "resources/test.bmp";
	private static int[] colors = {0xff0000, 0x00ff00, 0x0000ff, 0x00ffff, 0xffff00, 0xff00ff, 0xf0f0f}; 
	private static BMP_Image img;
	public static Entry[] pointsFromBmp(){
		img = null;		
		try {
			img = BMP_Image.load(filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Read each pixel of the image, and create a data point for each non-white pixel
		ArrayList<Entry> points = new ArrayList<Entry>();
		for( int y = 0; y < img.getHeight(); y++)
			for( int x = 0; x < img.getWidth(); x++)
				if( img.getPixel(x, y).getRGB() != 0xffffff )
					points.add( new Entry(x, y));
		
		return points.toArray( new Entry[0] );
	}
	
	/**Colors each non-white pixel in the image differently, depending on cluster membership
	 * 
	 * @param points
	 * @param clusterMembership
	 */
	public static void colorPixels(Entry[] points, int[] clusterMembership) throws Exception{
		int highestX = 1, highestY = 1;
		for(Entry e : points){
			if( e.getCoordinateAt(0) > highestX ) highestX = (int) e.getCoordinateAt(0);
			if( e.getCoordinateAt(1) > highestY ) highestY = (int) e.getCoordinateAt(1);
		}
		try {
			img = BMP_Image.create(filename, highestX+1, highestY+1 );
		} catch (IOException e) {
			throw e;
		}
		
		for(int i = 0; i < points.length; i++){
			Entry p = points[i];
			double[] dim = p.getCoordinates();
			
			RGB_Pixel px = img.getPixel((int) dim[0], (int) dim[1]);
			
			int color = clusterMembership[i] != -1 ? colors[ clusterMembership[i] ] : 0x000000;
			px.setRGB( color );
		}
		img.write();
	}
	
	public static String doubleArray(int ... in){
		String out = "[ ";
		for(int d : in){
			out+= (d+" , ");
		}
		//Magic number 2 stems from the wish to remove the ", " from the end of the string.
		//If the for-loop was never entered however, out.lenght == 2. In that case, we do nothing 
		out = out.substring(0, out.length()-2 > 0 ? out.length()-2 : 2); 
		return out + "]";
	}
	
	public static Double[] boxDoubleArray(double ... in){
		Double[] out = new Double[in.length];
		for(int i = 0; i < in.length; i++)
			out[i] = in[i];
		return out;
	}
}
