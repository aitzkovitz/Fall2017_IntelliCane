package project2017.intellic;

import java.util.ArrayList;

/**
 * Created by aaronitzkovitz on 10/10/17.
 */

public final class Therapist extends User {

    ArrayList<String> patients;

    // default constructor
    public Therapist(){
        super();
        patients = new ArrayList<String>();
    }
    public Therapist( String fname, String lname, String email ){
        super(fname, lname, email);
        patients = new ArrayList<String>();
    }

    public void setPatients(ArrayList<String> patients) {
        this.patients = patients;
    }

    public ArrayList<String> getPatients() {
        return patients;
    }

}
