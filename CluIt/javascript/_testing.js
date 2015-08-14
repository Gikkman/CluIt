
var calculate = function() {
	var API = Java.type("com.cluit.api.API").create();
	API.test();
	
	print( API.getFieldValue("Hello") );
}