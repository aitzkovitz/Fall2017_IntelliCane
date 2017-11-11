package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

/**
 * Created by aaronitzkovitz on 10/28/17.
 */

public class DeleteDataActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
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
                Intent intent = new Intent(DeleteDataActivity.this, LoginActivity.class);
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

    public void deleteData(View view){
        // session
        // define listener for when the operation completes
        final OnTaskCompleted listener = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject res, int code) {
                Log.v("LISTENER", res.toString());
                if (code != 200){
                    return;
                }
                else{
                    Toast.makeText(DeleteDataActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    // make intent to go back to admin activity
                    Intent intent = new Intent(DeleteDataActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // get info of user to delete
        final EditText editTextEmailToDelete = (EditText) findViewById(R.id.EditUserEmail);
        final String email = editTextEmailToDelete.getText().toString();

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
                    AdminRequest adminInfoRequest = new AdminRequest( listener, DeleteDataActivity.this );
                    // add data to send
                    adminInfoRequest.addPost(
                            new Pair<String, String>("email", email)
                    );
                    adminInfoRequest.addToken(tok);
                    adminInfoRequest.execute("deleteUser");


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
