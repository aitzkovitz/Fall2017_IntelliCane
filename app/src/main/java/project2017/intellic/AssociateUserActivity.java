package project2017.intellic;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

/*
This class is used by admins to associate patients with therapists.
 */
public class AssociateUserActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private Spinner spinnerTherapist;
    private Spinner spinnerPatient;
    private Map<String,String> therapistIDs = new HashMap<String,String>();
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
                Intent intent = new Intent(AssociateUserActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_associate_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Reference patient list under current Therapist user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference therapistRef = ref.child("Users").child("Therapist");
        DatabaseReference patientRef = ref.child("Users").child("Patient");

        therapistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create object to hold list of therapists
                Map<String,Object> therapistNode = (Map<String,Object>)dataSnapshot.getValue();
                ArrayList<String> therapists = new ArrayList<String>();
                for (String key : therapistNode.keySet()) {
                    // Use TherapistID to get LastName, FirstName
                    Map<String,Object> therapist = (Map<String,Object>)therapistNode.get(key);
                    String therapistName = therapist.get("lname").toString()
                            + ", "
                            + therapist.get("fname").toString();
                    therapistIDs.put(therapistName,key);
                    therapists.add(therapistName);
                }

                // Populate ListView with list of patients
                spinnerTherapist = (Spinner) findViewById(R.id.spinnerTherapist);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        AssociateUserActivity.this,
                        android.R.layout.simple_spinner_item,
                        therapists);
                spinnerTherapist.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

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
                        AssociateUserActivity.this,
                        android.R.layout.simple_spinner_item,
                        patients);
                spinnerPatient.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    protected void createAssociation(View view) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");

        Spinner spinnerTherapist = (Spinner)findViewById(R.id.spinnerTherapist);
        String therapistName = spinnerTherapist.getSelectedItem().toString();
        Spinner spinnerPatient = (Spinner)findViewById(R.id.spinnerPatient);
        String patientName = spinnerPatient.getSelectedItem().toString();

        final String therapistID = therapistIDs.get(therapistName);
        final String patientID = patientIDs.get(patientName);

        final DatabaseReference therapistRef = ref.child("Users").child("Therapist").child(therapistID).child("patients");
        final DatabaseReference patientRef = ref.child("Users").child("Patient");

        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> patientNode = (Map<String,Object>)dataSnapshot.getValue();
                Map<String,Object> patient = (Map<String,Object>)patientNode.get(patientID);
                therapistRef.child(patientID).child("fname").setValue(patient.get("fname"));
                therapistRef.child(patientID).child("lname").setValue(patient.get("lname"));
                therapistRef.child(patientID).child("email").setValue(patient.get("email"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Toast.makeText(AssociateUserActivity.this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AssociateUserActivity.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }
}
