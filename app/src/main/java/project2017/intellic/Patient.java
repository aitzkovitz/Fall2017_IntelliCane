package project2017.intellic;

import java.util.ArrayList;

/**
 * Created by aaronitzkovitz on 10/10/17.
 */

public final class Patient extends User {

    ArrayList<String> sessions;

    // default constructor
    public Patient(){
        super();
        sessions = new ArrayList<String>();
    }
    public Patient( String fname, String lname, String email ){
        super(fname, lname, email);
        sessions = new ArrayList<String>();
    }

    public void setSessions(ArrayList<String> sessions) {
        this.sessions = sessions;
    }

    public ArrayList<String> getSessions() {
        return sessions;
    }

}
