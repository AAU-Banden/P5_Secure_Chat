package aau.itcom.group_2.p5_secure_chatting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Context context;
    Intent intent;
    EditText editText_addName;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    final String TAG = "LOGIN_ACTIVITY";




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = getApplicationContext();

        editText_addName = findViewById(R.id.editText_addName);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //databaseReference = database.getReference();
        //final String user = mAuth.getInstance().getUid();

        final DatabaseReference users = database.getReference().child("users");


        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                //Test


                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot zoneSnapshot: dataSnapshot.getChildren()) {
                            //Log.i(TAG, zoneSnapshot.child("name").getValue(String.class));
                            //String s = zoneSnapshot.child("name").getValue(String.class);
                            String names = String.valueOf(dataSnapshot.child("name").getValue(String.class));
                            Log.i("OUR VALUE", names);

                            if (editText_addName.getText().toString().equals(names)) {
                                Toast.makeText(context, "Ciao", Toast.LENGTH_LONG).show();

                            } else if(editText_addName.getText().toString().equals("WTF")) {
                                Toast.makeText(context, "Nice", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });






            }
        });
    }

    // Going into a chat
    public void chatButtonClicked (View view) {
        intent = new Intent(MenuActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    // Checks if user exists in firebase
/*    public void addFriend(String name){
        Query query = databaseReference.child("users").orderByChild("name");
        System.out.println(query);
        if(name.equals(mAuth.getUid())) {
            if(name.equals(query)) {
                Log.d(TAG, "Hello");
            } else{
                Log.d(TAG, "Shit");
            }
        }


    }*/








}
