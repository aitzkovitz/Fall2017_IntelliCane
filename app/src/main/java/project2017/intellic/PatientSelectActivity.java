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

import static project2017.intellic.R.id.patient;

public class PatientSelectActivity extends AppCompatActivity {

    private ListView patientListView;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_select);

        // Get current user to determine what list of patients they
        // are allowed to see
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        // Reference patient list under current Therapist user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference patientRef = ref.child("Users").child("Therapist").child(uid).child("patients");

        // Listen to track changes in data at database reference
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create object to hold list of patients
                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                ArrayList<String> keys = new ArrayList<String>();
                for (String key : map.keySet()) {
                    keys.add(key);
                }

                // Populate ListView with list of patients
                patientListView = (ListView) findViewById(R.id.patientListView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        PatientSelectActivity.this,
                        android.R.layout.simple_list_item_1,
                        keys);
                patientListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Listen for item click in ListView
        patientListView = (ListView) findViewById(R.id.patientListView);
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Grab selected patientID and pass along with the Intent for next Activity
                String patientID = (String) patientListView.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), SessionSelectActivity.class);
                intent.putExtra("PATIENT_ID", patientID);
                startActivity(intent);
            }
        });
    }
}
