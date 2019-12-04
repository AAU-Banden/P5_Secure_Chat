package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;


import androidx.security.crypto.MasterKeys;

public class Keys {

    private final static String TAG = "KEY CREATION";
    private final static String DH_alias = "DH_key_alias";

    public void createDHKeys () throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        /**
         * Generates diffie hellman EC keys
         */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                DH_alias,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY) //What they keys can be used for
                .setDigests(KeyProperties.DIGEST_SHA256,  // private key is only authorized to use either SHA256 or SHA512 for signing
                        KeyProperties.DIGEST_SHA512)
                // Only permit the private key to be used if the user authenticated
                // within the last five minutes.
                //.setUserAuthenticationRequired(true) // Authentication is required - max five minutes before signing can happen
                //.setUserAuthenticationValidityDurationSeconds(5 * 60)
                .build()); // key length
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        // Signing the private key using ECDSA
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(keyPair.getPrivate());



        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        Log.i(TAG, "Private ECDH Key: " + privateKey);
        Log.i(TAG, "Public ECDH Key: " + publicKey);


    }

    public PrivateKey getPrivateKey(String alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        // Getting instance of the keystore:
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return (PrivateKey) keyStore.getKey(alias, null);
    }

    public PublicKey getPublicKey(String alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return (PublicKey) keyStore.getCertificate(alias).getPublicKey();

    }




    /**
    private String alias = "KeyAlias";
    // https://developer.android.com/topic/security/data
    private KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
    String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

    public Keys() throws GeneralSecurityException, IOException {
    }

    public void createKeyPair (){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512).build());


            KeyPair kp = kpg.generateKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }


    }
     */
    /*
     * Generate a new EC key pair entry in the Android Keystore by
     * using the KeyPairGenerator API. The private key can only be
     * used for signing or verification and only with SHA-256 or
     * SHA-512 as the message digest.
     */

}
