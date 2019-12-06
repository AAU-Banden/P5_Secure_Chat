package aau.itcom.group_2.p5_secure_chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.AddContactActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ListUsersActivity extends AppCompatActivity {
    private final String TAG = "ListUsersActivity";
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> arrayList;
    ArrayList<String> arrayListIDs;
    int totalUsers = 2;
    Context context;
    EditText editText_addName;
    ProgressDialog pd;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.add_contact:
                startActivity(new Intent(ListUsersActivity.this, AddContactActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);



        arrayList = new ArrayList<>();
        arrayListIDs = new ArrayList<>();
        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);

        pd = new ProgressDialog(ListUsersActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null) {
            Log.i(TAG, "Firebase-user is not null");
            currentUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            /**
             * Acessing user first names on the database and add them to arrayList
             */
            database.getReference("users").child(currentUserId).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String name = String.valueOf(postSnapshot.child("name").getValue());
                        String lastName = String.valueOf(postSnapshot.child("lastName").getValue());
                        String id = String.valueOf(postSnapshot.child("id").getValue());

                        Log.i(TAG, "Name in arraylist" + name);

                        if (!currentUserId.equals(String.valueOf(postSnapshot.child("id")))) {
                            arrayList.add(name + " " + lastName);
                            arrayListIDs.add(id);
                        }

                        totalUsers++;
                    }
                    pd.dismiss();

                    /**
                     * Hide list if no users are found
                     */
                    if (totalUsers < 1) {
                        noUsersText.setVisibility(View.VISIBLE);
                        usersList.setVisibility(View.GONE);
                        totalUsers = 0;

                    } else {
                        noUsersText.setVisibility(View.GONE);
                        usersList.setVisibility(View.VISIBLE);
                        usersList.setAdapter(new ArrayAdapter<>(ListUsersActivity.this, android.R.layout.simple_list_item_1, arrayList));

                        for (int i = 0; i < arrayList.size(); i++){
                            Log.i(TAG, i + " " + arrayList.get(i));
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else{
            /**
             * TODO: LOG USER OUT
             */
        }



        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(ListUsersActivity.this, ChatActivity.class);
                String clickedContactId = arrayListIDs.get(position);

                Log.i(TAG, "clicked contact id: "+clickedContactId);

                Bundle bundle = new Bundle();
                bundle.putString("CLICKED_USER_FULLNAME", arrayList.get(position));
                bundle.putString("CLICKED_USERID", clickedContactId);
                bundle.putString("CURRENT_USERID", currentUserId);

                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });

    }


    public void search (){

    }
}

