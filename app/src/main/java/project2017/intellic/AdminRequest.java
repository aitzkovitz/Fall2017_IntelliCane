package project2017.intellic;

import android.content.Context;
import android.os.AsyncTask;
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

import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by aaronitzkovitz on 10/13/17.
 */

public class AdminRequest extends
        AsyncTask<String, Void, String> {

    // firebase endpoints
    private final String addUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/app/";
    private final String deleteUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/deleteUser";
    private final String editUserURL = "https://us-central1-icane-41ce5.cloudfunctions.net/editUser";

    // for logging
    private final String TAG = "post json example";

    // to hold post data
    private String post;
    private String token;

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "1 - Admin request is about to start...");
    }

    @Override
    protected String doInBackground(String... adminFuncs) {
        boolean status = false;
        String resp, adminAction;
        adminAction = adminFuncs[0];

        if (adminAction == "newUser"){
            resp = getServerResponse(addUserURL);
        } else if(adminAction == "deleteUser"){
            resp = getServerResponse(deleteUserURL);
        } else if(adminAction == "editUser"){
            resp = getServerResponse(editUserURL);
        } else {
            resp = "no action was entered";
        }

        Log.e(TAG, "2 - pre Request to response...");

        return resp;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.v(TAG, "7 - onPostExecute returned " + result );

    }

    // get server response
    private String getServerResponse(String urlString){
        String returnedType;
        try {

            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            // set params and request properties
            setupConnection(urlConnection);

            // make the connection
            urlConnection.connect();

            Class[] classes = { InputStream.class };

            // get content from connection
            if (urlConnection.getContent(classes) instanceof String){
                returnedType = "string";
            } else {
                returnedType = "not a string";
            }

            // log the type of content returned
            urlConnection.disconnect();
            return returnedType;

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
            //conn.setChunkedStreamingMode(0);

            out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(post);
            writer.close();
            out.close();

            Log.v(TAG, "response code is "+conn.getResponseCode());


        } catch( Exception e ){
            Log.v(TAG, e.toString());
        }
    }

    // adds post data to object
    void addPost(Pair<String, String>... params) throws Exception {

        final JSONObject root = new JSONObject();

        // add data to the post
        for (Pair<String,String> x : params) {
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

}