package project2017.intellic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
//                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                ArrayList<Date> times = new ArrayList<Date>();
//                for (String key : map.keySet()) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
//                    try {
//                        Date time = sdf.parse(key);
//                        times.add(time);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Collections.sort(times, new Comparator<Date>() {
//                    @Override
//                    public int compare(Date o1, Date o2) {
//                        return o1.compareTo(o2);
//                    }
//                });
//
//                for (Date time : times) {
//                    Log.v("E_VALUE", "Time : " + time.toString());
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
