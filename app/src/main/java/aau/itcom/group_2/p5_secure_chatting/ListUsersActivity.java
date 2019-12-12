package aau.itcom.group_2.p5_secure_chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.crypto.tink.proto.KeyData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.AddContactActivity;
import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import aau.itcom.group_2.p5_secure_chatting.chatting.ChatActivity;
import aau.itcom.group_2.p5_secure_chatting.chatting.Message;
import aau.itcom.group_2.p5_secure_chatting.key_creation.Key;
import aau.itcom.group_2.p5_secure_chatting.local_database.AppDatabase;
import aau.itcom.group_2.p5_secure_chatting.local_database.ContactDAO;
import aau.itcom.group_2.p5_secure_chatting.local_database.KeyDAO;
import aau.itcom.group_2.p5_secure_chatting.local_database.MessageDAO;
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
    Contact contact;
    AppDatabase localDatabase;
    ContactDAO contactDAO;
    MessageDAO messageDAO;
    ArrayList<Contact> contacts;
    ArrayList<String> contactNames;
    KeyDAO keyDAO;

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


        contactNames = new ArrayList<>();
        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);

        pd = new ProgressDialog(ListUsersActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        localDatabase = AppDatabase.getInstance(this);
        contactDAO = localDatabase.getContactDAO();
        messageDAO = localDatabase.getMessageDAO();
        keyDAO = localDatabase.getKeyDAO();
        //contactDAO.nukeTable();
        //messageDAO.nukeTable();


        if (firebaseUser != null) {
            Log.i(TAG, "Firebase-user is not null");
            currentUserId = firebaseUser.getUid();
            database = FirebaseDatabase.getInstance();

            database.getReference().child("users").child(currentUserId).child("contacts").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {


                    contact = dataSnapshot.getValue(Contact.class);

                    if (contact != null) {
                        Log.i(TAG, "message: " + contact.getEmail());
                        contactDAO.insertContact(contact);
                        database.getReference().child("users").child(currentUserId).child("contacts").child(contact.getId()).removeValue();
                        /**
                         * Creating the shared key if a contactRequest have been accepted
                         */

                        try {
                            AddContactActivity.createSharedKey(contact);
                        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeySpecException | InvalidKeyException | NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
        } else {
            /**
             * TODO: LOG USER OUT
             */
        }


        /**
         * Acessing users in the local database and sorting the list alphabetically. Then adding them to the UI
         */


        contacts = (ArrayList<Contact>) contactDAO.loadAllContacts();

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                String s1 = contact1.getName();
                String s2 = contact2.getName();
                return s1.compareToIgnoreCase(s2);
            }
        });

        for (Contact contact : contacts) {
            contactNames.add(contact.getName() + " " + contact.getLastName());
        }

        if (contacts.size() < 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
            totalUsers = 0;

        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<>(ListUsersActivity.this, android.R.layout.simple_list_item_1, contactNames));

        }
        pd.dismiss();




            /*
            database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.child("users").child(currentUserId).child("contacts").getChildren()) {
                        String id = String.valueOf(postSnapshot.child("id").getValue());
                        Log.i(TAG, String.valueOf(dataSnapshot.child("users").child(currentUserId).child("contacts").getChildrenCount()) + " " +  dataSnapshot.child("contact").getChildrenCount());
                        Log.i(TAG, id);


                        contact  = dataSnapshot.child("contact").child(id).getValue(Contact.class);


                        Log.i(TAG, "Name in arraylist" + contact.getName());

                        if (!currentUserId.equals(String.valueOf(postSnapshot.child("id")))) {
                            arrayList.add(contact.getName() + " " + contact.getLastName());
                            arrayListIDs.add(id);
                        }

                        totalUsers++;
                    }


                    /**
                     * Hide list if no users are found
                     */
                    /*
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


                     */


        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(ListUsersActivity.this, ChatActivity.class);
                String clickedContactId = contacts.get(position).getId();

                Log.i(TAG, "clicked contact id: " + clickedContactId);

                Bundle bundle = new Bundle();
                bundle.putString("CLICKED_USER_FULLNAME", contacts.get(position).getName() + " " + contacts.get(position).getLastName());
                bundle.putString("CLICKED_USERID", clickedContactId);
                bundle.putString("CURRENT_USERID", currentUserId);
                bundle.putByteArray("CLICKED_USERIV", contacts.get(position).getIv());

                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });

    }


    public void search() {

    }

}
