const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const express = require('express');
const cookieParser = require('cookie-parser')();
const cors = require('cors')({origin: true});
const app = express();



// main validation function to be called last in middleware
const validateFirebaseIdToken = (req, res, next) => {
	console.log('Check if request is authorized with Firebase ID token');
	
	if ((!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ')) &&
		!req.cookies.__session) {
		console.error('No Firebase ID token was passed as a Bearer token in the Authorization header.' );
		res.status(403).send('Unauthorized');
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
				res.status(403).send('Unauthorized');
				return;
  			}
		}, function( errObj ){
			console.log( "Couldn't retrieve the user's role for verification. ");
			res.status(403).send('Unauthorized');
			return;
		});

    	console.log("Successfully verified");
    	res.status(200).send('Request Succeeded');
    	next();
  	}).catch(error => {
		console.error('Error while verifying Firebase ID token:', error);
		res.status(403).send('Unauthorized');
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
  	var photoURL = req.body
  	var uid, field4;
  	

  	// add user to authentication
	admin.auth().createUser({
		email: email,
	  	emailVerified: false,
	  	// use Math.random().toString(36).substr(2, 8) eventually?
	  	password: "autoPassword",
	  	displayName: fname + ' ' + lname,
	  	disabled: false
	}).then( function( userRecord ){

		uid = userRecord.uid;
		console.log("Successfully created new auth user: ", uid );

		if ( role == "Patient" ){
			field4 = "Sessions";
		} else {
			field4 = "Patients";
		}
	  	// Push the new message into the Realtime Database using the Firebase Admin SDK.
  		admin.database().ref('/Users').child(role).child(uid).set({
			"fname": fname,
			"lname": lname,
			"email": email,
			[field4]: true
  		}).then(snapshot => {
    		// Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    		console.log("added to db");
    		res.status(200).send({status:"Successfully added user"});


    		// send that new password to the user
    		//


  		});
	}).catch(function(error) {
		console.log("Error creating new user:", error);
		res.status(500).send( "Internal Server Error" );
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
		
		// make sure this isn't an Admin
    	var ref = admin.database().ref('/Roles').child(uid);
		ref.once("value", function(data) {
  			role = data.val();
  			if ("Administrator" == role ){
  				console.error('Trying to delete an admin account');
				res.status(400).send("Can't delete this account.");
				return;
  			}
		}, function( errObj ){
			console.log('Error trying to get user role.', errObj);
			res.status(500).send('Error trying to get user role.');
			return;
		});
  	}).catch(function(error) {
    	console.log("Error retrieving user to delete.", error);
    	res.status(500).send('You cannot delete this account.');
    	return;
  	});

  	// Now we are authorized and know we can delete this account
	admin.auth().deleteUser("q2wktRWhvwfgLe2ndLZIwlq07U82").then( function(){
		console.log("Successfully deleted user from auth: ", uid);
		// now we need to delete the user and associated date from DB
		if (role == "Therapist"){
			console.log('poopoopeepee');
			var tRef = admin.database.ref('/Users/Therapist').child(uid);
			tRef.remove().then(function(){
				res.status(200).send('Tharapist deleted successful');
				console.log("Remove succeeded for Patient." + uid);
				return;
			}).catch( function(error) {
				console.log("Remove failed on Tharapist: " + uid + "  " + error );
				return;
			});
		} else {
			// this is a patient so we need to clean up the sessions also
			var ref = admin.database().ref('/Users/Patient').child(uid).child("Sessions");
			ref.once("value", function(data) {
				// we need to check if this is empty
  				if (null == data.val()){
  					console.log("No extra session data to delete for patient.");
  					res.status(200).send( "delete patient session operation done." );
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
  						}).catch(function(err){
  							// couldn't remove session from sessions node
  							console.log("couldn't delete a session");
  							//continue;
  						});
					});
					console.log("made it to the end of delete");
					res.status(200).send("Delete function done");
					return;
  				}
			}, function( errObj ){
				console.log('Error trying to get user role.', errObj);
				res.status(500).send('Error trying to get user role.');
				return;
			});
			ref.remove().then( function(){
				res.status(200).send('Patient deleted sucessful');
				console.log("Remove succeeded for Patient." + uid);
				return;
			}).catch( function(error) {
				console.log("Remove failed on Patient: ", uid + "  " + error);
				return;
			});
		}
	}).catch(function(error) {
		console.log("Couldn't delete user for some reason: ", error);
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
				res.status(400).send("Can't edit this account.");
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
  					res.status(500).send("Could not get user from DB.");
  				});
  			}
		}, function( errObj ){
			console.log('Error trying to get user role.', errObj);
			res.status(500).send('Error trying to get user role.');
			return;
		});
  	}).catch(function(error) {
    	console.log("Error retrieving user to delete.", error);
    	res.status(500).send('You cannot delete this account.');
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
		phoneNumber: '+447911123456',//newPhone,
		emailVerified: newVerified,
		displayName: newDisplayName,
		photoURL: 'https://static.pexels.com/photos/290263/pexels-photo-290263.jpeg',//newPhotoURL,
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
		res.status(500).send(error.toJSON());
		return;
	});

});

// use the app at the new endpoint root '/'
exports.app = functions.https.onRequest(app);
