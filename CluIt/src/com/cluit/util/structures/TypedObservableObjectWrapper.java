package com.cluit.util.structures;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TypedObservableObjectWrapper<T> {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private T object;
	
	public TypedObservableObjectWrapper(T object) {
		this.object = object;
	}
	
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
}
