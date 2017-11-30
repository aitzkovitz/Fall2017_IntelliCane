
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String uid = "";
    private GoogleApiClient client;
    private EditText editTextEmail;
    private EditText editTextFName;
    private EditText editTextLName;
    private EditText editTextPhone;
    private EditText editTextPhotoURL;
    private EditText editTextDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextEmail = (EditText) findViewById(R.id.newUserEmail );
        editTextFName = (EditText) findViewById(R.id.newUserfName );
        editTextLName = (EditText) findViewById(R.id.newUserlName );
        editTextPhone = (EditText) findViewById(R.id.newUserPhone );
        editTextPhotoURL = (EditText) findViewById(R.id.newUserPhotoURL );
        editTextDisplayName = (EditText) findViewById(R.id.newUserDisplayName );

        // define validators
        View.OnFocusChangeListener textField = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText)view;
                String text = textBox.getText().toString();
                if (!b){
                    if (text.length() == 0){
                        textBox.setError("Can't be empty!");
                    } else {
                        Pattern p = Pattern.compile("^[A-Za-z]*");
                        Matcher m = p.matcher(text);
                        if (!m.matches()){
                            textBox.setError("Must be letters!");
                        }
                    }
                }
            }
        };

        // define validators for email
        View.OnFocusChangeListener emailField = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText textBox = (EditText)view;
                String text = textBox.getText().toString();
                if (!b){
                    if(text.length() == 0){
                        textBox.setError("Email can't be empty!");
                    }else {
                        if(!Patterns.EMAIL_ADDRESS.matcher(((EditText) view).getText()).matches()){
                            textBox.setError("Input must be valid email!");
                        }
                    }
                }
            }
        };

        // define validator for
        View.OnFocusChangeListener phoneField = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // if textview lost focus, or is empty
                if (b || ((EditText)view).getText().length() == 0){
                    return;
                } else {
                    // else check if it matches
                    if (!Patterns.PHONE.matcher(((EditText) view).getText()).matches()) {
                        ((EditText) view).setError("Must be a valid phone number!");
                    }
                }
            }
        };

        // define validator for URL
        View.OnFocusChangeListener URLField = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // if textview lost focus, or is empty
                if (b || ((EditText)view).getText().length() == 0){
                    return;
                } else {
                    // else check if it matches
                    if (!Patterns.WEB_URL.matcher(((EditText) view).getText()).matches()) {
                        ((EditText) view).setError("Must be a valid URL!");
                    }
                }
            }
        };

        // define validator for DisplayName
        View.OnFocusChangeListener displayNameField = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // if textview lost focus, or is empty
                if (b || ((EditText)view).getText().length() == 0){
                    return;
                } else {
                    Pattern p = Pattern.compile("^[A-Za-z0-9]*");
                    Matcher m = p.matcher(((EditText)view).getText());
                    if (!m.matches()){
                        ((EditText) view).setError("Must be letters or numbers!");
                    }
                }
            }
        };

        // add listeners
        editTextEmail.setOnFocusChangeListener(emailField);
        editTextFName.setOnFocusChangeListener(textField);
        editTextLName.setOnFocusChangeListener(textField);
        editTextPhone.setOnFocusChangeListener(phoneField);
        editTextPhotoURL.setOnFocusChangeListener(URLField);
        editTextPhotoURL.setOnFocusChangeListener(URLField);
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return true;
    }

    // called on click event
    public void createNewUserRequest( View view ) {
        RadioGroup radioGroup;
        RadioButton radioButton;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        radioGroup = (RadioGroup) findViewById(R.id.newUserRoleRadio );

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String email = editTextEmail.getText().toString();
        final String fname = editTextFName.getText().toString();
        final String lname = editTextLName.getText().toString();
        final String photourl = editTextPhotoURL.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String displayname = editTextDisplayName.getText().toString();
        final String role = radioButton.getText().toString();

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
                                                Toast.makeText(NewUserActivity.this, "User creation successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewUserActivity.this, AdminActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(NewUserActivity.this, res.getString("status"), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewUserActivity.this, NewUserActivity.class);
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
                                AdminRequest adminRequest = new AdminRequest( listener, NewUserActivity.this );
                                adminRequest.addPost(
                                        new Pair<String, String>("role", role),
                                        new Pair<String, String>("email", email),
                                        new Pair<String, String>("fname", fname),
                                        new Pair<String, String>("lname", lname),
                                        new Pair<String, String>("photoURL", photourl),
                                        new Pair<String, String>("phone", phone),
                                        new Pair<String, String>("displayName", displayname)
                                );
                                adminRequest.addToken(tok);
                                adminRequest.execute("addUser");

                            } catch(Exception e){
                                Log.v("AMI", e.toString());
                            }

                        }
                    }
                });
    }

}
