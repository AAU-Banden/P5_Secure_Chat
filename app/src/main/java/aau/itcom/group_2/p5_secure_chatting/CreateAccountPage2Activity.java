package aau.itcom.group_2.p5_secure_chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.nfc.NfcAdapter.EXTRA_ID;

public class CreateAccountPage2Activity extends AppCompatActivity {

    String EXTRA_ID = "emailID";
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String passwordStr;
    String confirmPasswordStr;
    String id;
    User user;
    Intent intent;

    String TAG = "CREATE ACCOUNT";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_page2);

        Intent intent = getIntent();

        final EditText password = findViewById(R.id.editText_createPassword);
        final EditText confirmPassword = findViewById(R.id.editText_confirmPassword);


        /**
         * Getting information from previous activity.
         */
        Bundle extras = intent.getExtras();
        if(extras != null) {
            firstName = extras.getString("FIRST_NAME");
            lastName = extras.getString("LAST_NAME");
            email = extras.getString("EMAIL");
            phoneNumber = extras.getString("PHONE_NUMBER");
        }else{
            Log.e(TAG, "NO BUNDLE WITH INPUT");
        }

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();



        final Button createButton = findViewById(R.id.button_createAccount);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "EMAIL: " + email);
                passwordStr = password.getText().toString();
                confirmPasswordStr = confirmPassword.getText().toString();
                createFirebaseUser(email, passwordStr);


            }
        });


    }


    /**
     * Creating Firebase User - Method more or less taken from Firebase documentation
     * @param email
     * @param password
     */
    private void createFirebaseUser (String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            onAuthSuccess(task.getResult().getUser());
                            // signing out so it doesn't think a user is logged in...
                            mAuth.signOut();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountPage2Activity.this, "Account creation failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }

        });
    }

    /**
     * Adding user into database with all user data.
     * @param firebaseUser
     *
     */
    private void onAuthSuccess(FirebaseUser firebaseUser) {
        // Write new user
        id = firebaseUser.getUid();
        user = new User (firstName, lastName, phoneNumber, email, passwordStr, false, id);
        databaseReference.child("users").child(id).setValue(user);
        // Go to MainActivity
        intent = new Intent (this, LoginActivity.class);
        intent.putExtra(EXTRA_ID, email);
        startActivity(intent);
        finish();
    }




}
