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

public class LoginActivity extends AppCompatActivity {
    String EXTRA_ID = "emailID";
    String phoneOrEmail;
    String password;
    Intent intent;
    EditText editTextPhoneOrEmail;
    EditText editTextPassword;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    final String TAG = "LOGIN_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intent = getIntent();
        editTextPhoneOrEmail = findViewById(R.id.editText_phoneNumberLogin);
        editTextPassword = findViewById(R.id.editText_passwordLogin);

        phoneOrEmail = getIntent().getStringExtra(EXTRA_ID);
        Log.i(TAG,  "email from create activty: "+  phoneOrEmail);
        editTextPhoneOrEmail.setText(phoneOrEmail);



        phoneOrEmail = editTextPhoneOrEmail.getText().toString();
        password = editTextPassword.getText().toString();


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();



        final Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(LoginActivity.this, MainActivity.class);

                /**
                 * TO DO: Create a login with phone number option
                 * Include some code that can check whether a user enters email or phone.
                 */


                loginWithEmail(phoneOrEmail, password);
                startActivity(intent);
            }
        });


        final Button signUpButton = findViewById(R.id.button_SignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);



            }
        });


    }

    public void loginWithEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
