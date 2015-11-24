package com.cluit.util.structures;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**This class is used to create observable objects.
 * 
 * An observable object can have listners attached to them. Whenever the observable is changed, the listner fires.
 * 
 * @author Simon
 *
 * @param <T>
 */
public class TypedObservableObjectWrapper<T> {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private T object;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	public TypedObservableObjectWrapper(T object) {
		this.object = object;
	}
	
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener( listener );
    }
	
	public void setValue(T newValue){
		T oldValue = object;		
		object = newValue;
		this.pcs.firePropertyChange("value", oldValue, newValue);
		
	}
	
	public T getValue(){
		return object;
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
