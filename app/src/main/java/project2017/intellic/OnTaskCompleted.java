package project2017.intellic;

import org.json.JSONObject;

/**
 * Created by aaronitzkovitz on 11/3/17.
 */

/* An instance of this class will be passed to every instance of
of adminRequest to define the completion action.
 */

// interface to handle async event completion
public interface OnTaskCompleted {
    void onTaskCompleted(JSONObject user, int code);
}
