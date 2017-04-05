
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class AdminActivity extends AppCompatActivity {

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

    /*
        TO-DO: implement a method to delete an existing user
    */

     /*
        TO-DO: implement a method to delete user data
     */
}
