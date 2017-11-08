
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

import org.json.JSONObject;

public class NewUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String uid = "";
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
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

        EditText editTextEmail = (EditText) findViewById(R.id.newUserEmail );
        EditText editTextFName = (EditText) findViewById(R.id.newUserfName );
        EditText editTextLName = (EditText) findViewById(R.id.newUserlName );
        EditText editTextPhone = (EditText) findViewById(R.id.newUserPhone );
        EditText editTextPhotoURL = (EditText) findViewById(R.id.newUserPhotoURL );
        EditText editTextDisplayName = (EditText) findViewById(R.id.newUserDisplayName );
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
                                        Log.v("LISTENER", res.toString());
                                        if (code == 200){
                                            Toast.makeText(NewUserActivity.this, "User creation successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(NewUserActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
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
