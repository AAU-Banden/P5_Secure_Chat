package aau.itcom.group_2.p5_secure_chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import android.os.UserHandle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    String TAG = "ChatActivity";
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    static String currentUserId;
    static User currentUser;
    String clickedUserId;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();


        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        Bundle extras = intent.getExtras();
        if(extras != null) {
            currentUserId = extras.getString("CURRENT_USERID");
            clickedUserId = extras.getString("CLICKED_USERID");
        }else{
            Log.e(TAG, "NO BUNDLE WITH INPUT");
        }




        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.child(currentUserId).getValue(User.class);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });






        //reference1 = new Firebase("https://p5-chat-nibba.firebaseio.com/" + UserDetails.username + "_" + UserDetails.chatWith);
        //reference2 = new Firebase("https://p5-chat-nibba.firebaseio.com/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Message message = new Message(messageText, null, currentUserId);

                    database.getReference().child("users").child(currentUserId).child("contacts").child(clickedUserId).child("chat").setValue(message);
                    database.getReference().child("users").child(clickedUserId).child("contacts").child(currentUserId).child("chat").setValue(message);

                    messageArea.setText("");
                    /*
                    Map<String, String> map = new HashMap<>();
                    map.put("message", messageText);
                                    //Dit navn
                    map.put("user", currentUser.getName());
                    database.getReference().child("users").child(currentUserId).child("contacts")
                            .child(clickedUserId).setValue(map);
                    //reference2.push().setValue(map);
                    messageArea.setText("");

                     */
                }
            }
        });

        pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        // Loading the messages down from firebase
        database.getReference().child("users").child(currentUserId).child("contacts").child(clickedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // opretter chat i firebase hvis det ikke eksisterer
                if(!dataSnapshot.child("chat").exists()){
                    database.getReference().child("users").child(currentUserId).child("contacts").child(clickedUserId).setValue("chat");
                }
                // Henter beskeder og putter dem i de rigtige messageBoxe
                else{
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Message message = postSnapshot.child("chat").getValue(Message.class);


                        if (message!=null) {
                            if (!message.getIdOfSender().equals(currentUserId)) {
                                addMessageBox(message.getMessage(), 1);
                            } else {
                                addMessageBox(message.getMessage(), 2);
                            }
                        }
                        pd.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


/*
        database.getReference().child("users").child(currentUserId).child("contacts").child(clickedUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();



                //Modtagerens navn
                if(!userName.equals(currentUser.getName())){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
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

 */
    }


    public void addMessageBox(String message, int type){
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(30,18,30,20);


        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bubble_in);

        }
        else{
            lp2.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.bubble_out);

        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
