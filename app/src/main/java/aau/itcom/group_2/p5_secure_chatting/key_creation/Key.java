package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "keys")
public class Key {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private String keyName;
    private byte[] bytes;
    private String algorithm;



    public Key(){

    }

    @Ignore
    public Key(byte[] bytes, String algorithm, String keyName) {
        this.bytes = bytes;
        this.algorithm = algorithm;
        this.keyName = keyName;
    }

    static{
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    @Ignore
    private final static String TAG = "KEY CREATION";
    @Ignore
    private final static String ECDH_alias = "ECDH_alias";
    @Ignore
    private final static String RSA_alias = "RSA_alias";

    public KeyPair createECDHKeyPair() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        /**
         * Generating keys using SpongeCastle as provider and with general parameters for the ECDH key - "secp256r1" (p=256 bit prime)
         */

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "SC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        return keyPairGenerator.generateKeyPair();

    }

    public void createRSAKeyPairInAndroidKeyStore () throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        /**
         * Generates diffie hellman EC keys
         */

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                RSA_alias,
                KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)//Purpose is to wrap other keys that are stored in the localdatabase
                //.setAlgorithmParameterSpec(new ECParameterSpec)
                .setDigests(KeyProperties.DIGEST_SHA256,  // private key is only authorized to use either SHA256 or SHA512 for signing
                        KeyProperties.DIGEST_SHA512)
                // Only permit the private key to be used if the user authenticated
                // within the last five minutes.
                //.setUserAuthenticationRequired(true) // Authentication is required - max five minutes before signing can happen
                //.setUserAuthenticationValidityDurationSeconds(5 * 60)
                .build()); // key length
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Signing the private key using ECDSA
        //Signature signature = Signature.getInstance("SHA256withECDSA");
        //signature.initSign(keyPair.getPrivate());

    }

    public byte[] encryptPublicKeyWithRSA (byte[] publicKeyBytes) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PublicKey publicKeyFromKeyStore = keyStore.getCertificate(RSA_alias).getPublicKey();


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyFromKeyStore);

        return cipher.doFinal(publicKeyBytes);
    }

    public PublicKey decryptPublicKeyWithRSA (byte[] key, String algorithm) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(RSA_alias, null);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);


        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(cipher.doFinal(key))); // generating public key.

    }
    public String decryptPublicKeyWithRSAToString(byte[] key, String algorithm) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(RSA_alias, null);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);


        return new String(cipher.doFinal(key)); // generating public key.
    }

    public byte[] encryptPrivateKeyWithRSA (byte[] privateKeyByte) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PublicKey publicKeyFromKeyStore = keyStore.getCertificate(RSA_alias).getPublicKey();


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyFromKeyStore);

        return cipher.doFinal(privateKeyByte);
    }

    public PrivateKey decryptPrivateKeyWithRSA (byte[] key, String algorithm) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(RSA_alias, null);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);


        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(cipher.doFinal(key))); // generating private key.

    }

    public byte[] encryptSecretKeyWithRSA (SecretKey secretKey) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PublicKey publicKeyFromKeyStore = (PublicKey) keyStore.getCertificate(RSA_alias);


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyFromKeyStore);

        return cipher.doFinal(secretKey.getEncoded());
    }

    public SecretKey decryptSecretKeyWithRSA (byte[] key, String algorithm) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(RSA_alias, null);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);


        return new SecretKeySpec(key, 0, key.length, algorithm); // generating secret key.

    }


    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
