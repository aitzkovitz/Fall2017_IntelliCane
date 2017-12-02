
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/*
This activity is shown when an admin logs in. Provides 6 different options.
 */
public class AdminActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }


    // 6 activities for 6 different admin options
    //-------------------
    // add new user
    public void newUser(View view) {
        Intent intent = new Intent(AdminActivity.this, NewUserActivity.class);
        startActivity(intent);
        //finish();
    }

    // delete user
    public void deleteUser(View view) {
        Intent intent = new Intent(AdminActivity.this, DeleteUserActivity.class);
        startActivity(intent);
        //finish();
    }

    // edit user
    // 1. Send HTTP request to get that users info, show it in a new activity
    // Admin can edit auth info:
    // - disabled, displayName, email, emailVerified, password, phoneNumber, photoURL
    // or database user info:
    // - fname, lname, any other info we want to store
    // 2. Edit that info then send it back in new intent
    public void editUser(View view) {
        Intent intent = new Intent(AdminActivity.this, EditUserActivity.class);
        startActivity(intent);
        //finish();
    }

    // delete session data
    //1. pick a patient
    //2. delete whichever of the sessions
    public void deleteData(View view) {
        Intent intent = new Intent(AdminActivity.this, DeleteDataActivity.class);
        startActivity(intent);
        //finish();
    }

    // assoicate patient and sessions
    public void associatePT(View View) {
        Intent intent = new Intent(AdminActivity.this, AssociateUserActivity.class);
        startActivity(intent);
        //finish();
    }

    // associate patient and therapist
    public void associatePS(View view) {
        Intent intent = new Intent(AdminActivity.this, AssociateSessionActivity.class);
        startActivity(intent);
        //finish();
    }
}

