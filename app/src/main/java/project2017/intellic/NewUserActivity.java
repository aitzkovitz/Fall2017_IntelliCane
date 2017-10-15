
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String uid = "";
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

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
                Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void createNewUserRequest( View view ) {
        RadioGroup radioGroup;
        RadioButton radioButton;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        EditText editTextEmail = (EditText) findViewById(R.id.newUserEmail);
        EditText editTextPassword = (EditText) findViewById(R.id.newUserPassword);
        EditText editTextFName = (EditText) findViewById(R.id.fName);
        EditText editTextLName = (EditText) findViewById(R.id.lName);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String fname = editTextFName.getText().toString();
        final String lname = editTextLName.getText().toString();
        final String role = radioButton.getText().toString();

        // TBI: remove password as this can be done on server more securely
        // once
        FirebaseUser currUser = mAuth.getCurrentUser();
        currUser.getIdToken(true)
                .addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {

                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                String tok = task.getResult().getToken();
                                // send the token as part of the request
                                AdminRequest adminRequest = new AdminRequest();
                                adminRequest.addPost(
                                        new Pair<String, String>("role", role),
                                        new Pair<String, String>("email", email),
                                        new Pair<String, String>("fname", fname),
                                        new Pair<String, String>("lname", lname));
                                adminRequest.addToken(tok);
                                adminRequest.execute("newUser");

                                Toast.makeText(NewUserActivity.this, "user creation successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NewUserActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();

                            } catch(Exception e){
                                Log.v("AMI", e.toString());
                            }
                        }
                    }
                });
    }

    /*
    public void createNewUser(View view) {
        RadioGroup radioGroup;
        RadioButton radioButton;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        EditText editTextEmail = (EditText) findViewById(R.id.newUserEmail);
        EditText editTextPassword = (EditText) findViewById(R.id.newUserPassword);
        EditText editTextFName = (EditText) findViewById(R.id.fName);
        EditText editTextLName = (EditText) findViewById(R.id.lName);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String fname = editTextFName.getText().toString();
        final String lname = editTextLName.getText().toString();
        final String role = radioButton.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(NewUserActivity.this, "user already exists",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            firebaseAuth = FirebaseAuth.getInstance();
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            uid = user.getUid();
                            addNewUser (uid, fname, lname, email, role);

                            Toast.makeText(NewUserActivity.this, "user creation successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewUserActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
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
    */

}
