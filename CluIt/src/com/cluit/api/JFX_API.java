package com.cluit.api;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;

public class JFX_API {
	
	private JFX_API() {}
	
	public static JFX_API create_this_API(){
		return new JFX_API();
	}

	public void createField_IntegerSpinner(String name, int ... values){
		int[] defaults = {0, 100, 5, 1};
		Object[] args = new Object[5];
		args[0] = name;		
		
		for(int i = 0; i < 4; i++){
			if( values.length > i)
				args[i+1] = values[i];
			else
				args[i+1] = defaults[i];
		}
		
		MethodMapper.invoke(Const.METHOD_ADD_INTEGER_SPINNER, args);
	}

	public void createField_CheckBox(String name, boolean ... values){
		Object[] args = new Object[2];
		args[0] = name;
		args[1] = (values.length > 0) ? values[0] : false;
		
		MethodMapper.invoke(Const.METHOD_ADD_CHECKBOX, args);
	}
}
