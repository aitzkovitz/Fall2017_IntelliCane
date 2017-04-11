package project2017.intellic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SessionDataActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_data);

        // Grab patientID from Intent
        String sid = getIntent().getStringExtra("SESSION_ID");

        // Reference session list under selected Patient user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference sessionRef = ref.child("Sessions").child(sid);

        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create object to store list of sessions
                //Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                //ArrayList<String> keys = new ArrayList<String>();
                //for (String key : map.keySet()) {
                //    keys.add(key);
                //}

                // Do something with data
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
