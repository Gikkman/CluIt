package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.main.ClusteringEngine;
import com.cluit.main.ClusteringEngine.Message;
import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.visuals.IntegerSpinnersConfigurator;

public class ControlPanelView implements Initializable {
	@FXML private ComboBox<String> comboBox_Algorithm;
	@FXML private CheckBox checkBox_Normalize;
	@FXML private Spinner<Integer> spinner_NumberClusters;
	
	@FXML private Button button_OK;
	@FXML private Button button_RunQueue;
	@FXML private Button button_Queue;
	 
	private ClusteringEngine mCluEngine;
	 
	private String   mComboOldValue = "";
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			updateAlgorithmsComboBox();
			comboBox_Algorithm_changed(new ActionEvent() );
		} catch (FileNotFoundException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Could not update list of availible Javascripts", e);
		}
		
		comboBox_Algorithm.setVisibleRowCount(10);
		
		IntegerSpinnersConfigurator.configure(spinner_NumberClusters);
		spinner_NumberClusters.valueProperty().addListener(( obs, oldValue, newValue) -> 
															 VariableSingleton.getInstance().putInt(Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS, newValue) );
		VariableSingleton.getInstance().putInt(Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS, spinner_NumberClusters.getValue() );
	}
	
	@FXML protected void checkbox_Normalize_Clicked(ActionEvent event) {	
		VariableSingleton.getInstance().putBool(Const.V_KEY_CHECKBOX_NORMALIZE, checkBox_Normalize.isSelected() );
	}
	
	@FXML protected void comboBox_Algorithm_changed(ActionEvent event){
		String val = comboBox_Algorithm.getValue();
		if( val == null || val.equals(mComboOldValue) )
			return;
		
		File algorithmFile = new File( Const.DIR_JAVASCRIPT + val + ".js" );		
		VariableSingleton.getInstance().putObject( Const.V_KEY_COMBOBOX_JAVASCRIPT_FILE, algorithmFile);
		VariableSingleton.getInstance().clearUserDefinedMap();
		
		if( mCluEngine == null){
			mCluEngine = new ClusteringEngine();
			mCluEngine.start();
		}
		mCluEngine.queueMessage( ClusteringEngine.Message.INIT_MESSAGE );
		
		mComboOldValue = val;
	}
	
	@FXML protected void button_OK_Clicked(ActionEvent event){				
		addReenableMethod(button_OK, button_RunQueue);		
		mCluEngine.queueMessage( Message.CLUSTER_MESSAGE );
				
	}
	
	@FXML protected void button_Queue_Clicked(ActionEvent event){
		mCluEngine.queueMessage( Message.ENQUEUE_MESSAGE );
	}
	
	@FXML protected void button_RunQueue_Clicked(ActionEvent event){
		addReenableMethod(button_OK, button_Queue, button_RunQueue);
		mCluEngine.queueMessage( Message.RUNQUEUE_MESSAGE );
	}
	
	@FXML protected void test() throws FileNotFoundException{
		updateAlgorithmsComboBox();
	}
	
	/**Updates the list of availible Javascripts for the algorithm's combo box
	 * 
	 * @throws FileNotFoundException Thrown if the user has removed the /javascript folder from the project
	 */
	private void updateAlgorithmsComboBox() throws FileNotFoundException{
		File dir = new File( Const.DIR_JAVASCRIPT );
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
		
		
		//Causes "index exceeds maxCellCount" now and again, if the user removes the last file from the list during runtime.
		//There is nothing I can do about it though, it is reported as a bug
		String current = comboBox_Algorithm.getValue();
		comboBox_Algorithm.getItems().clear();
		comboBox_Algorithm.getItems().addAll( fileNames );
		
		comboBox_Algorithm.setVisibleRowCount( comboBox_Algorithm.getItems().size() );
		
		if( comboBox_Algorithm.getItems().size() == 0)
			comboBox_Algorithm.setValue("");
		else if( !comboBox_Algorithm.getItems().contains(current) )
			comboBox_Algorithm.setValue( fileNames[0] );
		else
			comboBox_Algorithm.setValue( current );
	}

	/**Disables the argument nodes and adds a method to the method mapper for reenabling them again after the action is finished
	 * 
	 * @param nodes
	 */
	private void addReenableMethod(Node ... nodes){
		for( Node n : nodes )
			n.setDisable(true);
		
		MethodMapper.addMethod(Const.METHOD_DONE_BUTTON_REACTIVATE, (args) -> {
			try { Thread.sleep(1000); } 
			catch (InterruptedException e) { }
			for( Node n : nodes ){
				n.setDisable(false);
			}
			MethodMapper.removeMethod(Const.METHOD_DONE_BUTTON_REACTIVATE);
		}
	);	
	}
}
