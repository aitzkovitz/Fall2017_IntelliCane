package project2017.intellic;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by aaronitzkovitz on 11/8/17.
 */

// To Be Implemented: should be accesible offline
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
