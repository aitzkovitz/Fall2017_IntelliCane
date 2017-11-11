package project2017.intellic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aaronitzkovitz on 11/8/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
