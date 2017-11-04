package project2017.intellic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by aaronitzkovitz on 10/10/17.
 */

public class User implements Parcelable {

    // user id
    private String uid;

    // database userinfo
    private String fname;
    private String lname;

    // auth userinfo
    private String email;
    private String password;
    private String displayName;
    private String phone;
    private String photoURL;
    private boolean disabled;
    private boolean emailVerified;

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

    // construct from JSON
    public User(JSONObject json){
        try{
            if (json.has( "email" )){
                email = json.getString( "email" );
            }
            if (json.has( "fname" )){
                fname = json.getString( "fname" );
            }
            if (json.has( "lname" )){
                lname = json.getString( "lname" );
            }
            if (json.has( "password" )){
                password = json.getString( "password" );
            }
            if (json.has( "displayName" )){
                displayName = json.getString( "displayName" );
            }
            if (json.has( "phone" )){
                phone = json.getString( "phone" );
            }
            if (json.has( "photoURL" )){
                photoURL = json.getString( "photoURL" );
            }
            if (json.has( "disabled" )){
                disabled = json.optBoolean( "disabled", false );
            }
            if (json.has( "emailVerified" )){
                emailVerified = json.optBoolean( "emailVerified" );
            }
        } catch (JSONException e){
            Log.v("JSON CONSTRUCTOR", e.toString());
        }

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
        email = in.readString();
        password = in.readString();
        phone = in.readString();
        displayName = in.readString();
        photoURL = in.readString();
        emailVerified = in.readByte() != 0;
        disabled = in.readByte() != 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fname);
        out.writeString(lname);
        out.writeString(email);
        out.writeString(password);
        out.writeString(phone);
        out.writeString(displayName);
        out.writeString(photoURL);
        out.writeInt((int) (emailVerified ? 1 : 0));
        out.writeInt((int) (disabled ? 1 : 0));


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

    public boolean isEmailVerified(){
        return emailVerified;
    }

    public void setEmailVerified( boolean verified ){
        emailVerified = verified;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
