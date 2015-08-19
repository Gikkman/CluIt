package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ExcelImporter implements Initializable{
	@FXML private Button button_load;
	@FXML private Button button_import;
	@FXML private Label  label_load_file;
	@FXML private ScrollPane scroll_pane;
	@FXML private GridPane feature_grid;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}	
	
	@FXML protected void button_load_OnClick(ActionEvent event){
		System.out.println("LOAD");
	}
	
	@FXML protected void button_import_OnClick(ActionEvent event){
		System.out.println("IMPORT");
	}
	
	@FXML protected void feature_grid_OnScroll(ScrollEvent event){
		int scrollDir= event.getDeltaY() > 0 ? 1 : -1;
		double panePos   = scroll_pane.getHvalue();	
		//TODO: Fix this so timething better
		scroll_pane.setHvalue( panePos + 0.3 * scrollDir );
	}
}
