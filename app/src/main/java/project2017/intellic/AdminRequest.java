package project2017.intellic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by aaronitzkovitz on 10/13/17.
 */

public class AdminRequest extends
        AsyncTask<String, Void, Boolean> {

    private OnTaskCompleted listener;

    // firebase endpoints
    private final String addUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/admin/addUser";
    private final String deleteUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/admin/deleteUser";
    private final String getUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/admin/getUser";
    private final String updateUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/admin/updateUser";
    private final String deleteDataURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/admin/deleteData";
    // for logging
    private final String TAG = "post json example";

    // to hold post data
    private String post;
    private String token;

    // hold response
    private JSONObject resBody;
    private int responseCode;

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "1 - Admin request is about to start...");
    }

    @Override
    protected Boolean doInBackground(String... adminFuncs) {
        String adminAction;
        Boolean success = false;
        adminAction = adminFuncs[0];

        if (adminAction == "addUser"){
            success = getServerResponse( addUserURL );
        } else if(adminAction == "deleteUser"){
            success = getServerResponse( deleteUserURL );
        } else if(adminAction == "getUser"){
            success = getServerResponse( getUserURL );
        } else if(adminAction == "updateUser"){
            success = getServerResponse( updateUserURL );
        } else if(adminAction == "deleteData"){
            success = getServerResponse( deleteDataURL );
        } else {
            success = false;
        }

        Log.e(TAG, "2 - pre Request to response...");

        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if ( success ) {
            Log.v(TAG, "The Request Completed." );
            listener.onTaskCompleted(resBody, responseCode);
        } else{
            Log.v(TAG, "The Request failed." );
        }
    }

    public AdminRequest(OnTaskCompleted listener){
        this.listener=listener;
    }

    // get server response
    private Boolean getServerResponse(String urlString){
        String returnedType;
        try {

            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            // set params and request properties
            setupConnection(urlConnection);

            // make the connection
            urlConnection.connect();

            // read from input stream
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while( (line = reader.readLine()) != null ){
                stringBuilder.append( line );
            }

            // read string into JSON
            resBody = new JSONObject(stringBuilder.toString());

            // set properties with reponse info
            responseCode = urlConnection.getResponseCode();
            Log.v("CODE", "" + responseCode);

            // log the type of content returned
            urlConnection.disconnect();
            return true;

        } catch (Exception e) {

            Log.v(TAG, e.toString());

        }

        return null;
    }

    // prepare connection
    private void setupConnection(HttpsURLConnection conn){

        OutputStream out = null;
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // write data to the connection object
        try {
            // set request headers
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json, text/plain");

            out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write( post );
            writer.close();
            out.close();

            Log.v(TAG, "response code is " + conn.getResponseCode());

        } catch( Exception e ){
            Log.v(TAG, e.toString());
        }
    }

    // adds post data to object
    void addPost(Pair<String, ?>... params) throws Exception {

        final JSONObject root = new JSONObject();

        // add data to the post
        for (Pair<String,?> x : params) {
            try {
                root.put(x.first, x.second);
            } catch (JSONException e) {
                Log.v(TAG, "ya fucked up boi");
            }
        }

        post = root.toString();
        Log.v("JSON TEST", post);
    }


    // add auth token to object
    void addToken(String tok){
        token = tok;
    }

    // get request data
    JSONObject getResponseBody(){ return resBody; }
    int getResponseCode(){
        return responseCode;
    }

}