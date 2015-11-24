package com.cluit.api;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;

/**This class is inteded to be used as an API for Javascript to create custom fields in the controll window. The class defines a 
 * collection of methods which does all the logic needed to create custom fields. <br>
 * The fields can then be accessed by calling the API-method getFieldValue(String name), where name is the name the field was given by the user
 * at creation
 * 
 * @author Simon
 *
 */
public class JFX_API {
	//*******************************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	private JFX_API() {}
	
	//endregion *********************************************************************************************
	//region								STATIC			
	//*******************************************************************************************************

	public static JFX_API create_this_API(){
		return new JFX_API();
	}

	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************

	/**Creates an integer spinner in the tools panel
	 * 
	 * @param name Reference name for the Spinner
	 * @param min The minimum value for the spinner
	 * @param max The maximum value for the spinner
	 * @param default The default value for the spinner
	 * @param step How many digits does a single click increment/decrement the spinner? 
	 */
	public void createField_IntegerSpinner(String name, int ... values){
		Object[] args = new Object[5];
		args[0] = name;		
		
		for(int i = 0; i < 4; i++){
			if( values.length > i)
				args[i+1] = values[i];
			else
				MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Could not create an integer spinner. Wrong number of arguments.The syntax for creating an integer spinner is:\n\n JFX_API.createField_IntegerSpinner(String name, int min, int max, int default, int step)", new Exception() );
		}
		
		MethodMapper.invoke(Const.METHOD_ADD_INTEGER_SPINNER, args);
	}

	/**Creates a checkbox in the tools panel
	 * 
	 * @param name Reference name for the CheckBox
	 * @param values Default value for the checkbox
	 */
	public void createField_CheckBox(String name, boolean ... values){
		Object[] args = new Object[2];
		args[0] = name;
		args[1] = (values.length > 0) ? values[0] : false;
		
		MethodMapper.invoke(Const.METHOD_ADD_CHECKBOX, args);
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
