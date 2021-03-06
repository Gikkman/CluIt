package com.cluit.visual.application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.cluit.visual.controller.ExceptionDialogue;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setMinWidth(815);
			primaryStage.setMinHeight(660);
			
			Parent root = FXMLLoader.load(getClass().getResource("view/MainWindow.fxml"));    
	        Scene scene = new Scene(root, 615, 510);
	        primaryStage.setTitle("FXML Welcome");
	        primaryStage.setScene(scene);
	        
	        ExceptionDialogue e = new ExceptionDialogue();
	        e.initialize();
	        
	        primaryStage.show();
	        
	        primaryStage.setOnCloseRequest( (value) -> Platform.exit() );
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
