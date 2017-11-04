package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
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

public class EditUserActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
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
                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

/*
    public void onTaskCompleted(JSONObject res){
        if (res.length() == 0){
            Toast.makeText(EditUserActivity.this, "This user has no data.", Toast.LENGTH_SHORT).show();
        }
        else{
            // get a user object from the response
            User returnedUser = new User(res);

            // make intent and add the user data to it's bundle
            Intent intent = new Intent(EditUserActivity.this, UpdateUserActivity.class);
            Bundle extraInfo = new Bundle();
            extraInfo.putParcelable("userData", returnedUser);
            intent.putExtras(extraInfo);
            startActivity(intent);
            finish();
        }
    }*/

    // on button click listener
    public void editUserRequest(View view){

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
                    String tok = getTokenResult.getToken();
                    JSONObject response;
                    int code;
                    OnTaskCompleted listener = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(JSONObject res) {
                            Log.v("LISTENER", res.toString());
                            if (res.length() == 0){
                                Toast.makeText(EditUserActivity.this, "This user has no data.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // get a user object from the response
                                User returnedUser = new User(res);

                                // make intent and add the user data to it's bundle
                                Intent intent = new Intent(EditUserActivity.this, UpdateUserActivity.class);
                                Bundle extraInfo = new Bundle();
                                extraInfo.putParcelable("userData", returnedUser);
                                intent.putExtras(extraInfo);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                    AdminRequest adminInfoRequest = new AdminRequest(listener);
                    // add data to send
                    adminInfoRequest.addPost(
                            new Pair<String, String>("email", email)
                    );
                    adminInfoRequest.addToken(tok);
                    adminInfoRequest.execute("getUser");


                    // get the user info sent back and pass to next intent
                    response = adminInfoRequest.getResponseBody();
                    code = adminInfoRequest.getResponseCode();
/*
                    if (code == 200){
                        if (response.length() == 0){
                            Toast.makeText(EditUserActivity.this, "This user has no data.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // get a user object from the response
                            User returnedUser = new User(response);



                            // make intent and add the user data to it's bundle
                            Intent intent = new Intent(EditUserActivity.this, UpdateUserActivity.class);
                            Bundle extraInfo = new Bundle();
                            extraInfo.putParcelable("userData", returnedUser);
                            intent.putExtras(extraInfo);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        throw new Exception("Server sent bad response");
                    }

*/
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
