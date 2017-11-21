package project2017.intellic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.util.Converter;
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

import javax.xml.transform.Templates;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;

public class SessionDataActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    int numOfPoints = 0;
    List<Double> HAx = new ArrayList<Double>();
    List<Double> HAy = new ArrayList<Double>();
    List<Double> HAz = new ArrayList<Double>();
    List<Double> BAx = new ArrayList<Double>();
    List<Double> BAy = new ArrayList<Double>();
    List<Double> BAz = new ArrayList<Double>();
    List<Double> Gx = new ArrayList<Double>();
    List<Double> Gy = new ArrayList<Double>();
    List<Double> Gz = new ArrayList<Double>();
    List<Double> f0 = new ArrayList<Double>();
    List<Double> f1 = new ArrayList<Double>();
    List<Double> f2 = new ArrayList<Double>();
    List<Double> f3 = new ArrayList<Double>();
    List<Double> f4 = new ArrayList<Double>();
    List<Double> f5 = new ArrayList<Double>();
    List<Double> f6 = new ArrayList<Double>();
    List<Double> f7 = new ArrayList<Double>();
    List<Double> HA = new ArrayList<Double>();
    List<Double> BA = new ArrayList<Double>();
    List<Double> G = new ArrayList<Double>();
    List<Double> Fsum = new ArrayList<Double>();
    List<Double> Gt = new ArrayList<Double>();
    List<Double> HAt = new ArrayList<Double>();
    List<Double> BAt= new ArrayList<Double>();
    List<String> T = new ArrayList<String>();
    List<Double> V_US = new ArrayList<Double>();
    List<Double> V_LC = new ArrayList<Double>();
    List<Double> roll = new ArrayList<Double>();
    List<Double> pitch = new ArrayList<Double>();
    List<Templates> H = new ArrayList<>();

    DataPoint[] pointHA ;
    DataPoint[] pointBA;
    DataPoint[] pointG;
    DataPoint[] pointGt;
    DataPoint[] pointHAt;
    DataPoint[] pointBAt;
    DataPoint[] pointFsum;
    DataPoint[] pointsHAx ;
    DataPoint[] pointHAy;
    DataPoint[] pointHAz ;
    DataPoint[] pointBAx;
    DataPoint[] pointBAy ;
    DataPoint[] pointBAz ;
    DataPoint[] pointGx ;
    DataPoint[] pointGy ;
    DataPoint[] pointGz ;
    DataPoint[] pointf0 ;
    DataPoint[] pointf1 ;
    DataPoint[] pointf2 ;
    DataPoint[] pointf3 ;
    DataPoint[] pointf4 ;
    DataPoint[] pointf5 ;
    DataPoint[] pointf6 ;
    DataPoint[] pointf7;
    DataPoint[] pointV_US;
    DataPoint[] pointV_LC;
    DataPoint[] pointRoll ;
    DataPoint[] pointPitch ;

    //TextView text1 = findViewById(R.id.textView23);

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

    public double addForces(double f0, double f1, double f2, double f3, double f4, double f5,double f6, double f7){
        return f0 + f1 + f2 +f3 + f4 + f5 + f6 + f7;

    }

    private String[] arraySpinner;

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
        setContentView(R.layout.activity_session_data);

        // Grab patientID from Intent
        String sid = getIntent().getStringExtra("SESSION_ID");

        // Reference session list under selected Patient user
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference sessionRef = ref.child("Sessions").child(sid);


        this.arraySpinner = new String[] {
                "","HAx", "HAy", "HAz", "BAx", "BAy", "BAz", "Gx", "Gy", "Gz", "f0", "f1", "f2",
                "f3", "f4", "f5", "f6", "f7", "HA", "BA", "G", "Fsum", "Gt", "HAt", "BAt",
                "V_US", "V_LC", "roll", "pitch"
        };


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
                    // Cast timestampt to String for Map access
                    String timeKey = sdf.format(time);
                    // Gets next timestamp dataset in order
                    Map<String,Number> datapts = (Map<String,Number>)session.get(timeKey);

                    //
                    // LOAD DATA SEQUENTIALLY HERE
                    if (datapts != null){

                        T.add(timeKey);
                        HAx.add(datapts.get("HAx").doubleValue());
                        HAy.add(datapts.get("HAy").doubleValue());
                        HAz.add(datapts.get("HAz").doubleValue());
                        BAx.add(datapts.get("BAx").doubleValue());
                        BAy.add(datapts.get("BAy").doubleValue());
                        BAz.add(datapts.get("BAz").doubleValue());
                        Gx.add(datapts.get("Gx").doubleValue());
                        Gy.add(datapts.get("Gy").doubleValue());
                        Gz.add(datapts.get("Gz").doubleValue());
                        f0.add(datapts.get("f0").doubleValue());
                        f1.add(datapts.get("f1").doubleValue());
                        f2.add(datapts.get("f2").doubleValue());
                        f3.add(datapts.get("f3").doubleValue());
                        f4.add(datapts.get("f4").doubleValue());
                        f5.add(datapts.get("f5").doubleValue());
                        f6.add(datapts.get("f6").doubleValue());
                        f7.add(datapts.get("f7").doubleValue());
                        V_US.add(datapts.get("V_US").doubleValue());
                        V_LC.add(datapts.get("V_LC").doubleValue());
                        roll.add(datapts.get("roll").doubleValue());
                        pitch.add(datapts.get("pitch").doubleValue());
                    }


                    //
                    // Log statement will output values of each data value
                    // for debugging purposes
                    //for (String key : datapts.keySet()) {
                    //Log.v("E_VALUE", key + " : " + datapts.get(key));
                    //}

                }

                numOfPoints = HAx.size();
                GraphView graph = (GraphView) findViewById(R.id.graph);
                pointHA = new DataPoint[numOfPoints];
                pointBA = new DataPoint[numOfPoints];
                pointG = new DataPoint[numOfPoints];
                pointGt = new DataPoint[numOfPoints];
                pointHAt = new DataPoint[numOfPoints];
                pointBAt = new DataPoint[numOfPoints];
                pointFsum = new DataPoint[numOfPoints];
                pointsHAx = new DataPoint[numOfPoints];
                pointHAy = new DataPoint[numOfPoints];
                pointHAz = new DataPoint[numOfPoints];
                pointBAx = new DataPoint[numOfPoints];
                pointBAy = new DataPoint[numOfPoints];
                pointBAz = new DataPoint[numOfPoints];
                pointGx = new DataPoint[numOfPoints];
                pointGy = new DataPoint[numOfPoints];
                pointGz = new DataPoint[numOfPoints];
                pointf0 = new DataPoint[numOfPoints];
                pointf1 = new DataPoint[numOfPoints];
                pointf2 = new DataPoint[numOfPoints];
                pointf3 = new DataPoint[numOfPoints];
                pointf4 = new DataPoint[numOfPoints];
                pointf5 = new DataPoint[numOfPoints];
                pointf6 = new DataPoint[numOfPoints];
                pointf7 = new DataPoint[numOfPoints];
                pointV_US = new DataPoint[numOfPoints];
                pointV_LC = new DataPoint[numOfPoints];
                pointRoll = new DataPoint[numOfPoints];
                pointPitch = new DataPoint[numOfPoints];

                //variable used to pass points to our graph


                for(int i=0; i<numOfPoints; ++i){
                    HA.add(ThreeSquare(HAx.get(i), HAy.get(i), HAz.get(i)));
                    BA.add(ThreeSquare(BAx.get(i), BAy.get(i), BAz.get(i)));
                    G.add(ThreeSquare(Gx.get(i), Gy.get(i), Gz.get(i)));
                    Gt.add(TwoSquare(Gx.get(i),Gy.get(i)));
                    HAt.add(TwoSquare(HAx.get(i),HAy.get(i)));
                    BAt.add(TwoSquare(BAx.get(i),BAy.get(i)));
                    Fsum.add(addForces(f0.get(i),f1.get(i),f2.get(i),f3.get(i),f4.get(i),f5.get(i),f6.get(i),f7.get(i)));

                    pointHA[i] = new DataPoint(i,HA.get(i));
                    pointBA[i] = new DataPoint(i,BA.get(i));
                    pointG[i] = new DataPoint(i,G.get(i));
                    pointGt[i] = new DataPoint(i,Gt.get(i));
                    pointHAt[i] = new DataPoint(i,HAt.get(i));
                    pointBAt[i] = new DataPoint(i,BAt.get(i));
                    pointFsum[i] = new DataPoint(i,Fsum.get(i));
                    pointsHAx[i] = new DataPoint(i,HAx.get(i));
                    pointHAy[i] = new DataPoint(i,HAy.get(i));
                    pointHAz[i] = new DataPoint(i,HAz.get(i));
                    pointBAx[i] = new DataPoint(i,BAx.get(i));
                    pointBAy[i] = new DataPoint(i,BAy.get(i));
                    pointBAz[i] = new DataPoint(i,BAz.get(i));
                    pointGx[i] = new DataPoint(i,Gx.get(i));
                    pointGy[i] = new DataPoint(i,Gy.get(i));
                    pointGz[i] = new DataPoint(i,Gz.get(i));
                    pointf0[i] = new DataPoint(i,f0.get(i));
                    pointf1[i] = new DataPoint(i,f1.get(i));
                    pointf2[i] = new DataPoint(i,f2.get(i));
                    pointf3[i] = new DataPoint(i,f3.get(i));
                    pointf4[i] = new DataPoint(i,f4.get(i));
                    pointf5[i] = new DataPoint(i,f5.get(i));
                    pointf6[i] = new DataPoint(i,f6.get(i));
                    pointf7[i] = new DataPoint(i,f7.get(i));
                    pointV_US[i] = new DataPoint(i,V_US.get(i));
                    pointV_LC[i] = new DataPoint(i,V_LC.get(i));
                    pointRoll[i] = new DataPoint(i,roll.get(i));
                    pointPitch[i] = new DataPoint(i,pitch.get(i));


                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(pointsHAx);
                graph.addSeries(series);
                graph.getViewport().setScrollable(true);
                graph.getViewport().setScrollableY(true);
                graph.getViewport().setScalable(true);
                graph.getViewport().setScalableY(true);

            }





            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        final Spinner  s= (Spinner) findViewById(R.id.spinner4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        AdapterView.OnItemSelectedListener spinnerlist = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int i, long l) {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                LineGraphSeries<DataPoint> series;

                String st = Long.toString(spinner.getItemIdAtPosition(i));
                TextView test = (TextView) findViewById(R.id.textView22);
                test.setText(st);
                TextView max = (TextView) findViewById(R.id.textView24);
                TextView min = (TextView) findViewById(R.id.textView20);
                TextView Mean = (TextView) findViewById(R.id.textView12);
                Double calMean;
                TextView StandardDeveation = (TextView) findViewById(R.id.textView13);

                switch (st) {
                    case "1":
                        series = new LineGraphSeries<>(pointsHAx);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(HAx)));
                        min.setText(Double.toString(Collections.min(HAx)));
                        calMean=calculateMean(HAx);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(HAx,calMean)));
                        break;
                    case "2":
                        series = new LineGraphSeries<>(pointHAy);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(HAy)));
                        min.setText(Double.toString(Collections.min(HAy)));
                        calMean=calculateMean(HAy);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(HAy,calMean)));
                        break;
                    case "3":
                        series = new LineGraphSeries<>(pointHAz);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(HAz)));
                        min.setText(Double.toString(Collections.min(HAz)));
                        calMean=calculateMean(HAz);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(HAz,calMean)));
                        break;
                    case "4":
                        series = new LineGraphSeries<>(pointBAx);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(BAx)));
                        min.setText(Double.toString(Collections.min(BAx)));
                        calMean=calculateMean(BAx);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(BAx,calMean)));
                        break;
                    case "5":
                        series = new LineGraphSeries<>(pointBAy);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(BAy)));
                        min.setText(Double.toString(Collections.min(BAy)));
                        calMean=calculateMean(BAy);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(BAy,calMean)));
                        break;
                    case "6":
                        series = new LineGraphSeries<>(pointBAz);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(BAz)));
                        min.setText(Double.toString(Collections.min(BAz)));
                        calMean=calculateMean(BAz);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(BAz,calMean)));
                        break;
                    case "7":
                        series = new LineGraphSeries<>(pointGx);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(Gx)));
                        min.setText(Double.toString(Collections.min(Gx)));
                        calMean=calculateMean(Gx);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(Gx,calMean)));
                        break;
                    case "8":
                        series = new LineGraphSeries<>(pointGy);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(Gy)));
                        min.setText(Double.toString(Collections.min(Gy)));
                        calMean=calculateMean(Gy);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(Gy,calMean)));
                        break;
                    case "9":
                        series = new LineGraphSeries<>(pointGz);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(Gz)));
                        min.setText(Double.toString(Collections.min(Gz)));
                        calMean=calculateMean(Gz);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(Gz,calMean)));
                        break;
                    case "10":
                        series = new LineGraphSeries<>(pointf0);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f0)));
                        min.setText(Double.toString(Collections.min(f0)));
                        calMean=calculateMean(f0);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f0,calMean)));
                        break;
                    case "11":
                        series = new LineGraphSeries<>(pointf1);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f1)));
                        min.setText(Double.toString(Collections.min(f1)));
                        calMean=calculateMean(f1);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f1,calMean)));
                        break;
                    case "12":
                        series = new LineGraphSeries<>(pointf2);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f2)));
                        min.setText(Double.toString(Collections.min(f2)));
                        calMean=calculateMean(f2);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f2,calMean)));
                        break;
                    case "13":
                        series = new LineGraphSeries<>(pointf3);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f3)));
                        min.setText(Double.toString(Collections.min(f3)));
                        calMean=calculateMean(f3);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f3,calMean)));
                        break;
                    case "14":
                        series = new LineGraphSeries<>(pointf4);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f4)));
                        min.setText(Double.toString(Collections.min(f4)));
                        calMean=calculateMean(f4);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f4,calMean)));
                        break;
                    case "15":
                        series = new LineGraphSeries<>(pointf5);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f5)));
                        min.setText(Double.toString(Collections.min(f5)));
                        calMean=calculateMean(f5);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f5,calMean)));
                        break;
                    case "16":
                        series = new LineGraphSeries<>(pointf6);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f6)));
                        min.setText(Double.toString(Collections.min(f6)));
                        calMean=calculateMean(f6);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f6,calMean)));
                        break;
                    case "17":
                        series = new LineGraphSeries<>(pointf7);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(f7)));
                        min.setText(Double.toString(Collections.min(f7)));
                        calMean=calculateMean(f7);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(f7,calMean)));
                        break;
                    case "18":
                        series = new LineGraphSeries<>(pointHA);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(HA)));
                        min.setText(Double.toString(Collections.min(HA)));
                        calMean=calculateMean(HA);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(HA,calMean)));
                        break;
                    case "19":
                        series = new LineGraphSeries<>(pointBA);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(BA)));
                        min.setText(Double.toString(Collections.min(BA)));
                        calMean=calculateMean(BA);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(BA,calMean)));
                        break;
                    case "20":
                        series = new LineGraphSeries<>(pointG);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(G)));
                        min.setText(Double.toString(Collections.min(G)));
                        calMean=calculateMean(G);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(G,calMean)));
                        break;
                    case "21":
                        series = new LineGraphSeries<>(pointFsum);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(Fsum)));
                        min.setText(Double.toString(Collections.min(Fsum)));
                        calMean=calculateMean(Fsum);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(Fsum,calMean)));
                        break;
                    case "22":
                        series = new LineGraphSeries<>(pointGt);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(Gt)));
                        min.setText(Double.toString(Collections.min(Gt)));
                        calMean=calculateMean(Gt);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(Gt,calMean)));
                        break;
                    case "23":
                        series = new LineGraphSeries<>(pointHAt);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(HAt)));
                        min.setText(Double.toString(Collections.min(HAt)));
                        calMean=calculateMean(HAt);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(HAt,calMean)));
                        break;

                    case "24":
                        series = new LineGraphSeries<>(pointBAt);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(BAt)));
                        min.setText(Double.toString(Collections.min(BAt)));
                        calMean=calculateMean(BAt);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(BAt,calMean)));
                        break;

                    case "25":
                        series = new LineGraphSeries<>(pointV_US);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(V_US)));
                        min.setText(Double.toString(Collections.min(V_US)));
                        calMean=calculateMean(V_US);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(V_US,calMean)));
                        break;
                    case "26":
                        series = new LineGraphSeries<>(pointV_LC);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(V_LC)));
                        min.setText(Double.toString(Collections.min(V_LC)));
                        calMean=calculateMean(V_LC);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(V_LC,calMean)));
                        break;
                    case "27":
                        series = new LineGraphSeries<>(pointRoll);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(roll)));
                        min.setText(Double.toString(Collections.min(roll)));
                        calMean=calculateMean(roll);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(roll,calMean)));
                        break;
                    case "28":
                        series = new LineGraphSeries<>(pointPitch);
                        graph.addSeries(series);
                        graph.getViewport().setScrollable(true);
                        graph.getViewport().setScrollableY(true);
                        graph.getViewport().setScalable(true);
                        graph.getViewport().setScalableY(true);
                        max.setText(Double.toString(Collections.max(pitch)));
                        min.setText(Double.toString(Collections.min(pitch)));
                        calMean=calculateMean(pitch);
                        Mean.setText(Double.toString(calMean));
                        StandardDeveation.setText(Double.toString(CalculateSD(pitch,calMean)));
                        break;

                }}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        s.setOnItemSelectedListener(spinnerlist);


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
  //this function add the whole array and divides by the numbr of items
    private Double calculateMean(List <Double> Signal) {
        double sum = 0;
        for (int i=0; i< Signal.size(); i++) {
            sum += Signal.get(i);
        }
        return sum / Signal.size();
    }

    //this function takes the squared differance of the element minus the average divide by the
    // number of elements and returns the squareroot of that
    private double CalculateSD(List <Double> Signal,double Mean)
    {

        double sd = 0;
        for (int i = 0; i < Signal.size(); i++)
        {
            sd += Math.pow((Signal.get(i) - Mean),2.0);
        }
        sd=sd/Signal.size();
        return sqrt(sd);

    }
}
