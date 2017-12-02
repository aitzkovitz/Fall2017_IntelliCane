package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aaronitzkovitz on 10/18/17.
 */

/*
This class is used to select users to edit.
 */
public class EditUserActivity extends AppCompatActivity{

    private EditText editTextEmailtoEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextEmailtoEdit = (EditText) findViewById(R.id.EditUserEmail);
        editTextEmailtoEdit.addTextChangedListener(new TextValidator(editTextEmailtoEdit) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0){
                    textView.setError("Email must not be empty!");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                        textView.setError("Input must be valid email!");
                    }
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
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
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


    // on button click listener
    public void editUserRequest(View view){

        // define listener for when the operation completes
        final OnTaskCompleted listener = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject res, int code) {
                Log.v("LISTENER", res.toString());
                if (code != 200){
                    Log.v("RES", "bad response");
                }
                else{
                    // get a user object from the response
                    User returnedUser = new User(res);

                    // make intent and add the user data to it's bundle
                    Intent intent = new Intent(EditUserActivity.this, UpdateUserActivity.class);
                    Bundle extraInfo = new Bundle();
                    extraInfo.putParcelable( "userData" , returnedUser );
                    intent.putExtras(extraInfo);
                    startActivity(intent);
                    //finish();
                }
            }
        };

        // get info of user to delete
        final String email = editTextEmailtoEdit.getText().toString();

        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();

        // TBI: add to manifest
        // TBI: use cookies so we don't have to get token every time
        // TBI: in this activity, possibly lookup patients by therapist
        currUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                // we got the token
                try {
                    // get token from promise
                    String tok = getTokenResult.getToken();
                    JSONObject response;
                    int code;

                    // pass complete listener into constructor
                    AdminRequest adminInfoRequest = new AdminRequest( listener, EditUserActivity.this );
                    // add data to send
                    adminInfoRequest.addPost(
                            new Pair<String, String>("email", email)
                    );
                    adminInfoRequest.addToken( tok );
                    adminInfoRequest.execute( "getUser" );

                } catch(Exception e){
                    Log.v("AMI", e.toString());
                }

            }
        }).addOnFailureListener( new OnFailureListener() {
            public void onFailure(Exception getTokenException){
                return;
            }
        });

    }

}
