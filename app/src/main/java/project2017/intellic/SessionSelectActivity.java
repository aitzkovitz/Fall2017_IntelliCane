package project2017.intellic;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
/*
This class is used by therapists to select a certain patients session data.
 */
public class SessionSelectActivity extends AppCompatActivity {

    private ListView sessionListView;
    private FirebaseDatabase database;

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
                Intent intent = new Intent(SessionSelectActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_session_select);

        // Grab patientID from Intent
        String pid = getIntent().getStringExtra("PATIENT_ID");

        // Reference session list under selected Patient user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference sessionRef = ref.child("Users").child("Patient").child(pid).child("sessions");

        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Create object to store list of sessions
                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();

                if (map == null){
                    Toast.makeText(SessionSelectActivity.this, "No Session Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> keys = new ArrayList<String>();
                for (String key : map.keySet()) {
                    keys.add(key);
                }

                // Populate ListView with list of sessions
                sessionListView = (ListView) findViewById(R.id.sessionListView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        SessionSelectActivity.this,
                        android.R.layout.simple_list_item_1,
                        keys);
                sessionListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Listen for item click in ListView
        sessionListView = (ListView) findViewById(R.id.sessionListView);
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Grab selected sessionID and pass along with the Intent for next Activity
                String sessionID = (String) sessionListView.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), SessionDataActivity.class);
                intent.putExtra("SESSION_ID", sessionID);
                startActivity(intent);
            }
        });
    }
}
