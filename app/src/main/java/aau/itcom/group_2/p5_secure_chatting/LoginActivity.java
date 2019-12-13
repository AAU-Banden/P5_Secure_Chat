package aau.itcom.group_2.p5_secure_chatting;

import aau.itcom.group_2.p5_secure_chatting.create_account.CreateAccountActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    Context context;
    String EXTRA_ID = "emailID";
    String phoneOrEmail;
    String password;
    String user, pass;
    Intent intent;
    Intent intentListUser;
    EditText editTextPhoneOrEmail;
    EditText editTextPassword;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    final String TAG = "LOGIN_ACTIVITY";



    int toastDuration = Toast.LENGTH_SHORT;
    CharSequence missingLoginInfo = "Missing login information";
    CharSequence loginFail = "Authentication failed.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        context = getApplicationContext();
        intent = getIntent();
        editTextPhoneOrEmail = findViewById(R.id.editText_emailLogin);
        editTextPassword = findViewById(R.id.editText_passwordLogin);

        phoneOrEmail = getIntent().getStringExtra(EXTRA_ID);
        Log.i(TAG,  "email from create activty: "+  phoneOrEmail);
        editTextPhoneOrEmail.setText(phoneOrEmail);






        //Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();





    }

    public void signUpButtonClicked (View view){
        intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }


    public void loginButtonClicked (View view){
        /**
         * TO DO: Create a login with phone number option
         * Include some code that can check whether a user enters email or phone.
         */

        phoneOrEmail = editTextPhoneOrEmail.getText().toString();
        password = editTextPassword.getText().toString();
        Log.i(TAG, phoneOrEmail + " " +  password);
        if (phoneOrEmail.equals("")){
            editTextPhoneOrEmail.setError("Please insert email");
            Toast.makeText(context, missingLoginInfo, toastDuration).show();
        } else if (password.equals("")){
            editTextPassword.setError("Please insert password");
            Toast.makeText(context, missingLoginInfo, toastDuration).show();
        }else{
            loginWithEmail(phoneOrEmail, password);
        }
    }



    public void loginWithEmail(String email, String password){
        intentListUser = new Intent(LoginActivity.this, ListUsersActivity.class);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getUid();
                            //username = findViewById(R.id.username);
                            //password = findViewById(R.id.editText_passwordLogin);
                            //user = editTextPhoneOrEmail.getText().toString();
                            pass = editTextPassword.getText().toString();
                            startActivity(intentListUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            editTextPhoneOrEmail.setError("Email or password doesn't exist.");
                            editTextPassword.setText("");
                            Toast.makeText(context, loginFail, toastDuration).show();
                        }

                        // ...
                    }
                });
    }
}
