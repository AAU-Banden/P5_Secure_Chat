package aau.itcom.group_2.p5_secure_chatting.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import aau.itcom.group_2.p5_secure_chatting.R;
import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import aau.itcom.group_2.p5_secure_chatting.local_database.AppDatabase;
import aau.itcom.group_2.p5_secure_chatting.local_database.MessageDAO;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.RoomDatabase;

public class ChatActivity extends AppCompatActivity {
    String TAG = "ChatActivity";
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    String currentUserId;
    User currentUser;
    String clickedUserId;
    ProgressDialog pd;
    Message message;
    String fullNameClickedUser;
    TextView textView;
    static AppDatabase localDatabase;
    static MessageDAO messageDAO;
    ArrayList<Message> messages;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_chatactivity, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        layout = findViewById(R.id.layout1);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        textView = (TextView) myToolbar.findViewById(R.id.toolbarTextView);
        textView.setText(fullNameClickedUser);


        database = FirebaseDatabase.getInstance();
        localDatabase = AppDatabase.getInstance(this);
        messageDAO = localDatabase.getMessageDAO();

        pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        messages = (ArrayList<Message>) messageDAO.loadAllMessages();

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                String s1 = message1.getTime();
                String s2 = message2.getTime();
                return s1.compareToIgnoreCase(s2);
            }
        });
        for (Message message : messages) {
            if (!message.getIdOfSender().equals(currentUserId)) {
                addMessageBox(message.getMessage(), 1);
            } else {
                addMessageBox(message.getMessage(), 2);
            }
        }
        pd.dismiss();



        Bundle extras = intent.getExtras();
        if(extras != null) {
            fullNameClickedUser = extras.getString("CLICKED_USER_FULLNAME");
            currentUserId = extras.getString("CURRENT_USERID");
            clickedUserId = extras.getString("CLICKED_USERID");
        }else{
            Log.e(TAG, "NO BUNDLE WITH INPUT");
        }



        Log.i(TAG, "clicked contact id: "+ clickedUserId);
        Log.i(TAG, "current user id: "+ currentUserId);




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                try {
                    messageText = encryptDH(clickedUserId, messageText);
                    Log.i(TAG, "Encrypted message: " + messageText);
                } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }


                if(!messageText.equals("")){
                    message = new Message(messageText, null, currentUserId);


                    /**
                     * Sending message to server and storing it locally.
                     */
                    database.getReference().child("chat").child(currentUserId+clickedUserId).child(message.getId()).setValue(message);

                    messageDAO.insertMessage(message);
                    // database.getReference().child("users").child(clickedUserId).child("contacts").child(currentUserId).child("chat").child(message.getTime()).setValue(message);

                    addMessageBox(message.getMessage(), 2);

                    messageArea.setText("");

                }
            }
        });



        database.getReference().child("chat").child(clickedUserId+currentUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // Henter beskeder og putter dem i de rigtige messageBoxe
                message = dataSnapshot.getValue(Message.class);





                if (message != null) {
                    try {
                        Log.i(TAG, "Encrypted message: " + message.getMessage());
                        message.setMessage(decryptDH(clickedUserId, message.getMessage()));
                        Log.i(TAG, "Decrypted message: " + message.getMessage());
                    } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "message: " + message.getMessage());
                    messageDAO.insertMessage(message);
                    addMessageBox(message.getMessage(), 1);

                }

                database.getReference().child("chat").child(clickedUserId+currentUserId).child(message.getId()).removeValue();
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







    }
    public void addMessageBox(String message, int type){
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(30,18,30,20);


        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) { //left
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bubble_in);

        }
        else{
            lp2.gravity = Gravity.END; //right
            textView.setBackgroundResource(R.drawable.bubble_out);

        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public String encryptDH(String contactId, String message) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        // Key imported, obtain a reference to it.
        SecretKey keyStoreKey = (SecretKey) keyStore.getKey(contactId, null);
        // The original key can now be discarded.

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyStoreKey);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));

        return Base64.encodeToString(cipherText, Base64.DEFAULT);

    }

    public String decryptDH(String contactId, String message) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        // Key imported, obtain a reference to it.
        SecretKey keyStoreKey = (SecretKey) keyStore.getKey(contactId, null);
        // The original key can now be discarded.

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyStoreKey);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));

        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }
}
