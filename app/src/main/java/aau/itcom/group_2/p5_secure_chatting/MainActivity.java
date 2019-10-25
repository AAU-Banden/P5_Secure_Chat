package aau.itcom.group_2.p5_secure_chatting;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bitch
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
