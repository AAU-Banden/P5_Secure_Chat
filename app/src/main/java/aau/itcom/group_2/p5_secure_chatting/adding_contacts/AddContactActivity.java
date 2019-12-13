package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import aau.itcom.group_2.p5_secure_chatting.R;
import aau.itcom.group_2.p5_secure_chatting.create_account.User;
import aau.itcom.group_2.p5_secure_chatting.key_creation.Key;
import aau.itcom.group_2.p5_secure_chatting.local_database.AppDatabase;
import aau.itcom.group_2.p5_secure_chatting.local_database.ContactDAO;
import aau.itcom.group_2.p5_secure_chatting.local_database.KeyDAO;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;


import android.app.ProgressDialog;
import android.os.Bundle;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


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
    ProgressDialog pd;
    int counter = 0;
    int totalUsers = 0;
    static AppDatabase localDatabase;
    static ContactDAO contactDAO;
    static KeyDAO keyDAO;
    private final static String pvECDHKeyName = "ECDH_PRIVATE";
    private final static String pbECDHKeyName = "ECDH_PUBLIC";
    private final static String secretKeyName = "SECRET";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editText_addContact = findViewById(R.id.editText_addContact);
        requestList = findViewById(R.id.requestList);

        contactRequests = new ArrayList<>();

        pd = new ProgressDialog(AddContactActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        localDatabase = AppDatabase.getInstance(this);
        contactDAO = localDatabase.getContactDAO();
        keyDAO = localDatabase.getKeyDAO();




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
                        contactRequest = postSnapshot.getValue(ContactRequest.class);

                        contactRequests.add(contactRequest);

                        Log.i(TAG, "Name of contact request: " + contactRequests.get(totalUsers).getMessage());

                        totalUsers++;
                    }
                    pd.dismiss();

                    /**
                     * Hide list if no users are found
                     */
                    if (totalUsers < 1) {
                        requestList.setVisibility(View.GONE);
                        totalUsers = 0;

                    } else {
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

    public String retrievePublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        Key key = new Key();
        Key publicKeyFromRoom = keyDAO.loadKeyWithKeyName(pbECDHKeyName);
        byte[] publicKeyBytes = publicKeyFromRoom.getBytes();

        Log.i(TAG, "ENCRYPTED PUBLIC ECDH KEY: " + Base64.encodeToString(publicKeyBytes, Base64.DEFAULT));

        /*KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry("DH_key_alias", null);
        PublicKey publicKey = keyStore.getCertificate("DH_key_alias").getPublicKey();

        //getting encoded public key and turning it into a string
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyString = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

        //decoding public key
        //byte[] decodedString = Base64.decode(publicKeyString.getBytes(), Base64.DEFAULT);
        //String string = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);


         */
        String string =  key.decryptPublicKeyWithAESToString(publicKeyBytes,new GCMParameterSpec(128, publicKeyFromRoom.getIv())); // Getting encoded string
        // REMEMBER TO DECODE WHEN RECEIVING
        Log.i(TAG, "ENCODED DECRYPTED PUBLIC ECDH KEY: " + string);
        return string;

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
                    Log.i(TAG, dataSnapshot.child(currentUserId).child("name").getValue().toString());

                    currentUser = (User) dataSnapshot.child(currentUserId).getValue(User.class);

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String emailDatabase = String.valueOf(postSnapshot.child("email").getValue());
                        counter ++;
                        Log.i(TAG, emailDatabase);
                        Log.i(TAG, "typed mail: " +  email);

                        if (email.equals(currentUser.getEmail())){
                            editText_addContact.setError("Error (You cannot send a request to yourself)");
                            counter = 0;
                        } else if (email.equals(emailDatabase)) {
                            requestedID = String.valueOf(postSnapshot.child("id").getValue());


                            Log.i(TAG, "id: " + requestedID);
                            if (currentUser!=null){
                                try {
                                contact = new Contact(currentUser.getName(), currentUser.getLastName(), currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getID(), retrievePublicKey());
                                Log.i(TAG, contact.getId());
                                contactRequest = new ContactRequest(contact);
                                } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                                    e.printStackTrace();
                                }


                                database.getReference().child("users").child(requestedID).child("contactRequests")
                                        .child(contactRequest.getContactRequestID()).setValue(contactRequest);
                                Log.i(TAG, "Adding contact request to firebase");
                            }
                            editText_addContact.setText("");
                            break;
                        } else if (counter == dataSnapshot.getChildrenCount()){
                            editText_addContact.setError("No user found with this email");
                            counter= 0;
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

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = mAuth.getUid();
        contact = null;


        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(userID).getValue(User.class);
                if (user != null) {
                    try {
                        contact = new Contact(user.getName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getID(), retrievePublicKey());
                    } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "contact " + contact.getName());
                }


                /**
                 * Adding to this users contacts in local database
                 */
                contactDAO.insertContact(contactRequest.getContact());

                //database.getReference().child("contact").child(contactRequest.getContactRequestID()).setValue(contactRequest.getContact());
               // database.getReference().child("users").child(userID).child("contacts").setValue(contactRequest.getContactRequestID());

                /**
                 * Sending accept-value (contact) to sender + key - this will trigger onDataChange for the sender in ListActivity and add to contacts.
                 */
                //database.getReference().child("contact").child(userID).setValue(contact);
                database.getReference().child("users").child(contactRequest.getContactRequestID()).child("contacts").child(userID).setValue(contact);


                /**
                 * Sending key to sender
                 */
                //database.getReference().child("users").child(contactRequest.getContactRequestID()).child("contact").child((userID)).child("DH_public_key").setValue(contact.getPublicKey());



                /**
                 * Removing request
                 */
                database.getReference().child("users").child(userID).child("contactRequests").child(contactRequest.getContactRequestID()).removeValue();


                //HOTFIX: ADDING CHAT
                //database.getReference().child("users").child(userID).child("contacts").child(contactRequest.getContactRequestID()).child("chat").setValue("lalala");
                //database.getReference().child("users").child(contactRequest.getContactRequestID()).child("contacts").child(userID).child("chat").setValue("lalala");


                /**
                 * TODO: Create shared secret for encryption for this contact - save it to android keystore
                 */
                try {
                    createSharedKey(contactRequest.getContact());
                } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeySpecException | InvalidKeyException | NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
            }
            //


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void declineRequest (final ContactRequest contactRequest){
        // Removing request
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = mAuth.getUid();
        Log.i(TAG, userID);
        Log.i(TAG, contactRequest.getContactRequestID());


        database.getReference().child("users").child(userID).child("contactRequests").child(contactRequest.getContactRequestID()).removeValue();



    }

    public static void createSharedKey(Contact contact) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        //Public key from contact
        Key key = new Key();
        String keyString = contact.getPublicKey();
        byte[] decodedKey = Base64.decode(keyString.getBytes(), Base64.DEFAULT);



        Log.i(TAG, "DECODED KEY FROM CONTACT" + Arrays.toString(decodedKey));

        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "SC");
        PublicKey contactPublicKey = keyFactory.generatePublic(X509publicKey);

        //private key from user:
        Key privateKeyFromRoom = keyDAO.loadKeyWithKeyName(pvECDHKeyName);
        PrivateKey privateKey = privateKeyFromRoom.decryptPrivateKeyWithAES(privateKeyFromRoom.getBytes(), privateKeyFromRoom.getAlgorithm(), new GCMParameterSpec(128, privateKeyFromRoom.getIv()));


        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "SC");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(contactPublicKey, true);


        SecretKey secretKey = keyAgreement.generateSecret("AES");


        //putting key into roomdatabase
        Object[] encryptedKey = key.encryptKeyWithAES(secretKey.getEncoded());
        byte[] encryptedKeyBytes = (byte[]) encryptedKey[0];
        byte[] encryptedKeyIv = (byte[]) encryptedKey[1];

        Key key2 = new Key(encryptedKeyBytes, secretKey.getAlgorithm(), contact.getId(), encryptedKeyIv);

        Log.i(TAG, "SHARED KEY STORED VALUES: " + Arrays.toString(key2.getBytes()) + " " + key2.getAlgorithm() + " " + key2.getKeyName());
        keyDAO.insertKey(key2);
    }
