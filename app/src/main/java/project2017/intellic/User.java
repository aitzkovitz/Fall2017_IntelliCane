package project2017.intellic;

import java.util.ArrayList;


/**
 * Created by aaronitzkovitz on 10/10/17.
 */

public class User {

    public String fname;
    public String lname;
    public String email;

    // default needed for constructor for snapshot.getValue
    public User (){

    }

    public User ( String fname, String lname, String email ) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
