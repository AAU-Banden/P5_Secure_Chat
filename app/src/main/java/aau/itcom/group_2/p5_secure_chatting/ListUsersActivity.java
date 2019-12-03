package aau.itcom.group_2.p5_secure_chatting;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ListUsersActivity extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 2;
    Context context;
    EditText editText_addName;
    final String TAG = "LOGIN_ACTIVITY";
    ProgressDialog pd;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /*@Override
        public void onResume() {
            super.onResume();*/

           setContentView(R.layout.activity_list_users);

           ActionBar actionBar = getSupportActionBar();
           assert actionBar != null;
           actionBar.setTitle("ListUsersActivity");



           usersList = findViewById(R.id.usersList);
            noUsersText = findViewById(R.id.noUsersText);

            pd = new ProgressDialog(ListUsersActivity.this);
            pd.setMessage("Loading...");
            pd.show();


            database = FirebaseDatabase.getInstance();

            /**
             * Unfinished code for the search bar
             */
        /*final DatabaseReference users = database.getReference().child("users");

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
        });*/


            /**
             * Acessing user first names on the database and add them to arrayList
             */
            database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String name = String.valueOf(postSnapshot.child("name").getValue());
                        String loggedIn = String.valueOf(postSnapshot.child("id").getValue());

                        if (!loggedIn.equals(UserDetails.userId)) {
                            al.add(name);
                        } else {
                            UserDetails.username = name;
                        }

                        totalUsers++;
                    }
                    pd.dismiss();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
            /**
             * Hide list if no users are found
             */
            if (totalUsers <= 1) {
                noUsersText.setVisibility(View.VISIBLE);
                usersList.setVisibility(View.GONE);

            } else {
                noUsersText.setVisibility(View.GONE);
                usersList.setVisibility(View.VISIBLE);
                usersList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, al));

            }

            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserDetails.chatWith = al.get(position);

                    startActivity(new Intent(ListUsersActivity.this, ChatActivity.class));
                }
            });

        }


        }

