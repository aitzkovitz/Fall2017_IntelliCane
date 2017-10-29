package project2017.intellic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by aaronitzkovitz on 10/10/17.
 */

public class User implements Parcelable {

    private String fname;
    private String lname;
    private String password;
    private String phone;
    private String displayName;
    private String uid;
    private boolean disabled;
    private String photoURL;
    private String email;

    // TBI: add copy constructor
    // TBI: add variadic constructor
    public User (String fname, String lname, String email){
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

    // default needed for constructor for snapshot.getValue
    public User(){

    }

    // make it parcelable so we can pass instances from and to activities
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    private User(Parcel in) {
        fname = in.readString();
        lname = in.readString();
        password = in.readString();
        phone = in.readString();
        displayName = in.readString();
        disabled = in.readByte() != 0;
        photoURL = in.readString();
        email = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fname);
        out.writeString(lname);
        out.writeString(password);
        out.writeString(phone);
        out.writeString(displayName);
        out.writeInt((int) (disabled ? 1 : 0));
        out.writeString(photoURL);
        out.writeString(email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
