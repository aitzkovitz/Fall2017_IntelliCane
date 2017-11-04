package project2017.intellic;

import org.json.JSONObject;

/**
 * Created by aaronitzkovitz on 11/3/17.
 */

// interface to handle async event completion
public interface OnTaskCompleted {
    void onTaskCompleted(JSONObject user);
}
