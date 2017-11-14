const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const express = require('express');
const cookieParser = require('cookie-parser')();
const cors = require('cors')({origin: true});
const app = express();

// TBI: rewrite by chaining thenables together instead of nesting

// main validation function to be called last in middleware
const validateFirebaseIdToken = (req, res, next) => {
	console.log('Check if request is authorized with Firebase ID token');
	
	if ((!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ')) &&
		!req.cookies.__session) {
		console.error('No Firebase ID token was passed as a Bearer token in the Authorization header.' );
		res.status(403).send({status:'Unauthorized'});
		return;
	}

	let idToken;
	if (req.headers.authorization && req.headers.authorization.startsWith('Bearer ')) {
		console.log('Found "Authorization" header');
		// Read the ID Token from the Authorization header.
		idToken = req.headers.authorization.split('Bearer ')[1];
	} else {
		console.log('Found "__session" cookie');
		// Read the ID Token from cookie.
		idToken = req.cookies.__session;
	}

	// verify the token we got
	admin.auth().verifyIdToken(idToken).then(decodedIdToken => {
		console.log('ID Token correctly decoded', decodedIdToken);
		var uid = decodedToken.uid;

		// check if this is an admin
		var ref = admin.database().ref('/Roles').child(uid);
		ref.once("value", function(data) {
  			var role = data.val();
  			if ("Administrator" !== role ){
  				console.error('You do not have admin privs');
				res.status(403).send({status:'Unauthorized'});
				return;
  			}
		}, function( error ){
			console.log( "Couldn't retrieve the user's role for verification. ");
			res.status(403).send({status:error.message});
			return;
		});

    	console.log("Successfully verified");
    	res.status(200).send({status:'Request Succeeded'});
    	next();
  	}).catch(error => {
		console.error('Error while verifying Firebase ID token:', error);
		res.status(403).send({status:error.message});
	});
};


// hook up all middleware parts of app
app.use(cors);
app.use(cookieParser);
//app.use(validateFirebaseIdToken);

/////////////////// ADD NEW USER TO AUTH AND DB ///////////////////
app.post('/admin/addUser', (req, res) => {
  	// grab original body
  	const original = req.body;

  	// parse json body request for user info
  	var fname = req.body.fname;
  	var lname = req.body.lname;
  	var email = req.body.email;
  	var role = req.body.role;
  	var photoURL = req.body.photoURL;
  	var phone = req.body.phone;
  	var displayName = req.body.displayName;
  	var uid, field4;
  	

  	// add user to authentication
	admin.auth().createUser({
		email: email,
	  	emailVerified: false,
	  	// use Math.random().toString(36).substr(2, 8) eventually?
	  	password: "123456",
	  	displayName: displayName,
	  	disabled: false
	}).then( function( userRecord ){

		uid = userRecord.uid;
		console.log("Successfully created new auth user: ", uid );

		// set last field of user entry
		field4 = "Patient" == role ? "Sessions" : "Patients"; 

	  	// Push the new message into the Realtime Database using the Firebase Admin SDK.
  		admin.database().ref('/Users').child(role).child(uid).set({
			"fname": fname,
			"lname": lname,
			"email": email,
			[field4]: null
  		}).then(function(){
    		admin.database().ref('/Roles').child(uid).set(role).then(function(){
    			console.log("added to db");
    			res.status(200).send({status:"Successfully added user"});

    			// 
    			// send user a new password
    			//

    		}).catch(function(error){
    			console.log("Couldn't add new user to roles", error);
				res.status(500).send({status:error.message});
    		});
  		});
	}).catch(function(error) {
		console.log("Error creating new user:", error);
		res.status(500).send({status:error.message});
	});
});



////////////// DELETE USER ///////////////////
app.post('/admin/deleteUser', (req, res) => {
  	// grab original body
  	const original = req.body;

  	// verify this user is an admin
  	// verify that they are not trying to delete another admin
  	// if its a patient, delete all that sessions data associated with the patient

  	// parse json body request for user info
  	var email = req.body.email;
  	var uid,role,sessions;

  	// get UID from email info
  	admin.auth().getUserByEmail( email ).then(function(userRecord) {
    		
		// now we have the user
		console.log("Successfully fetched user data:", userRecord.toJSON());
		uid = userRecord.uid;
		console.log("uid found is:", uid);

		// make sure this isn't an Admin
    	var ref = admin.database().ref('/Roles').child(uid);
		console.log("uid found is:", ref);
		ref.once("value", function(data) {
  			role = data.val();
			console.log("role found is:", role);
  			if ("Administrator" == role ){
  				console.error('Trying to delete an admin account');
				res.status(400).send({status:"Can't delete this account."});
				return;
  			}
		}, function( error ){
			console.log('Error trying to get user role.', error);
			res.status(500).send({status:error.message});
			return;
		});
  	}).catch(function(error) {
    	console.log("Error retrieving user to delete.", error);
    	res.status(500).send({status:error.message});
    	return;
  	});

  	// Now we are authorized and know we can delete this account
	admin.auth().deleteUser(uid).then( function(){
		console.log("Successfully deleted user from auth: ", uid);
		// now we need to delete the user and associated date from DB
		if (role == "Therapist"){
			var tRef = admin.database.ref('/Users').child(role).child(uid);
			tRef.remove().then(function(){

				// now we need to delete from roles section
				roleRef = admin.database.ref('/Roles').child(uid);
				roleRef.remove().then(function(){

					// remove from roles successful
					res.status(200).send({status:'Tharapist deleted successful'});
					console.log("Remove succeeded for Therapist" + uid);
					return;
				}).catch(function(error){
					console.log("Error removing user from Roles node.", error);
    				res.status(500).send({status:error.message});
    				return;
				});
			}).catch( function(error) {
				console.log("Remove failed on Tharapist: " + uid + "  " + error );
				res.status(500).send({status:error.message});
				return;
			});
		} else {
			// this is a patient so we need to clean up the sessions also
			var ref = admin.database().ref('/Users/Patient').child(uid).child( "Sessions" );
			ref.once("value", function(data) {
				// we need to check if this is empty
  				if (null == data.val()){
  					console.log("No extra session data to delete for patient.");
  					res.status(200).send({status:"delete patient session operation done." });
  					return;
  				} else{
  					// there are sessions we need to delete, val will be an object most likely and we need keys (session ids)
  					sessions = data.val();
  					console.log("sessions is type: ", typeof sessions );
  					sessions = data.val();

  					// loop through object props
  					Object.keys(sessions).forEach(function(key,index) {
  						var newRef = admin.database().ref('/Sessions').child(key);
  						newRef.remove().then( function(){
  							console.log("successfully deleted a session");
  						}).catch(function(error){
  							// couldn't remove session from sessions node
  							console.log("couldn't delete a session");
  							res.status(500).send({status:error.message});
  						});
					});
					console.log("made it to the end of delete");
					res.status(200).send({status:"Delete function done"});
					return;
  				}
			}, function( error ){
				console.log('Error trying to get user role.', error);
				res.status(500).send({status:error.message});
				return;
			});
			ref = admin.database().ref('/Users/Patient').child(uid);
			ref.remove().then( function(){
				res.status(200).send({status:'Patient deleted sucessful'});
				console.log("Remove succeeded for Patient." + uid);
				return;
			}).catch( function(error) {
				res.status(500).send({status:error.message});
				console.log("Remove failed on Patient: ", uid + "  " + error);
				return;
			});
		}
	}).catch(function(error) {
		console.log("Couldn't delete user for some reason: ", error);
		res.status(500).send({status:error.message});
		return;
	});
});

///////////////// GET USER //////////////////
app.post('/admin/getUser', (req, res) => {
  	// grab original body
  	const original = req.body;
  	
  	// parse json body request for user info
  	var email = req.body.email;
  	var uid, role, sessions;

  	// get UID from email info
  	admin.auth().getUserByEmail( email ).then(function(userRecord) {

		// now we have the user
		console.log("Successfully fetched user data:", userRecord.toJSON());
		uid = userRecord.uid;
		
		// make sure this isn't an Admin
    	var ref = admin.database().ref('/Roles').child(uid);
		ref.once("value", function(data) {
  			role = data.val();
  			if ("Administrator" == role ){
  				console.error('Trying to edit an admin account');
				res.status(400).send({status:"Can't edit this account."});
				return;
  			} else {
  				// call database for fname and lname
  				var ref1 = admin.database().ref('/Users').child( role ).child( uid );
  				ref1.once("value", function( data1 ) {
  					var user = data1.val();
  					console.log("User object: ", user);

  					// make user response object
	  				var resp = { "email"			: userRecord.email,
	  							 "uid"				: uid,
	  							 "fname"			: user.fname,
	  							 "lname"			: user.lname,
	  							 "role" 			: role, 
	  							 "displayName"		: userRecord.displayName, 
	  							 "phone"			: userRecord.phoneNumber,
	  							 "photoURL"			: userRecord.photoURL,
	  							 "emailVerified"	: userRecord.emailVerified,
	  							 "disabled"			: userRecord.disabled 
	  				};

	  				//^\+?[1-9]\d{1,14}$
	  				// send back user
	  				res.status(200).send(resp);


  				}, function(error){
  					console.log("Error getting user from db: ", error.message );
  					res.status(500).send({status:"Could not get user from DB."});
  				});
  			}
		}, function( error ){
			console.log('Error trying to get user role.', error);
			res.status(500).send({status:error.message});
			return;
		});
  	}).catch(function(error) {
    	console.log("Error retrieving user to delete.", error);
    	res.status(500).send({status:error.message});
    	return;
  	});

});


/////////////// UPDATE USER INFO //////////////////////////
app.post('/admin/updateUser', (req, res) => {

	// update DATABASE with new info
	function updateInDB(){
		let userRef = admin.database().ref('/Users').child(role).child(uid);
		userRef.child('fname').set(newFname);
		userRef.child('lname').set(newLname);
		userRef.child('email').set(newEmail);
		return true;
	}

	// get the new info
	// TBI: all of these need to be validated and cleaned
	var uid = req.body.uid;
	var role = req.body.role;
	var newFname = req.body.newFname;
	var newLname = req.body.newLname;
	var newEmail = req.body.newEmail;
	var newPhone = req.body.newPhone;
	var newPhotoURL = req.body.newPhotoURL;
	var newDisplayName = req.body.newDisplayName;
	var newDisabled = req.body.newDisabled;
	var newVerified = req.body.newVerified;

	console.log(req.body);

	// update AUTH with new info
	admin.auth().updateUser( uid, {
		email: newEmail,
		//phoneNumber: newPhone,
		emailVerified: newVerified,
		displayName: newDisplayName,
		//photoURL: newPhotoURL,
		disabled: newDisabled
	}).then(function(userRecord){
		console.log("successfully updated user: " + userRecord );
		if (updateInDB()){
			res.status(200).send({status:"update sucessful"});
		}else{
			console.log('you suck');
		}
		
	}).catch(function(error){
		console.log("error updating user" + error);
		res.status(500).send({status:error.message});
		return;
	});

});


/////////////////////// DELETE DATA ENDPOINT //////////////////
app.post('/admin/getSessions', (req, res) => {
  	// grab original body
  	const original = req.body;
  	
  	// parse json body request for user info
  	var email = req.body.email;
  	var uid, role, sessions;

  	// get UID from email info
  	admin.auth().getUserByEmail( email ).then(function(userRecord) {

		// now we have the user
		console.log("Successfully fetched user data:", userRecord.toJSON());
		uid = userRecord.uid;
		
		// make sure this is a patient
    	var ref = admin.database().ref('/Roles').child(uid);
		ref.once("value", function(data) {
  			role = data.val();
  			if ("Patient" != role ){
  				console.error('Can only get patients for delete data');
				res.status(400).send({status:'Can only get patients for delete data.'});
				return;
  			} else {
  				// call database for fname and lname
  				var ref1 = admin.database().ref('/Users').child( 'Patient' ).child( uid );
  				ref1.once("value", function( data1 ) {
  					var user = data1.val();
  					console.log("User object: ", user);
  					var sessions = user.sessions;

  					if ( sessions == null ){
  						// user has no sessions
  						console.log('This patient has no sessions');
  						res.status(400).send({status:'This patient has no sessions.'});
  						return;
  					} else {
  						// user has sessions
	  					res.status(200).send( sessions );
  					}
  				}, function(error){
  					console.log("Error getting user from db: ", error.message );
  					res.status(500).send({status:"Could not get user from DB."});
  				});
  			}
		}, function( error ){
			console.log('Error trying to get user role.', error);
			res.status(500).send({status:error.message});
			return;
		});
  	}).catch(function(error) {
    	console.log("Error retrieving user to delete.", error);
    	res.status(500).send({status:error.message});
    	return;
  	});

});

/////////////////////// DELETE SESSION ENDPOINT /////////////////////////////////
app.post('/admin/deleteData', (req, res) => {
	// parse json body request for user info
  	const sessionId = req.body.sessionId;
  	const userEmail = req.body.userEmail;
  	var uid;

  	// do some validation to make sure this is a session ID
  	// ...

  	// get UID from email
  	admin.auth().getUserByEmail( userEmail ).then(function( userRecord ){
  		// get the uid
  		uid = userRecord.uid;
  	}).then(function(){
  		// get the reference to sessions
  		var userRef = admin.database().ref('/Users').child( 'Patient' ).child(uid).child("sessions").child(sessionId);
  		userRef.remove();
  	}).then(function(){
  		// detete from sessions node
  		var sessionRef = admin.database().ref('/Sessions').child( sessionId );
  		sessionRef.remove();
  	}).then(function(){
  		// success
  		console.log("Success, session deleted!");
  		res.status(200).send({status:"Success, Session Deleted!"});
  		return;
  	}).catch(function(error){
  		console.log("couldn't delete session for some reason");
  		res.status(500).send({status:error.message});
  		return;
  	});
});


/* use the app at the new endpoint root '/' */
exports.app = functions.https.onRequest(app);

