

var fields = function() {
	JFX_API.createField_IntegerSpinner("foo", 1, 9, 5, 2 );
	JFX_API.createField_CheckBox("bar", true );
}

var calculate = function() {	
	print( API.getFieldValue("foo") );	
	print( API.getFieldValue("bar") );
	
	API.finish();
}