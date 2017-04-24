package project2017.intellic;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.ValueDependentColor;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class SessionDataActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    double HAx, HAy, HAz, BAx, BAy, BAz, Gx, Gy, Gz, f0, f1, f2, f3, f4, f5, f6, f7;

    List<Double> HA = new ArrayList<Double>();
    List<Double> BA = new ArrayList<Double>();
    List<Double> G = new ArrayList<Double>();
    List<Double> f = new ArrayList<Double>();
    List<Double> Gt = new ArrayList<Double>();
    List<Double> HAt = new ArrayList<Double>();
    List<Double> BAt= new ArrayList<Double>();
    List<Double> T = new ArrayList<Double>();
    List<Integer> V_US = new ArrayList<Integer>();
    List<Double> V_LC = new ArrayList<Double>();
    List<Double> roll = new ArrayList<Double>();
    List<Double> pitch = new ArrayList<Double>();

    // Convert Inches to meters
    public double ToM (double us)
    {
        return ((us * 2.54) / 100);
    }
    // convert inches to cm
    public double ToCm (double us)
    {
        return (us * 2.54);
    }
    // convert inches to feet
    public double ToFeet (double us)
    {
        return (us * (1/12));
    }
    // convert grams to N
    public double ToNewton(double gram)
    {
        return ((gram / 1000) * 9.81);
    }
    // convert grams to kg
    public double ToKilo(double gram)
    {
        return (gram / 1000);
    }
    // convert grams to lb
    public double ToPounds(double gram)
    {
        return (gram * 0.0022);
    }
    // convert degrees to radians
    public double ToRadians(double deg)
    {
        return (deg * (PI / 180));
    }

    public double ThreeSquare(double x, double y, double z)
    {
        return (sqrt((x*x) + (y*y) + (z*z)));
    }

    public double TwoSquare(double x, double y)
    {
        return (sqrt((x*x) + (y*y)));
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
                Intent intent = new Intent(SessionDataActivity.this, LoginActivity.class);
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
                // Map to hold Session Data
                Map<String, Object> session = (Map<String, Object>) dataSnapshot.getValue();
                // List of timestamps that will be ordered
                ArrayList<Time> timeStamps = new ArrayList<Time>();
                // Format of timestamps
                DateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

                // Cast each String timestamp to Time Object and sort
                for (String strTime : session.keySet()) {
                    try {
                        Time time = new Time(sdf.parse(strTime).getTime());
                        timeStamps.add(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(timeStamps);

                // Access each timestamp in chronological order
                // Each timestamp will hold its data values in a Map (datapts)
                // To access an individual data value use:
                //      datapts.get(<Name_of_Data_Value>);
                //      datapts.get("f7");
                // datapts.keySet() holds the names of all data values (not ordered)
                for (Time time : timeStamps) {
                    // Cast timestamps to String for Map access
                    String timeKey = sdf.format(time);
                    // Gets next timestamp dataset in order
                    Map<String,Double> datapts = (Map<String,Double>)session.get(timeKey);

                    //
                    // LOAD DATA SEQUENTIALLY HERE
                    //
                    // Log statement will output values of each data value
                    // for debugging purposes
                    Log.v("E_VALUE", "HAx : " + datapts.get("HAx"));
                    Log.v("E_VALUE", "HAy : " + datapts.get("HAy"));
                    Log.v("E_VALUE", "HAz : " + datapts.get("HAz"));
                    Log.v("E_VALUE", "BAx : " + datapts.get("BAx"));
                    Log.v("E_VALUE", "BAy : " + datapts.get("BAy"));
                    Log.v("E_VALUE", "BAz : " + datapts.get("BAz"));
                    Log.v("E_VALUE", "Gx : " + datapts.get("Gx"));
                    Log.v("E_VALUE", "Gy : " + datapts.get("Gy"));
                    Log.v("E_VALUE", "Gz : " + datapts.get("Gz"));
                    Log.v("E_VALUE", "f0 : " + datapts.get("f0"));
                    Log.v("E_VALUE", "f1 : " + datapts.get("f1"));
                    Log.v("E_VALUE", "f2 : " + datapts.get("f2"));
                    Log.v("E_VALUE", "f3 : " + datapts.get("f3"));
                    Log.v("E_VALUE", "f4 : " + datapts.get("f4"));
                    Log.v("E_VALUE", "f5 : " + datapts.get("f5"));
                    Log.v("E_VALUE", "f6 : " + datapts.get("f6"));
                    Log.v("E_VALUE", "f7 : " + datapts.get("f7"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        DataPoint[] points = new DataPoint[9000];
//        for(int i = 0 ; i < points.length; i++){
//            points[i] = new DataPoint(i,Math.sin(i*0.5) * 20*(Math.random()*10+1));
//
//        }
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
//        graph.addSeries(series);
//        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScrollableY(true);
//        graph.getViewport().setScalable(true);
//        graph.getViewport().setScalableY(true);
/*
        GraphView graph2 = (GraphView) findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 7),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph2.addSeries(series2);

        // styling
        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

        series2.setSpacing(50);

        // draw values on top
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);
  */  }
}
