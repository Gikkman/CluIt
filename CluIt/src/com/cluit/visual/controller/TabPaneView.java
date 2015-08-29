package com.cluit.visual.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.MultiMethodMapper;
import com.cluit.visual.application.Main;

public class TabPaneView implements Initializable{
	@FXML TabPane tab_pane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		URL url = Main.class.getResource("view/tabs/");
		File folder = new File( url.getPath() );
		File[] tabs = folder.listFiles( (dir, name) -> { return name.endsWith(".fxml"); } );		
		for( File f : tabs ){
			try {
				Parent p = FXMLLoader.load( Main.class.getResource( "view/tabs/" + f.getName()  ) );
				Tab t = new Tab( f.getName().substring(0, f.getName().indexOf(".") ));
				t.setContent(p);
				tab_pane.getTabs().add(t);			
			} catch (IOException e) {
				MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Could not load FXML "+f.getName(), e);
				e.printStackTrace();
			}
		}
		
		MethodMapper.addMethod(Const.METHOD_RENDERING_ENGINE_PAINT, (args) -> paintTabs(args) );	
	}

	private void paintTabs(Object[] args) {
		MultiMethodMapper.invoke(Const.MULTI_METHOD_PAINT_TABS, args);
	}	
}
