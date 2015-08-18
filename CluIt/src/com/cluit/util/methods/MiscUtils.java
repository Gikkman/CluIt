package com.cluit.util.methods;

import java.io.IOException;
import java.util.ArrayList;

import com.cluit.util.dataTypes.Entry;
import com.cluit.util.drawing.BMP_Image;
import com.cluit.util.drawing.RGB_Pixel;

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
	public static void colorPixels(Entry[] points, int[] clusterMembership){
		for(int i = 0; i < points.length; i++){
			Entry p = points[i];
			double[] dim = p.getAllEntries();
			
			RGB_Pixel px = img.getPixel((int) dim[0], (int) dim[1]);
			
			int color = clusterMembership[i] != -1 ? colors[ clusterMembership[i] ] : 0x000000;
			px.setRGB( color );
		}
		img.write();
	}
}
