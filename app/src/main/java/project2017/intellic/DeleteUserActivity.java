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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import bolts.Task;

/**
 * Created by aaronitzkovitz on 10/17/17.
 */

// TBI: add to manifest!
// used to delete users
public class DeleteUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                Intent intent = new Intent(DeleteUserActivity.this, LoginActivity.class);
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

    // TBI: use cookies instead of requesting a token to send each time?
    public void deleteUserRequest(View view) {

        // define listener for when the operation completes
        final OnTaskCompleted listener = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject res, int code) {
                try {
                    Log.v("LISTENER", res.toString());
                    if (code != 200) {
                        Toast.makeText(DeleteUserActivity.this, res.getString("status"), Toast.LENGTH_SHORT).show();
                        return;

                    } else {
                        // go back to adminActivity if success
                        Toast.makeText(DeleteUserActivity.this, "Successfully Deleted User", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeleteUserActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        // get info of user to delete
        EditText editTextEmail = (EditText) findViewById(R.id.deleteUserEmail);
        final String email = editTextEmail.getText().toString();

        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();

        // if a patient is deleted, all the associated sessions must be deleted in DB
        // TBI: in this activity, possibly lookup patients by therapist
        currUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                // we got the token
                try {
                    String tok = getTokenResult.getToken();
                    // send the token as part of the request
                    AdminRequest adminRequest = new AdminRequest( listener, DeleteUserActivity.this );
                    adminRequest.addPost(
                            new Pair<String, String>("email", email)
                    );
                    adminRequest.addToken(tok);
                    adminRequest.execute("deleteUser");

                    Toast.makeText(DeleteUserActivity.this, "user creation successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteUserActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();

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
