package com.cluit.visual.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.io.jar.JarResoruceLister;
import com.cluit.visual.application.Main;

public class TabPaneView implements Initializable{
	@FXML TabPane tab_pane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
		 try {
			//First, we fetch all files from the tabs folder and store them in the fxmlFiles array list
			ArrayList<String> fxmlFiles = new ArrayList<>();
			String[] files = JarResoruceLister.getResourceListing(Main.class, "view/tabs/");
			Arrays.sort(files);
			
			for(String s : files)
				if( s.endsWith(".fxml") )
					fxmlFiles.add(s);
			
			//Then, we load all the tabs and add them to the tab view
			for( String tab : fxmlFiles ){
				try {
					Parent p = FXMLLoader.load( Main.class.getResource( "view/tabs/" + tab  ) );
					Tab t = new Tab( tab.substring(0, tab.indexOf(".") ));
					
					t.setContent(p);
					tab_pane.getTabs().add(t);			
				} catch (IOException e) {
					MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Could not load FXML "+tab, e);
					e.printStackTrace();
				}
			
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}	
}
