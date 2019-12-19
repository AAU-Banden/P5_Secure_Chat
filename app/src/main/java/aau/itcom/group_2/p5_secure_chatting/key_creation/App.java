package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import aau.itcom.group_2.p5_secure_chatting.local_database.AppDatabase;
import aau.itcom.group_2.p5_secure_chatting.local_database.ContactDAO;
import aau.itcom.group_2.p5_secure_chatting.local_database.KeyDAO;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

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
                key.createAESKeyInAndroidKeyStore();

                KeyPair keyPair = key.createECDHKeyPair();
                Log.i(TAG, "Private ECDH: "+ Arrays.toString(keyPair.getPrivate().getEncoded()));
                Log.i(TAG, "Public ECDH: "+ Arrays.toString(keyPair.getPublic().getEncoded()));

                Object[] encryptedECDHPvKey = key.encryptKeyWithAES(keyPair.getPrivate().getEncoded());
                Object[] encryptedECDHPbKey = key.encryptKeyWithAES(keyPair.getPublic().getEncoded());
                byte[] publicKeyBytes = (byte[]) encryptedECDHPbKey[0];
                byte[] ivPbFromEncryption = (byte[]) encryptedECDHPbKey[1];
                byte[] privateKeyBytes = (byte[]) encryptedECDHPvKey[0];
                byte[] ivPvFromEncryption = (byte[]) encryptedECDHPvKey[1];

                Log.i(TAG, "Private ECDH Encrypted: "+ Arrays.toString(privateKeyBytes));
                Log.i(TAG, "Public ECDH Encrypted: "+ Arrays.toString(publicKeyBytes));

                Key keyECDHPvKey = new Key(privateKeyBytes, keyPair.getPrivate().getAlgorithm(), pvKeyName, ivPvFromEncryption);
                Key keyECDHPbKey = new Key(publicKeyBytes, keyPair.getPublic().getAlgorithm(), pbKeyName, ivPbFromEncryption);
                keyDAO.insertKey(keyECDHPvKey);
                keyDAO.insertKey(keyECDHPbKey);

            } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException | IOException | CertificateException | InvalidKeyException | KeyStoreException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnrecoverableEntryException e) {
                e.printStackTrace();
            }


            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();

        }
    }

}