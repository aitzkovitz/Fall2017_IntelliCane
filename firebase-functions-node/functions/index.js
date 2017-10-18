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
app.use(validateFirebaseIdToken);

/////////////////// ADD NEW USER TO AUTH AND DB ///////////////////
app.post('/admin/addUser', (req, res) => {
  	// grab original body
  	const original = req.body;

  	// parse json body request for user info
  	var fname = req.body.fname;
  	var lname = req.body.lname;
  	var email = req.body.email;
  	var role = req.body.role;
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
    		res.status(200).send('Request Succeeded');
  		});
	}).catch(function(error) {
		console.log("Error creating new user:", error);
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
	admin.auth().deleteUser(uid).then( function( userRecord ){
		console.log("Successfully deleted user from auth: ", uid);
		// now we need to delete the user and associated date from DB
		if (role == "Therapist"){
			// do any additionaly db cleanup that needs to be done
		} else {
			// this is a patient so we need to clean up the sessions also
			var ref = admin.database().ref('/Users/Patient').child(uid).child("Sessions");
			ref.once("value", function(data) {
				// we need to check if this is empty
  				if (null == data.val()){
  					console.log("No extra session data to delete for patient.");
  					res.status(200).send( "delete patient operation done." );
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
  							continue;
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
		}
	}).catch(function(error) {
		console.log("Couldn't delete user for some reason: ", error);
	});
});

///////////////// EDIT USER/DELETE USER DATA //////////////////
app.post('/admin/editUser', (req, res) => {
  	// grab original body
  	const original = req.body;
  	
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

  	// make changes to this user
  	// ...

});


/////////////// GET USER INFO //////////////////////////
app.get('/admin/getUser/:id', (req, res) => {

	// id will be the uid of the person we want
	var uid = req.params.id
	


});

// use the app at the new endpoint root '/'
exports.app = functions.https.onRequest(app);