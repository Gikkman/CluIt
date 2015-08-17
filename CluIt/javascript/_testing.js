

var fields = function() {
	API.createField_IntegerSpinner("Hello", 1, 9, 5, 2 );
	API.createField_CheckBox("World", true );
}

var calculate = function() {	
	print( API.getFieldValue("Hello") );
	
	print( API.getFieldValue("World") );
}