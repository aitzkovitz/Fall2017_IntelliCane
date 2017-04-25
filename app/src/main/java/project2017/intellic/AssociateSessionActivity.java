package project2017.intellic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssociateSessionActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private Spinner spinnerPatient;
    private Spinner spinnerSession;
    private Map<String,String> patientIDs = new HashMap<String,String>();

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
                Intent intent = new Intent(AssociateSessionActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_associate_session);

        // Reference patient list under current Therapist user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference patientRef = ref.child("Users").child("Patient");
        DatabaseReference sessionRef = ref.child("Sessions");

        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> patientNode = (Map<String,Object>)dataSnapshot.getValue();
                ArrayList<String> patients = new ArrayList<String>();
                for (String key : patientNode.keySet()) {
                    // Use PatientID to get LastName, FirstName
                    Map<String,Object> patient = (Map<String,Object>)patientNode.get(key);
                    String patientName = patient.get("lname").toString()
                            + ", "
                            + patient.get("fname").toString();
                    patientIDs.put(patientName,key);
                    patients.add(patientName);
                }

                spinnerPatient = (Spinner) findViewById(R.id.spinnerPatient);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        AssociateSessionActivity.this,
                        android.R.layout.simple_spinner_item,
                        patients);
                spinnerPatient.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> sessionNode = (Map<String,Object>)dataSnapshot.getValue();
                ArrayList<String> sessions = new ArrayList<String>();
                for (String key : sessionNode.keySet()) {
                    sessions.add(key);
                }

                spinnerSession = (Spinner)findViewById(R.id.spinnerSession);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        AssociateSessionActivity.this,
                        android.R.layout.simple_spinner_item,
                        sessions);
                spinnerSession.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    protected void createAssociation(View view) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");

        Spinner spinnerPatient = (Spinner)findViewById(R.id.spinnerPatient);
        String patientName = spinnerPatient.getSelectedItem().toString();
        Spinner spinnerSession = (Spinner)findViewById(R.id.spinnerSession);

        final String sessionName = spinnerSession.getSelectedItem().toString();
        final String patientID = patientIDs.get(patientName);

        final DatabaseReference patientRef = ref.child("Users").child("Patient");
        patientRef.child(patientID).child("sessions").child(sessionName).setValue(true);

        Toast.makeText(AssociateSessionActivity.this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AssociateSessionActivity.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }
}
