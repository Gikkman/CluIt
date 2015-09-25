package com.cluit.visual.application.view.tabs;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Results;

public abstract class _AbstractTableTab implements Initializable{
	private static int DEBUG_INT = 0;
	
	protected final String tabID;
	
	
	public _AbstractTableTab() {
		tabID = getTabID();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		VariableSingleton.getInstance().setResultListener( (evt) -> backgroundPaint( evt.getNewValue() ) );
	}
	
	private void backgroundPaint(Object args){
		if( args != null && args instanceof Results ){
			Results r = (Results) args;
			Platform.runLater( () -> paintThisTab(r) );			
		}
		else{			
			Platform.runLater( () -> DEBUG_paintThisTab(DEBUG_INT++) );		
		}
	}
	
	protected abstract String getTabID();

	protected abstract void DEBUG_paintThisTab(int i);
	protected abstract void paintThisTab(Results r);
}
