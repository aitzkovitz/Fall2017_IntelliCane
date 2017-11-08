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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aaronitzkovitz on 10/18/17.
 */


// TBI: add activity to manifest!
public class UpdateUserActivity extends AppCompatActivity {

    // auth info
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextPhotoURL;
    private EditText editTextDisplayName;
    private CheckBox checkboxDisabled;
    private CheckBox checkboxEmailVerified;

    // database userinfo
    private EditText editTextFname;
    private EditText editTextLname;

    private FirebaseAuth mAuth;
    private String uid;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // grab the fields and fill them with info from last activity
        // get auth info fields to populate with response info later
        editTextEmail = (EditText) findViewById(R.id.editUserEmail);
        editTextPhone = (EditText) findViewById(R.id.editUserPhone);
        editTextPhotoURL = (EditText) findViewById(R.id.editUserPhotoURL);
        editTextDisplayName = (EditText) findViewById(R.id.editUserDisplayName);
        checkboxDisabled = (CheckBox) findViewById(R.id.editUserDisabled );
        checkboxEmailVerified = (CheckBox) findViewById(R.id.editUserVerified);

        // get database userinfo fields to populate with response info later
        editTextFname = (EditText) findViewById(R.id.editUserFname);
        editTextLname = (EditText) findViewById(R.id.editUserLname);

        // get user info from extra
        Bundle extraInfo = this.getIntent().getExtras();
        if ( extraInfo != null ){

            User userToUpdate = extraInfo.getParcelable("userData");

            uid = userToUpdate.getUid();
            role = userToUpdate.getRole();
            editTextFname.setText(userToUpdate.getFname());
            editTextLname.setText(userToUpdate.getLname());
            editTextEmail.setText(userToUpdate.getEmail());
            editTextPhone.setText(userToUpdate.getPhone());
            editTextPhotoURL.setText(userToUpdate.getPhotoURL());
            editTextDisplayName.setText(userToUpdate.getDisplayName());
            checkboxDisabled.setSelected(userToUpdate.isDisabled());
            checkboxEmailVerified.setSelected(userToUpdate.isEmailVerified());

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
                Intent intent = new Intent(UpdateUserActivity.this, LoginActivity.class);
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

    // send new info to server
    void updateUserRequest(View view){
        // get new info from text fields
        final String newFname = editTextFname.getText().toString();
        final String newLname = editTextLname.getText().toString();
        final String newEmail = editTextEmail.getText().toString();
        final String newPhone = editTextPhone.getText().toString();
        final String newPhotoURL = editTextPhotoURL.getText().toString();
        final String newDisplayName = editTextDisplayName.getText().toString();
        final boolean newDisabled = checkboxDisabled.isChecked();
        final boolean newVerified = checkboxEmailVerified.isChecked();

        // TBI: use cookies instead of requesting a token to send each time
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        currUser.getIdToken(true)
                .addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {

                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                JSONObject response;
                                int code;

                                // make completion listener callback
                                OnTaskCompleted listener = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(JSONObject res, int code) {
                                        Log.v("LISTENER", res.toString());
                                        if (code == 200){
                                            Toast.makeText(UpdateUserActivity.this, "User update successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UpdateUserActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                };

                                String tok = task.getResult().getToken();
                                // construct update request
                                AdminRequest updateRequest = new AdminRequest( listener, UpdateUserActivity.this );
                                // add post data to request
                                updateRequest.addPost(
                                        new Pair<String, String>("uid", uid),
                                        new Pair<String, String>("role", role),
                                        new Pair<String, String>("newFname", newFname),
                                        new Pair<String, String>("newLname", newLname),
                                        new Pair<String, String>("newEmail", newEmail),
                                        new Pair<String, String>("newPhone", newPhone),
                                        new Pair<String, String>("newPhotoURL", newPhotoURL),
                                        new Pair<String, String>("newDisplayName", newDisplayName),
                                        new Pair<String, Boolean>("newDisabled", newDisabled),
                                        new Pair<String, Boolean>("newVerified", newVerified)
                                );
                                // add token to request
                                updateRequest.addToken(tok);
                                // send request
                                updateRequest.execute("updateUser");

                                // get response data
                                response = updateRequest.getResponseBody();
                                code = updateRequest.getResponseCode();

                                Intent intent = new Intent(UpdateUserActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();

                            } catch(Exception e){
                                Log.v("AMI", e.toString());
                            }

                        }
                    }
                });
    }

}
