package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuBarView {
	
	@FXML protected void open_ExcelImport(ActionEvent event){
		//TODO: Make window unique (i.e. disallow multiples)
		try{    
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../application/view/ExcelImporter.fxml"));    
	        Scene scene = new Scene(root);
	        stage.setTitle("Excel Importer");
	        stage.setScene(scene); 
	        stage.show();
	        
	        stage.setMaxHeight( stage.getHeight() );
	        stage.setMinHeight( stage.getHeight() );
	        stage.setMinWidth( stage.getWidth() -200 );
	        
          } catch(Exception e) {
  			e.printStackTrace();
  		}
	}
}
