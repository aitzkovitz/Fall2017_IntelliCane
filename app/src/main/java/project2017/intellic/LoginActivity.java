
/**
 * Created by bk_conazole on 4/5/17.
 */

package project2017.intellic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;



public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void goto_activity_login(View view) {setContentView(R.layout.activity_login);}

    public void loginPublic(View view) {
        login();
    }

    // this method will log user into firebase
    private void login() {

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);
        String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        //hide the keyboard after user entered password
        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                editTextPassword.getWindowToken(), 0);

        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        // authenticate user by using
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (password.length() < 6)
                            {
                                editTextPassword.setError(getString(R.string.minimum_password));
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            progressDialog.dismiss();
                            getRole();
                            Toast.makeText(LoginActivity.this, "welcome to intelliC", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // this method determines role of a user and give access accordingly
    private void getRole()
    {
        // getting the current users user id so that to access roles on the database
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReferenceFromUrl("https://icane-41ce5.firebaseio.com/");
        DatabaseReference roleRef = dbRef.child("Roles").child(uid);

        roleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Administrator = "Administrator";
                String Therapist = "Therapist";
                String Patient = "Patient";
                String role = "";
                role = dataSnapshot.getValue( String.class);
                //invokes an admin activity
                if(role.equals(Administrator))
                {
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }
                // invokes Patient activity
                else if(role.equals(Patient))
                {
                    Intent intent = new Intent(LoginActivity.this, SessionSelectActivity.class);
                    intent.putExtra("PATIENT_ID", uid);
                    startActivity(intent);
                    finish();
                }
                // invokes Therapist activity
                else if(role.equals(Therapist))
                {
                    Intent intent = new Intent(LoginActivity.this, TherapistActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "role is not assigned", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(LoginActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

