package project2017.intellic;

import android.graphics.Color;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.ValueDependentColor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class SessionDataActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_data);


        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint[] points = new DataPoint[9000];
        for(int i = 0 ; i < points.length; i++){
            points[i] = new DataPoint(i,Math.sin(i*0.5) * 20*(Math.random()*10+1));

        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
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

//    private FirebaseDatabase database;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_session_data);
//
//        // Grab patientID from Intent
//        String sid = getIntent().getStringExtra("SESSION_ID");
//
//        // Reference session list under selected Patient user
//        database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
//        DatabaseReference sessionRef = ref.child("Sessions").child(sid);
//
//        sessionRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
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
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }
}
