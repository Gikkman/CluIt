package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.main.Overseer;
import com.cluit.util.Const;
import com.cluit.util.AoP.Invocation;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;

public class ControlPanel implements Initializable {
	 @FXML private ComboBox<String> comboBox_Algorithm;
	 @FXML private CheckBox checkBox_Normalize;
	 @FXML private Button button_OK;
	 @FXML private Spinner<Integer> spinner_NumberClusters;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			updateAlgorithmsComboBox();
			comboBox_Algorithm_changed(new ActionEvent() );
		} catch (FileNotFoundException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Could not update list of availible Javascripts", e);
		}
		
		//TODO: editable spinners
		spinner_NumberClusters.valueProperty().addListener(( obs, oldValue, newValue) -> 
															 VariableSingleton.getInstance().putInt(Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS, newValue) );
		VariableSingleton.getInstance().putInt(Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS, spinner_NumberClusters.getValue() );
	}
	
	@FXML protected void checkbox_Normalize_Clicked(ActionEvent event) {	
		VariableSingleton.getInstance().putBool(Const.V_KEY_CHECKBOX_NORMALIZE, checkBox_Normalize.isSelected() );
	}
	
	@FXML protected void comboBox_Algorithm_changed(ActionEvent event){
		if( comboBox_Algorithm.getValue() == null )
			return;
		
		File algorithmFile = new File( Const.STRING_JAVASCRIPT_PATH + comboBox_Algorithm.getValue() + ".js" );		
		System.out.println(algorithmFile +" "+com.cluit.util.methods.MiscUtils.getStackPos());
		VariableSingleton.getInstance().putObject( Const.V_KEY_COMBOBOX_JAVASCRIPT_FILE, algorithmFile);
		
		
	}
	
	@FXML protected void button_OK_Clicked(ActionEvent event){		
		button_OK.setDisable( true );
		Overseer o = Overseer.create();
		
		if( o != null ){
			o.cluster();	
			MethodMapper.addMethod(Const.METHOD_DONE_BUTTON_REACTIVATE, new Invocation() {			
				@Override
				public void execute(Object... args) {
					try { Thread.sleep(1000); } 
					catch (InterruptedException e) { }
					button_OK.setDisable(false);
					MethodMapper.removeMethod(Const.METHOD_DONE_BUTTON_REACTIVATE);
				}
			});	
		} else {
			button_OK.setDisable( false );
		}			
	}
	
	@FXML protected void test() throws FileNotFoundException{
		updateAlgorithmsComboBox();
	}
	
	/**Updates the list of availible Javascripts for the algorithm's combo box
	 * 
	 * @throws FileNotFoundException Thrown if the user has removed the /javascript folder from the project
	 */
	private void updateAlgorithmsComboBox() throws FileNotFoundException{
		File dir = new File( Const.STRING_JAVASCRIPT_PATH );
		if( !dir.exists() )
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Javascript directory not found. Have you removed it?", new Exception() );
		
		File[] files = dir.listFiles( new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if( pathname.getName().endsWith(".js") && pathname.isFile() )
					return true;
				return false;
			}
		});
		
		String[] fileNames = new String[ files.length ];
		for( int i = 0; i < files.length; i++){
			int nameLenght = files[i].getName().length();
			fileNames[i] = files[i].getName().substring(0, nameLenght-3);
		}
		
		//TODO: Clean this up.Causes "index exceeds maxCellCount" now and again, if the user removes the last file from the list during runtime
		String current = comboBox_Algorithm.getValue();
		comboBox_Algorithm.getItems().clear();
		comboBox_Algorithm.getItems().addAll( fileNames );
		
		if( comboBox_Algorithm.getItems().size() == 0)
			comboBox_Algorithm.setValue("");
		else if( !comboBox_Algorithm.getItems().contains(current) )
			comboBox_Algorithm.setValue( fileNames[0] );
		else
			comboBox_Algorithm.setValue( current );
	}
}
