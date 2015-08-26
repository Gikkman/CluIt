package com.cluit.visual.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;

public class TabPaneView implements Initializable{
	@FXML TabPane tab_pane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MethodMapper.addMethod(Const.METHOD_RENDERING_ENGINE_PAINT, (args) -> paintTabs(args) );
	}

	private void paintTabs(Object[] args) {
		//TODO
	}	
}
