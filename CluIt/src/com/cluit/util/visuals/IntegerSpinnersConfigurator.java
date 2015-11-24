package com.cluit.util.visuals;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**UI code is messy...
 * 
 * This class configures the parameter JFX Spinner to only accept ingeters.
 * 
 * @author Simon
 *
 */
public class IntegerSpinnersConfigurator {
	private IntegerSpinnersConfigurator() { }
		
	public static void configure(Spinner<Integer> spinner,  int min, int max, int defaultValue, int stepSize){
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, defaultValue, stepSize) );
	    spinner.setEditable(true);
	    
	    EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if( event.getCode() == KeyCode.ENTER){
					//If we don't override this event, the value factory will be fed the value that is currently written
					//into the editor-text. If the value is too high (or low), that would fire the onChange-event twice.
					//
					//Instead, we use our own method, to ensure the event is only fired once
					commitEditorText(spinner, min, max, defaultValue);
					event.consume();
				}
			}
		};
	    
	    EventHandler<KeyEvent> keyTypedHandler = new EventHandler<KeyEvent>() {
	       @Override
	    	public void handle(KeyEvent event) {
	            switch (event.getCharacter() ) {
				case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9": case "-":
					break;	
				default:
					//Anything but numeric input is discarded
					event.consume();
				}	            	
	        }
	    };
	    
	    //Sometimes the value might be invalid. For example, if the user pastes something non-numeric
	    //If so, we reset the field to the previous value.
	    //We do however allow the user to clear the entire field, and to only have a minus sign. These potential
	    //errors are cough by the "onFocus" method
	    ChangeListener<String> pasteListener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if( newValue.length() == 0 || newValue.equals("-") )
					return;
				
				if( !newValue.matches("-?[0-9]+") )
					spinner.getEditor().textProperty().set( oldValue );
			}
		};

	    spinner.getEditor().addEventHandler(KeyEvent.KEY_TYPED, keyTypedHandler);
	    spinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, keyPressedHandler);
	    spinner.getEditor().textProperty().addListener( pasteListener );
	    //Make sure the current value in the edit-text box is committed as soon as it looses focus
	    spinner.focusedProperty().addListener( (ov, oldV, newV) -> { if(!newV) commitEditorText(spinner, min, max, defaultValue); } );

	}
	
	 private static void commitEditorText(Spinner<Integer> spinner, int min, int max, int defaultValue) {
		 //Method by Jonathan Fortin @ StackOverflow.
		 //Commits the value from the spinners edit-text field to its value factory
        
		String text = spinner.getEditor().getText();
         //Format the value correctly. We could just commit a value to the factory that is too large or
		 //to small, but that would fire the valueProperty-listener twice.
		Integer value = text.matches("-?[0-9]+") ? Integer.valueOf(text) : defaultValue;
        value = value < min ? min : value > max ? max : value;
        
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            valueFactory.setValue(value);
            spinner.getEditor().textProperty().set( String.valueOf(valueFactory.getValue()) );
        }
    }

	public static void configure(Spinner<Integer> spinner) {
		IntegerSpinnerValueFactory vf = (IntegerSpinnerValueFactory) spinner.getValueFactory();
		int min = vf.getMin();
		int max = vf.getMax();
		int defaultValue = vf.getValue();
		int stepSize = vf.getAmountToStepBy();
		configure(spinner, min, max, defaultValue, stepSize);
	}
}
