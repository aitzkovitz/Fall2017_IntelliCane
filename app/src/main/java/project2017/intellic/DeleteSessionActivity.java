package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaronitzkovitz on 11/11/17.
 */

public class DeleteSessionActivity extends AppCompatActivity {

    private ListView sessionListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get session info from sender
        Bundle extraInfo = this.getIntent().getExtras();
        if ( extraInfo != null ){

            // get sessions
            ArrayList<String> sessions = extraInfo.getStringArrayList("SESSION_ARRAY");

            // Populate ListView with list of sessions
            sessionListView = (ListView) findViewById(R.id.deleteSessionListView);


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    DeleteSessionActivity.this,
                    android.R.layout.simple_list_item_1,
                    sessions);
            sessionListView.setAdapter(arrayAdapter);


        }else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteSessionActivity.this, LoginActivity.class);
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

    // send new info to server
    void deleteSession(View view){
        // delete some sessions

    }
}
