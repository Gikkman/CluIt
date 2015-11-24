package testing.io;

import java.io.IOException;

import com.cluit.util.drawing.BMP_Image;
import com.cluit.util.drawing.RGB_Pixel;



public class Test_Image {
	
	private static int[] solX = new int[] {0, 2, 4, 6, 8, 1, 0, 2, 3, 0, 4, 5, 0, 6, 7, 0, 8, 9, 10 };
	private static int[] solY = new int[] {0, 0, 0, 0, 0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7, 8, 8, 9, 9 };

	public static void run(String[] args) throws IOException {
		System.out.println("Test - Image initialized");
		
		BMP_Image img = BMP_Image.load("resources/testing//ClusterMap.bmp");
		int idx = 0;
		
		if( img.isLoaded() ){
			for( int y = 0; y < img.getHeight(); y++){
				for( int x = 0; x < img.getWidth(); x++){
					RGB_Pixel p = img.getPixel(x, y);
					if( p.getRGB() != 0xffffff ){
						if( x != solX[idx] || y != solY[idx] ){
							System.out.println("Error! No data found at ["+x+","+y+"]. RGB data expected");
						}
						if( x == 0 )
							if( y == 2)
								if( p.R != 255 && p.G != 0   && p.B != 0)
									System.out.println("Error! Incorrect RGB value read at ["+x+","+y+"]. Expected [255,0,0] found ["+p.R+","+p.G+","+p.B+"]");
							if( y == 4)	
								if( p.R != 0   && p.G != 255 && p.B != 0)
									System.out.println("Error! Incorrect RGB value read at ["+x+","+y+"]. Expected [0,255,0] found ["+p.R+","+p.G+","+p.B+"]");
							if( y == 6)	
								if( p.R != 0   && p.G != 0   && p.B != 255)
									System.out.println("Error! Incorrect RGB value read at ["+x+","+y+"]. Expected [0,0,255] found ["+p.R+","+p.G+","+p.B+"]");
							if( y == 8)	
								if( p.R != 0   && p.G != 0   && p.B != 0)
									System.out.println("Error! Incorrect RGB value read at ["+x+","+y+"]. Expected [0,0,0] found ["+p.R+","+p.G+","+p.B+"]");
							
							if( x == 10 && y == 9)
								if( p.R != 255 && p.G != 0   && p.B != 0)
									System.out.println("Error! Incorrect RGB value read at ["+x+","+y+"]. Expected [255,0,0] found ["+p.R+","+p.G+","+p.B+"]");
							
							idx++;
					}
				}
			}
		}	
		
		System.out.println("Test - Image finalized");
	}
}
