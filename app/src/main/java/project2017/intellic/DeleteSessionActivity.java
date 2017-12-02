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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aaronitzkovitz on 11/11/17.
 */

/*
This class is used by admins to select and delete sessions.
 */
public class DeleteSessionActivity extends AppCompatActivity {

    private ListView sessionListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get session info from sender
        Bundle extraInfo = this.getIntent().getExtras();
        if ( extraInfo != null ){

            // get sessions
            ArrayList<String> sessions = extraInfo.getStringArrayList("SESSION_ARRAY");
            final String userEmail = extraInfo.getString("USER_EMAIL");

            // Populate ListView with list of sessions
            sessionListView = (ListView) findViewById(R.id.deleteSessionListView);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    DeleteSessionActivity.this,
                    android.R.layout.simple_list_item_1,
                    sessions);
            sessionListView.setAdapter(arrayAdapter);

            // add click listener
            sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // Grab selected sessionID and pass along with the Intent for next Activity
                    String sessionID = (String) sessionListView.getItemAtPosition(position);
                    deleteSelectedSession(sessionID, userEmail);
                }
            });

            // TBI: add "are you sure" dialogue box

        }else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(DeleteSessionActivity.this, LoginActivity.class);
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

    // send request to delete session
    private void deleteSelectedSession(final String sessionId, final String userEmail){
        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();

        // make response listener for call
        final OnTaskCompleted listener = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject res, int code) {
                try {
                    if (code != 200) {
                        Toast.makeText(DeleteSessionActivity.this, res.getString("status"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeleteSessionActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        Toast.makeText(DeleteSessionActivity.this, res.getString("status"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeleteSessionActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };


        // get token
        currUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                // we got the token
                try {
                    // get token from response
                    String tok = getTokenResult.getToken();
                    JSONObject response;
                    int code;

                    // pass complete listener into constructor
                    AdminRequest adminInfoRequest = new AdminRequest( listener, DeleteSessionActivity.this );
                    // add data to send
                    adminInfoRequest.addPost(
                            new Pair<String, String>("sessionId", sessionId),
                            new Pair<String, String>("userEmail", userEmail)
                    );
                    adminInfoRequest.addToken(tok);
                    adminInfoRequest.execute("deleteData");


                } catch(Exception e){
                    Log.v("AMI", e.toString());
                }
            }
        });
    }

}
