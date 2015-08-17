package com.cluit.visual.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.cluit.util.Const;
import com.cluit.util.AoP.Invocation;
import com.cluit.util.AoP.MethodMapper;

public class ExceptionDialogue {
	public void initialize() {
		//TODO: Different types of exceptions
		MethodMapper.addMethod(Const.METHOD_EXCEPTION_GENERAL, new RemoteMethod() );		
		MethodMapper.addMethod(Const.METHOD_EXCEPTION_API,     new RemoteMethod() );		
		MethodMapper.addMethod(Const.METHOD_EXCEPTION_JS,      new RemoteMethod() );			
	}
	
	
	private class RemoteMethod implements Invocation{
		@Override
		public void execute(Object... args) {
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Exception detected!");
					alert.setContentText((String) args[0] );
					Exception e = (Exception) args[1];
					
					// Create expandable Exception.
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String exceptionText = sw.toString();
					
					Label label = new Label("The exception stacktrace was:");
					
					TextArea textArea = new TextArea(exceptionText);
					textArea.setEditable(false);
					textArea.setWrapText(true);

					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);

					GridPane expContent = new GridPane();
					expContent.setMaxWidth(Double.MAX_VALUE);
					expContent.add(label, 0, 0);
					expContent.add(textArea, 0, 1);

					// Set expandable Exception into the dialog pane.
					alert.getDialogPane().setExpandableContent(expContent);

					alert.showAndWait();				
				}
			} );
		}
	}
}
