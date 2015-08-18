var entries;
var numberOfClusters;
var k;
var cursor;

var fields = function() {
	JFX_API.createField_IntegerSpinner("Neighbours", 1, 6, 3);
}

var calculate = function() {
	print("initializing")
	
	entries = API.getAllEntries();
	numberOfClusters = API.getTargetNumberOfClusters();
	
	entries = API.shuffleArray(entries);
	
	cursor = 0;
	addClusters();
	
	k = API.getFieldValue("Neighbours");
	cluster();
	
	API.finish();
}

//Add the initial entries to the initial clusters
var addClusters = function() {
	for( var i = 0; i < numberOfClusters; i++ ){
		var e = entries[ getCursor() ];
		var cluster = API.addCluster( e.getAllEntries() );
		API.addEntryToCluster(e, cluster);		
	}
}

var cluster = function(){
	var i = getCursor();
	
	while( i < entries.length ){
		
		var e = entries[i];
		var cluster = doVote(e);
		API.addEntryToCluster(e, cluster);
		
		i = getCursor();
	}
	
}

var doVote = function(e){
	var List = Java.type('java.util.LinkedList');
	var voters = new List();
	
	getClosestPoints(e, voters);
	
	var cluster = castVotes( voters );
		
	return (cluster != -1) ? cluster : doVoteRecursive( voters ); 
}

var doVoteRecursive = function(voters) {
	//Voters are sorted in descending order, from closest to the Entry to furthest away
	//We remove the voter that is furthest from the point in question, and then do voting again
	voters.removeLast();	
	var cluster = castVotes(voters);

	return cluster != -1 ? cluster : doVoteRecursive(voters);
}

var getClosestPoints = function(e, voters) {
	var clusteredEntries = API.getClusteredEntries();
	var queue = API.createEntryPriorityQueue_Max();
	
	// For each clustered point:
	// 
	// if	queue isn't full yet, add this point to the queue.
	// 
	// else if this point is closer to the targetPoint than the point at the head of the queue,
	// 		remove the head and add this point to the queue. 
	// 
	for each (var entry in clusteredEntries ){
		var dist = API.getDistance(entry, e);
		if( queue.size() < k ) {
			queue.add( dist, entry );
		} else {
			if( queue.peekKey() > dist ){	
				queue.poll();
				queue.add( dist, entry );
			}
		}
	}
	//Since we can't iterate over the KeyPriorityQueue, we reformat it
	//to a linked list, ordered from closest to farthest away
	while( !queue.isEmpty() ) {
		voters.addFirst( queue.poll() );
	}

}

var castVotes = function(voters) {
	var draw = false;
	var ballot = new Array( numberOfClusters+1 ).join('0').split('').map(parseFloat)
	
	//Each voter casts a vote for his cluster
	for each (var e in voters){
		var cluster = API.getEntryMembership(e);
		ballot[cluster]++;
	}
	//Find the cluster with the most votes		
	var winner = 0;
	for(var i = 1; i < ballot.length; i++){
		if( ballot[i] > ballot[winner] ) {
			winner = i;
			draw = false;
		}
		else if( ballot[i] == ballot[winner] ){
			draw = true;
		}
	}
	if( draw )
		return -1;
	return winner;
}

var getCursor = function(){
	return cursor++;
}


















