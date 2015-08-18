package com.cluit.visual.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
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
	@FXML GridPane tools_grid;
	private int mRows = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MethodMapper.addMethod(Const.METHOD_ADD_INTEGER_SPINNER, (args) -> addIntegerSpinner(args) );
		MethodMapper.addMethod(Const.METHOD_ADD_CHECKBOX, (args) -> addCheckBox(args) );
		MethodMapper.addMethod(Const.METHOD_CLEAR_TOOLS_PANE, (args) -> clearGrid() );
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
			
			//Set the function for mapping this spinners value, so the API can later find it for the JS-scritp
			spinner.valueProperty().addListener( (observable, oldValue, newValue) 
					-> VariableSingleton.getInstance().putObject(Const.V_KEY_JS_REFERENCE+name, newValue) );
			VariableSingleton.getInstance().putObject(Const.V_KEY_JS_REFERENCE+name, defautlValue);
			
			addGridObject(name, spinner);			
			
		} catch (Exception e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_API, "Error when trying to instantiate spinner with args: "+args, e);
		}
	}
	
	private void addCheckBox(Object ... args){
		try{
			String name 	 	 = (String) args[0];
			boolean defautlValue = (boolean)args[1];
			
			CheckBox checkbox = new CheckBox();
			checkbox.setSelected( defautlValue );
			
			checkbox.selectedProperty().addListener( (observable, oldValue, newValue) 
					-> VariableSingleton.getInstance().putObject(Const.V_KEY_JS_REFERENCE+name, newValue) );
			VariableSingleton.getInstance().putObject(Const.V_KEY_JS_REFERENCE+name, defautlValue);
			
			addGridObject(name, checkbox);
			
		} catch (Exception e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_API, "Error when trying to instantiate checkbox with args: "+args, e);
		}
	}
	
	private void addGridObject(String name, Node element){
		Label label = new Label(name+":");
		Platform.runLater( () -> tools_grid.addRow(mRows++, label, element) ); 	
	}

	private void clearGrid(){
		Platform.runLater( () -> tools_grid.getChildren().clear() );
	}
	
	@FXML protected void test(ActionEvent e){
		System.out.println();
	}
}
