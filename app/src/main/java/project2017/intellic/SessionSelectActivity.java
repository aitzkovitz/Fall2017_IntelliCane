package project2017.intellic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SessionSelectActivity extends AppCompatActivity {

    private ListView sessionListView;
    private FirebaseDatabase database;

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
