package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import aau.itcom.group_2.p5_secure_chatting.ListUsersActivity;
import aau.itcom.group_2.p5_secure_chatting.R;
import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    private final static String TAG = "AddContactActivity";
    FirebaseDatabase database;
    EditText editText_addContact;
    String email;
    String requestedID;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    User user;
    String userID;
    ContactRequest contactRequest;
    ArrayList<ContactRequest> contactRequests;
    User currentUser;
    String currentUserId;
    Contact contact;
    ListView requestList;
    ArrayList<String> arrayList;
    ProgressDialog pd;
    int counter = 0;
    int totalUsers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editText_addContact = findViewById(R.id.editText_addContact);
        requestList = findViewById(R.id.requestList);

        arrayList = new ArrayList<>();
        contactRequests = new ArrayList<>();

        pd = new ProgressDialog(AddContactActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();
            /**
             * Acessing user first names on the database and add them to arrayList
             */
            database.getReference("users").child(currentUserId).child("contactRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        contactRequest = dataSnapshot.child(currentUserId).child("contactRequests").getValue(ContactRequest.class);

                        contactRequests.add(contactRequest);

                        Log.i(TAG, arrayList.get(0));

                        totalUsers++;
                    }
                    pd.dismiss();

                    /**
                     * Hide list if no users are found
                     */
                    if (totalUsers < 1) {
                        requestList.setVisibility(View.GONE);
                        totalUsers = 0;
                        Log.i(TAG, "first if");

                    } else {
                        Log.i(TAG, "else");
                        requestList.setVisibility(View.VISIBLE);
                        requestList.setAdapter(new CustomAdapter(contactRequests, AddContactActivity.this));

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }


    }

    public void sendContactRequest (View view){
        /**
         * First finding the id that is searched with the email
         */
        email = editText_addContact.getText().toString();

        if (!email.equals("")) {

            database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String emailDatabase = String.valueOf(postSnapshot.child("email").getValue());

                        counter ++;
                        Log.i(TAG, emailDatabase);
                        Log.i(TAG, "typed mail: " +  email);

                        if (email.equals(emailDatabase)) {
                            requestedID = String.valueOf(postSnapshot.child("id").getValue());

                            currentUserId = mAuth.getUid();
                            currentUser = dataSnapshot.child(currentUserId).getValue(User.class);
                            Log.i(TAG, "id: " + requestedID + " users name" + currentUser.getName());
                            if (currentUser!=null){
                                contact = new Contact(currentUser.getName(), currentUser.getLastName(), currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getID());
                                contactRequest = new ContactRequest(contact);
                                database.getReference().child("users").child(requestedID).child("contactRequests")
                                        .child(contactRequest.getContactRequestID()).setValue(contactRequest);
                                Log.i(TAG, "Adding contact request to firebase");
                            }
                            break;
                        } else if (counter == dataSnapshot.getChildrenCount()){
                            editText_addContact.setError("No user found with this email");
                            counter = 0;
                        }
                    }



                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


        } else{
            editText_addContact.setError("Please enter an email");
        }


    }

    public void acceptRequest (final ContactRequest contactRequest){

        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userID = firebaseUser.getUid();
                user = dataSnapshot.child("users").getValue(User.class);
                Contact contact = null;
                if (user != null) {
                    contact = new Contact(user.getName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getID());
                }
                database.getReference().child("users").child(userID).child("contacts").setValue(contactRequest.getContact());
                database.getReference().child("users").child(contactRequest.getContactRequestID()).child("contacts").setValue(contact);

                // Removing request
                database.getReference().child("users").child(userID).child(contactRequest.getContactRequestID()).removeValue();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void declineRequest (final ContactRequest contactRequest){
        // Removing request
        database.getReference().child("users").child(userID).child(contactRequest.getContactRequestID()).removeValue();



    }
}
