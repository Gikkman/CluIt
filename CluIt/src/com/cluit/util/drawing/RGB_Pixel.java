package com.cluit.util.drawing;

public class RGB_Pixel {
	public int R = 0;
	public int G = 0;
	public int B = 0;
	
	public RGB_Pixel(int r, int g, int b){
		this.R = r;
		this.G = g;
		this.B = b;
	}
	
	public int getRGB(){
		int out = 0;
		out += ( R << 16);
		out += ( G << 8);
		out += ( B );
		return out;
	}
	
	public void setRGB(int RGB){
		R = ((RGB & 0xff0000) >> 16);
		G = ((RGB & 0x00ff00) >> 8);
		B = ((RGB & 0x0000ff));
	}
	
	public void setRGB(int R, int G, int B){
		this.R = R;
		this.G = G;
		this.B = B;
	}
	
	public String toString(){
		return "("+R+","+G+","+B+")";
	}
}
