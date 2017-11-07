package project2017.intellic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import bolts.Task;

/**
 * Created by aaronitzkovitz on 10/14/17.
 */

public class NewUserSignupActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFName;
    private EditText editTextLName;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button buttonSignUP;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseUser newUser;
    private FirebaseDatabase database;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_logout was selected
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(NewUserSignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);
    }

    public void userSignup(View view) {
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.signUpUserEmail);
        editTextPassword = (EditText) findViewById(R.id.signUpUserPassword);
        editTextFName = (EditText) findViewById(R.id.signUpUserFname);
        editTextLName = (EditText) findViewById(R.id.signUpUserLname);
        radioGroup = (RadioGroup) findViewById(R.id.signUpUserRoleRadio);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String fname = editTextFName.getText().toString();
        final String lname = editTextLName.getText().toString();
        final String role = radioButton.getText().toString();

        // add to auth and log new user in
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            try {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String uid = user.getUid();
                                addNewUser(uid, fname, lname, email, role);
                                if (role.equals("Physical Therapist")) {
                                    Intent intent = new Intent(NewUserSignupActivity.this, PatientSelectActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                // invokes Patient activity
                                else if (role.equals("Patient")) {
                                    Intent intent = new Intent(NewUserSignupActivity.this, SessionSelectActivity.class);
                                    intent.putExtra("PATIENT_ID", uid);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(NewUserSignupActivity.this, "role is not assigned", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e){
                                Log.v("NEWUSERSIGNUP", e.toString());
                            }

                        }
                    }
                });
    }

    private void addNewUser(String uid, String fName, String lName, String email, String role){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users");
        DatabaseReference roleRef = database.getReference("Roles");

        //writing data into the database
        userRef.child(role).child(uid).child("email").setValue(email);
        userRef.child(role).child(uid).child("fname").setValue(fName);
        userRef.child(role).child(uid).child("lname").setValue(lName);
        if (role == "Therapist") {
            userRef.child(role).child(uid).child("patients").setValue(true);
        }
        if (role == "Patient") {
            userRef.child(role).child(uid).child("sessions").setValue(true);
        }
        roleRef.child(uid).setValue(role);
    }
}