package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Base64;

import aau.itcom.group_2.p5_secure_chatting.local_database.AppDatabase;
import aau.itcom.group_2.p5_secure_chatting.local_database.ContactDAO;
import aau.itcom.group_2.p5_secure_chatting.local_database.KeyDAO;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class App extends Application {

    private final static String TAG = "FIRST_TIME_RAN";
    AppDatabase localDatabase;
    KeyDAO keyDAO;
    private final static String pvKeyName = "ECDH_PRIVATE";
    private final static String pbKeyName = "ECDH_PUBLIC";


    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            localDatabase = AppDatabase.getInstance(this);
            keyDAO = localDatabase.getKeyDAO();
            /**
             * Creating ECDH Keypair
             */
            Key key = new Key();

            try {
                key.createRSAKeyPairInAndroidKeyStore();
                KeyPair keyPair = key.createECDHKeyPair();


                byte[] encryptedECDHPvKey = key.encryptPrivateKeyWithRSA(keyPair.getPrivate().getEncoded());
                byte[] encryptedECDHPbKey = key.encryptPublicKeyWithRSA(keyPair.getPublic().getEncoded());

                Key keyECDHPvKey = new Key(encryptedECDHPvKey, keyPair.getPrivate().getAlgorithm(), pvKeyName);
                Key keyECDHPbKey = new Key(encryptedECDHPbKey, keyPair.getPublic().getAlgorithm(), pbKeyName);

                keyDAO.insertKey(keyECDHPvKey);
                keyDAO.insertKey(keyECDHPbKey);

            } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException | IOException | CertificateException | UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }


            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();

        }
    }

}