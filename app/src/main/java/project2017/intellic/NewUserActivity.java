
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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

    // Navigates back to .AdminActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // allows data to be cached and availbale locally when internet is lost
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        //writing data into the database
        myRef.child(role).child(uid).child("email").setValue(email);
        myRef.child(role).child(uid).child("fname").setValue(fName);
        myRef.child(role).child(uid).child("lname").setValue(lName);
        //myRef.child(uid).child("role").setValue(role);
    }
}
