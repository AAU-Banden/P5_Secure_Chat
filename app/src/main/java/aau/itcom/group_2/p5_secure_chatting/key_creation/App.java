package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.app.Application;
import android.content.SharedPreferences;

import aau.itcom.group_2.p5_secure_chatting.key_creation.Keys;
import androidx.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class App extends Application {

    private final static String TAG = "FIRST_TIME_RAN";
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            /**
             * Creating DH Keypair
             */
            Keys keys = new Keys();

            try {
                keys.createDHKeys();
            } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | InvalidAlgorithmParameterException | IOException | CertificateException | UnrecoverableKeyException | KeyStoreException e) {
                e.printStackTrace();
            }

            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();

            try {
                Log.i(TAG, "CREATING DH KEYS: PUBLIC KEY: " + keys.getPublicKey("DH_key_alias").toString());
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}