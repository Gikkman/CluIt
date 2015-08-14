
var calculate = function() {
	var API = Java.type("api.API").create();
	API.test();
	
	print( API.getFieldValue("Hello") );
}