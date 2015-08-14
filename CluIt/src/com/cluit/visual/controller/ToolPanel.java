package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;

public class ToolPanel implements Initializable{
	@FXML GridPane grid_tools;
	private int mRows = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
		
	private void addIntegerSpinner(Object ... args){
		try {
			String name 	 = (String) args[0];
			int min 		 = (int) args[1];
			int max 		 = (int) args[2];
			int defautlValue = (int) args[3];
			int stepSize 	 = (int) args[4];
			
			//TODO editable spinners
			Spinner<Integer> spinner = new Spinner<>();
			spinner.setEditable(false);
			IntegerSpinnerValueFactory spinnerFactory = new IntegerSpinnerValueFactory(min, max, defautlValue, stepSize);
			spinner.setValueFactory(spinnerFactory);
			
			spinner.valueProperty().addListener( (observable, oldValue, newValue) 
					-> VariableSingleton.getInstance().putObject(Const.NAME_JS_REFERENCE+name, newValue) );
			VariableSingleton.getInstance().putObject(Const.NAME_JS_REFERENCE+name, defautlValue);
			
			Label label = new Label(name+":");
			
			grid_tools.addRow(mRows++, label, spinner);		
			
			
		} catch (Exception e) {
			MethodMapper.invoke(Const.EXCEPTION_API, "Error when trying to instantiate spinner with args: "+args);
		}
	}
	
	@FXML protected void test(ActionEvent e){
		Object[] args = {"Hello", 1, 9, 5, 2 };
		addIntegerSpinner(args);
	}
}
