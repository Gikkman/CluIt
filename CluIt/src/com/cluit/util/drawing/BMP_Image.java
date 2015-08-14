package com.cluit.util.drawing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.bmp.BMPEncoder;

public class BMP_Image {
	private BufferedImage img;
	private File file;
	private RGB_Pixel[][] pixels;
	
	private BMP_Image(String filepath){
		file = new File(filepath);
	}
	
	/**Reads an existing BMP-file from the file system.
	 * 
	 * @param filepath Path to the file that'll be loaded
	 * @return The loaded BMP_Image file
	 * @throws IOException Thrown if the file could not be loaded
	 */
	public static BMP_Image load(String filepath) throws IOException{
		BMP_Image i = new BMP_Image(filepath);
		i.readImage();
		return i;
	}
	/**Creates an empty BMP-file in the file system. This file should later be used to write to.
	 * 
	 * @param filepath Path to where the file should be created 
	 * @return A blank BMP_Image object
	 * @throws IOException Thrown if the file could not be created
	 */
	public static BMP_Image create(String filepath) throws IOException{
		BMP_Image i = new BMP_Image(filepath);
		i.createBlankImage();		
		return i;
	}	
	
	/**Retrieves the RGB_Pixel element at coordinate [x,y] counting from the top left corner
	 * 
	 * @param x The x coordinate. Range [0 - N-1], where N is the width of the image
	 * @param y The y coordinate. Range [0 - N-1], where N is the height of the image
	 * @return The RGB_Pixel element from that coordinate
	 */
	public RGB_Pixel getPixel(int x, int y) {
		if( x > getWidth() || y > getHeight() ){
			System.err.println( "Error! The requested pixel ["+x+","+y+"] is outside of image range - ("+getWidth()+","+getHeight()+")" + new Exception().getStackTrace()[0] );
			return null;
		}
		return pixels[x][y];
	}
	
	/**Fetches the entire array of RGB_Pixels from the image.
	 * To reach the pixel at coordinate [x, y], you have to access element ( x + y*width )
	 * 
	 * @return One array with all the RGB_Pixels. 
	 */
	public RGB_Pixel[] getPixels() {
		int w = getWidth(), h = getHeight();
		RGB_Pixel[] p = new RGB_Pixel[w*h];
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				p[x + y*w] = getPixel(x, y);
		return p;
	}
	
	/**Checks if there is any image data loaded in this instance (i.e., is the BufferedImage != null). 
	 * 
	 * @return True if the BufferedImage contains data, False if it is null
	 */
	public boolean isLoaded(){
		return img != null;
	}
	
	public final int getWidth(){
		return img.getWidth();
	}
	
	public final int getHeight(){
		return img.getHeight();
	}	
	
	/**Creates an image from the pixels stored in the image and writes that image to the BMP_Image object's file
	 * 
	 * @return True if write was successful, False if not
	 */
	public boolean write(){
		int w = getWidth(), h = getHeight(), colors = 3;
		int amountOfPixels = w*h*colors;	//Each color (RGB) needs its own index. So we need 3 int per pixel
		int[] imageArray = new int[amountOfPixels];
		
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				RGB_Pixel p = pixels[x][y];
				imageArray[(x*colors+0) + (y*w*colors)] = p.R;	
				imageArray[(x*colors+1) + (y*w*colors)] = p.G;	
				imageArray[(x*colors+2) + (y*w*colors)] = p.B;	
				
			}
		}
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = img.getRaster();
		raster.setPixels(0, 0, w, h, imageArray);
		try {
			BMPEncoder.write(img, file);
			System.out.println("Image write successful to file: " + file.getPath() );
			return true;
		} catch (IOException e) {
			System.out.println("Could not write the desired file: " + file.getPath());
			e.printStackTrace();
			return false;
		}		
	}

	/***********************************************************************************/
	
	private void readImage() throws IOException{
			img = BMPDecoder.read(file);
			pixels = readPixels();
	}
	private void createBlankImage() throws IOException {
		img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR);
		BMPEncoder.write(img, file);
		readImage();
	}
	private RGB_Pixel[][] readPixels() {			
		final int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	    final int width = img.getWidth();
	    final int height = img.getHeight();    
	    
	    RGB_Pixel[][] result = new RGB_Pixel[height][width];
	    int r = 0, g = 0, b = 0;
         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel++) {
            r = (( pixels[pixel] & 0xff0000) >> 16); // red
            g = (( pixels[pixel] & 0x00ff00) >> 8); // green
            b = (( pixels[pixel] & 0x0000ff) ); 	// blue
            result[col][row] = new RGB_Pixel(r, g, b);
            col++;
            if (col == width) {
               col = 0;
               row++;
            }
         }
		return result;
	}
}
