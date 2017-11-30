package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aaronitzkovitz on 10/28/17.
 */

// therapists are directed here to create their own patients
public class NewPatientActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    String uid = "";
    private GoogleApiClient client;
    EditText editTextEmail;
    EditText editTextFName;
    EditText editTextLName;
    EditText editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get text views
        editTextEmail = (EditText) findViewById(R.id.newPatientEmail );
        editTextFName = (EditText) findViewById(R.id.newPatientFname );
        editTextLName = (EditText) findViewById(R.id.newPatientLname );
        editTextPhone = (EditText) findViewById(R.id.newPatientPhone ); // not required

        // add validators
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText) view;
                String text = textBox.getText().toString();
                if (!b){
                    if (text.length() == 0){
                        textBox.setError("Email can't be empty!");
                    } else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            textBox.setError("Must be a valid email!");
                        }
                    }
                }
            }
        });

        editTextFName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText) view;
                String text = textBox.getText().toString();
                if (!b){
                    if (text.length() == 0){
                        textBox.setError("First name can't be empty!");
                    } else {
                        Pattern p = Pattern.compile("^[A-Za-z]*");
                        Matcher m = p.matcher(text);
                        if (!m.matches()){
                            textBox.setError("Must be a valid first name!");
                        }
                    }
                }
            }
        });

        editTextLName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText) view;
                String text = textBox.getText().toString();
                if (!b){
                    if (text.length() == 0){
                        textBox.setError("Last name can't be empty!");
                    } else {
                        Pattern p = Pattern.compile("^[A-Za-z]*");
                        Matcher m = p.matcher(text);
                        if (!m.matches()){
                            textBox.setError("Must be a valid last name!");
                        }
                    }
                }
            }
        });

        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText) view;
                String text = textBox.getText().toString();
                if (!b && text.length() != 0 && !Patterns.PHONE.matcher(text).matches()){
                    textBox.setError("Must be a valid phone number!");
                }
            }
        });

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
                Intent intent = new Intent(NewPatientActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return true;
    }

    // called on click event
    public void createNewPatientRequest( View view ) {
        RadioGroup radioGroup;
        RadioButton radioButton;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final String email = editTextEmail.getText().toString();
        final String fname = editTextFName.getText().toString();
        final String lname = editTextLName.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String uid = mAuth.getCurrentUser().getUid();

        // TBI: use cookies instead of requesting a token to send each time
        FirebaseUser currUser = mAuth.getCurrentUser();
        currUser.getIdToken(true)
                .addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {

                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                // make callback for task completion
                                OnTaskCompleted listener = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(JSONObject res, int code) {
                                        try {
                                            Log.v("LISTENER", res.toString());
                                            if (code == 200) {
                                                Toast.makeText(NewPatientActivity.this, "User creation successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewPatientActivity.this, TherapistActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(NewPatientActivity.this, res.getString("status"), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewPatientActivity.this, TherapistActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }catch(Exception e){
                                            Log.v("RESP HANDLER", e.toString());
                                        }
                                    }
                                };

                                // get token from promise
                                String tok = task.getResult().getToken();

                                // pass listener in request constructor
                                AdminRequest adminRequest = new AdminRequest( listener, NewPatientActivity.this );
                                adminRequest.addPost(
                                        new Pair<String, String>("email", email),
                                        new Pair<String, String>("fname", fname),
                                        new Pair<String, String>("lname", lname),
                                        new Pair<String, String>("uid", uid),
                                        new Pair<String, String>("phone", phone)
                                );
                                adminRequest.addToken(tok);
                                adminRequest.execute("addPatient");

                            } catch(Exception e){
                                Log.v("AMI", e.toString());
                            }

                        }
                    }
                });
    }

}