/*

        //Private key from user
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        //KeyStore.Entry entry = keyStore.getEntry("DH_key_alias", null);
        //KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
        //PrivateKey privateKey = privateKeyEntry.getPrivateKey();


        PrivateKey privateKey = (PrivateKey) keyStore.getKey("DH_key_alias", null);


        PublicKey publicKey = keyStore.getCertificate("DH_key_alias").getPublicKey();

*/





        /* https://neilmadden.blog/2016/05/20/ephemeral-elliptic-curve-diffie-hellman-key-agreement-in-java/
        Note that it is not advisable to use the shared secret directly as a symmetric key for various reasons. In particular,
        while the derived secret is indistinguishable from a randomly selected element from the set of all possible outputs
        of the elliptic curve group, this is not the same thing as a uniformly random string of bits. Viewed as a string of
        bits, it will have some structure to it. Put another way, the P-256 curve provides roughly equivalent security to a
        128-bit secret key, yet the output shared secret is 256 bits. This reveals that the shared secret does not really
        provide 256 bits of “random” key data. There are further reasons for not using the shared secret directly, depending
        on the usage. For instance, the security considerations section of RFC 7748 advises to derive a key from the shared secret
        plus both public keys if we intend to use the key for authentication (this RFC uses different curves than we use here,
        but it is good advice regardless):

         */
        /*
        byte[] sharedSecret = keyAgreement.generateSecret();
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        hash.update(sharedSecret);

        List<ByteBuffer> keys = Arrays.asList(ByteBuffer.wrap(publicKey.getEncoded()), ByteBuffer.wrap(contactPublicKey.getEncoded()));
        Collections.sort(keys);
        hash.update(keys.get(0));
        hash.update(keys.get(1));

        byte[] derivedKey = hash.digest();

        SecretKey secretKey = new SecretKeySpec(derivedKey, "AES");


        //SecretKey secretKey = keyAgreement.generateSecret("AES");

        Log.i(TAG, "SECRETKEY: " + Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT));


        keyStore.setEntry(
                contact.getId(), //alias of the secret key
                new KeyStore.SecretKeyEntry(secretKey),
                new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

        return secretKey;

         */



}
