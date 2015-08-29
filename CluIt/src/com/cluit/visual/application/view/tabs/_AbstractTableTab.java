package com.cluit.visual.application.view.tabs;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.util.Const;
import com.cluit.util.AoP.MultiMethodMapper;
import com.cluit.util.dataTypes.Results;

public abstract class _AbstractTableTab implements Initializable{
	protected final String tabID;
	
	public _AbstractTableTab() {
		tabID = getTabID();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MultiMethodMapper.addMethod(Const.MULTI_METHOD_PAINT_TABS, tabID, (args) -> backgroundPaint(args) );		
	}
	
	private void backgroundPaint(Object[] args){
		if( args[0] instanceof Results ){
			Results r = (Results) args[0];
			Platform.runLater( () -> paintThisTab(r) );			
		}
		else{
			int i = (int) args[0];
			Platform.runLater( () -> DEBUG_paintThisTab(i) );		
		}
	}
	
	protected abstract String getTabID();

	protected abstract void DEBUG_paintThisTab(int i);
	protected abstract void paintThisTab(Results r);
}
