
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class AdminActivity extends AppCompatActivity {

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
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_admin);
    }
    //opens NewUserActivity when user clicks button
    public void newUser(View view) {
        Intent intent = new Intent(AdminActivity.this, NewUserActivity.class);
        startActivity(intent);
        finish();

    }

    protected void associateUser(View View) {
        Intent intent = new Intent(AdminActivity.this, AssociateUserActivity.class);
        startActivity(intent);
        finish();
    }

    protected void associateSession(View view) {
        Intent intent = new Intent(AdminActivity.this, AssociateSessionActivity.class);
        startActivity(intent);
        finish();
    }
}
