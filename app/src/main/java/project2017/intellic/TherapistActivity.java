package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by aaronitzkovitz on 10/28/17.
 */

public class TherapistActivity extends AppCompatActivity {
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
                Intent intent = new Intent(TherapistActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist);
    }


    // 2 activities for 2 different therapist options
    //-------------------
    // add new user
    public void newUser(View view) {
        Intent intent = new Intent(TherapistActivity.this, NewPatientActivity.class);
        startActivity(intent);
        finish();
    }

    // delete user
    public void deleteUser(View view) {
        Intent intent = new Intent(TherapistActivity.this, PatientSelectActivity.class);
        startActivity(intent);
        finish();
    }

}
