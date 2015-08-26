package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.visual.application.Main;

public class MenuBarView {
	
	@FXML protected void open_ExcelImport(ActionEvent event){
		//TODO: Make window unique (i.e. disallow multiples)
		try{    
            Stage stage = new Stage();
            System.out.println( Main.class );
            
            URL url = Main.class.getResource("view/ExcelImporter.fxml");            
            Parent root = FXMLLoader.load( url );    
	        Scene scene = new Scene(root);
	        stage.setTitle("Excel Importer");
	        stage.setScene(scene); 
	        
	        
	        stage.show();	        
	        stage.setMaxHeight( stage.getHeight() );
	        stage.setMinHeight( stage.getHeight() );
	        stage.setMinWidth( stage.getWidth() -200 );
	        stage.setOnCloseRequest( (ew) -> MethodMapper.invoke(Const.METHOD_UNLOAD_EXCEL_SHEET) );
	        
	        
          } catch(Exception e) {
  			e.printStackTrace();
  		}
	}
}
