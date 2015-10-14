Contract
	A Javascript that is intended to be read by CluIt has to follow a strict contract, otherwise the program won't work. Instructions for how to write 
	a propper Javascript will follow in this document, as well as explanations of the various API methods that CluIt implements.
	
	First, the Javascript-file has to implement two functions. CluIt will check that these two functions are present in the Javascript. If not, the program will throw an error.
		var fields = function() { ... }
		var calculate = function() { ... }
	
	The fields() function is called when the script is selected in the scrips-combo box in CluIt's main window. This method should tell CluIt which custom fields this scripts would 
	like to add to the control panel. To add fields, consult the JFX_API section bellow. 
	The calculate() function is called when CluIt is asked to perform a clustering calculation using the selected script. The last call in this method _MUST_ be API.finish();

JFX_API
	Methods in this JFX_API should only be called in the fields() function of the Javascript.
	
	Methods under this API creates custom fields in the main window's control panel. These fields are identified by their name, 
	and the value associated with a certain field can be accessed from a Javascript by calling the API method getFieldValue(String name)
	

	createField_IntegerSpinner(String name, int min, int max, int default, int step)
		String name  Reference name for the Spinner
		int min 	 The minimum value for the spinner
		int max 	 The maximum value for the spinner
		int default  The default value for the spinner
		int step 	 How many digits does a single click increment/decrement the spinner? 
	
	***Creates an Integer Spinner in the control pane in the main window, associated with the given name*** 
	
	
	public void createField_CheckBox(String name, boolean default)
		String name  	Reference name for the CheckBox
		boolean default The default value of the check box
		
	***Creates a Check Box in the control pane in the main window, associated with the given name*** 
	
	
API
	