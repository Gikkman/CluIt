package com.cluit.util.preferences;

import javafx.scene.paint.Color;

import java.util.prefs.Preferences;

public class PreferenceManager {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************

	private static PreferenceManager INSTANCE = new PreferenceManager();
	
	private static String CLUSTER_KEY = "UserPrefsClusterName_";
	private static String COLOR_KEY   = "UserPrefsColorIndex_";
	
	private final Preferences prefs;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	private PreferenceManager(){
		prefs = Preferences.userRoot().node( this.getClass().getName() );
	}
	
	//*******************************************************************************************************
	//region							STATIC 			
	//*******************************************************************************************************
	
	public static String getClusterName(int index){
		return INSTANCE.prefs.get(CLUSTER_KEY+index, "Cluster "+index);
	}
	
	public static void setClusterName(int index, String name){
		INSTANCE.prefs.put(CLUSTER_KEY+index, name);
	}
	
	public static Color getFeatureColor(String feature){
		return getFeatureColor(feature, Color.WHITE);
	}
	
	public static Color getFeatureColor(String feature, Color defaultColor){
		String colorAsString = INSTANCE.prefs.get(COLOR_KEY+feature, parseColor(defaultColor));		
		return parseColor(colorAsString);
	}
	
	public static void setFeatureColor(String feature, Color color){
		String colorAsString = parseColor(color);
		INSTANCE.prefs.put(COLOR_KEY+feature, colorAsString);
	}
	
	public static int getInteger(String identifyer, int defaultValue) {
		return INSTANCE.prefs.getInt(identifyer, defaultValue);
	}
	
	public static void storeInteger(String identifyer, int value) {
		INSTANCE.prefs.putInt(identifyer, value);
	}
	
	//endregion *********************************************************************************************
	//		
	//region							PRIVATE 		
	//*******************************************************************************************************
	
	private static String parseColor(Color color){
		return color.getRed() + ":" + color.getGreen() +":" + color.getBlue() + ":" + color.getOpacity();
	}
	private static Color parseColor(String color){
		String[] colorStrings = color.split(":");
		double[] colorValues = new double[4];
		for(int i = 0; i < colorValues.length; i++)
			colorValues[i] = Double.valueOf( colorStrings[i] );
		
		return new Color(colorValues[0], colorValues[1], colorValues[2], colorValues[3]);
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
