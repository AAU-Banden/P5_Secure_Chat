package aau.itcom.group_2.p5_secure_chatting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "TheMainActivty";
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseUser = mAuth.getCurrentUser();

        Log.i(TAG, "path " +  getDatabasePath("app_database").getAbsolutePath());

        if (firebaseUser != null){
            Log.i(TAG, "ytjyjytjytjyt" + firebaseUser.getUid());
            startActivity(new Intent(this, ListUsersActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(firebaseUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        /**
         * TODO - WHAT SHOULD HAPPEN TO THE UI...
         */
    }
}
