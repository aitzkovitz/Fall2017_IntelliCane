package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.HashMap;

/**
 * Created by aaronitzkovitz on 10/18/17.
 */


// TBI: add activity to manifest!
public class UpdateUserActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextPass;
    private EditText editTextPhotoURL;
    private EditText editTextDisplayName;
    private CheckBox checkboxDisabled;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // grab the fields and fill them with info from last activity
        // get editUser fields to populate with response info later
        editTextEmail = (EditText) findViewById(R.id.editUserEmail);
        editTextPhone = (EditText) findViewById(R.id.editUserPhone);
        editTextPass = (EditText) findViewById(R.id.editUserPassword);
        editTextPhotoURL = (EditText) findViewById(R.id.editUserPhotoURL);
        editTextDisplayName = (EditText) findViewById(R.id.editUserDisplayName);
        checkboxDisabled = (CheckBox) findViewById(R.id.editUserDisabled );

        // get user info from extra
        Bundle extraInfo = this.getIntent().getExtras();
        if ( extraInfo != null ){

            User userToUpdate = extraInfo.getParcelable("userData");

            editTextEmail.setText(userToUpdate.getEmail());
            editTextPhone.setText(userToUpdate.getPhone());
            editTextPass.setText(userToUpdate.getPassword());
            editTextPhotoURL.setText(userToUpdate.getPhotoURL());
            editTextDisplayName.setText(userToUpdate.getDisplayName());
            checkboxDisabled.setSelected(userToUpdate.isDisabled());

        }else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT)
                    .show();
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
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(UpdateUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    // send new info to server
    void updateUserRequest(View view){
        // get new info from text fields
        final String newEmail = editTextEmail.getText().toString();
        final String newPhone = editTextPhone.getText().toString();
        final String newPass = editTextPass.getText().toString();
        final String newPhotoURL = editTextPhotoURL.getText().toString();
        final String newDisplayName = editTextDisplayName.getText().toString();
        final boolean newDisabled = checkboxDisabled.isChecked();

        // TBI: use cookies instead of requesting a token to send each time
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        currUser.getIdToken(true)
                .addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {

                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                HashMap<String, Object> response;
                                String tok = task.getResult().getToken();
                                int code;
                                // send the token as part of the request
                                AdminRequest updateRequest = new AdminRequest();
                                updateRequest.addPost(
                                        new Pair<String, String>("newEmail", newEmail),
                                        new Pair<String, String>("newPhone", newPhone),
                                        new Pair<String, String>("newPass", newPass),
                                        new Pair<String, String>("newPhotoURL", newPhotoURL),
                                        new Pair<String, String>("newDisplayName", newDisplayName),
                                        new Pair<String, Boolean>("newDisabled", newDisabled));
                                updateRequest.addToken(tok);
                                updateRequest.execute("updateUser");

                                response = updateRequest.getResponseBody();
                                code = updateRequest.getResponseCode();

                                if (code == 200 ){
                                    Toast.makeText(UpdateUserActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(UpdateUserActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    Log.v("UPDATE RESPONSE", code + response.toString());
                                }

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
