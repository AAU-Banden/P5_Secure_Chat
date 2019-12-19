package aau.itcom.group_2.p5_secure_chatting.key_creation;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ser.Serializers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
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
    private byte[] iv;



    public Key(){

    }

    @Ignore
    public Key(byte[] bytes, String algorithm, String keyName, byte[] iv) {
        this.bytes = bytes;
        this.algorithm = algorithm;
        this.keyName = keyName;
        this.iv = iv;
    }

    static{
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    @Ignore
    private final static String TAG = "KEY CREATION";
    @Ignore
    private final static String ECDH_alias = "ECDH_alias";
    @Ignore
    private final static String AES_alias = "AES_alias";

    public KeyPair createECDHKeyPair() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        /**
         * Generating keys using SpongeCastle as provider and with general parameters for the ECDH key - "secp256r1" (p=256 bit prime)
         */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "SC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        return keyPairGenerator.generateKeyPair();

    }

    public void createAESKeyInAndroidKeyStore () throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        /**
         * AES key generation
         */
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder(AES_alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build());
        SecretKey secretKey = keyGenerator.generateKey();

        Log.i(TAG, "AES KEY: " + String.valueOf(secretKey));

    }

    private SecretKey getAESKeyFromAKS() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(AES_alias, null);

        return secretKeyEntry.getSecretKey();

    }

    public Object[] encryptKeyWithAES (byte[] keyBytes) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnrecoverableEntryException, InvalidAlgorithmParameterException {
        SecretKey secretKey = getAESKeyFromAKS();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new Object[]{cipher.doFinal(keyBytes), cipher.getIV()};

    }

    public PublicKey decryptPublicKeyWithAES (byte[] key, String algorithm, GCMParameterSpec gcmParameterSpec) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        SecretKey secretKey = getAESKeyFromAKS();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(cipher.doFinal(key))); // generating public key.

    }

    public String decryptPublicKeyWithAESToString(byte[] key, GCMParameterSpec gcmParameterSpec) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKey secretKey = getAESKeyFromAKS();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] cipherText = cipher.doFinal(key);


        Log.i(TAG, "666DECRYPTED PBKEY: " + Arrays.toString(cipherText));

        return Base64.encodeToString(cipherText, Base64.DEFAULT); // We encode it to string, when it is received it needs to be decoded.
    }


    public PrivateKey decryptPrivateKeyWithAES (byte[] key, String algorithm, GCMParameterSpec gcmParameterSpec) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        SecretKey secretKey = getAESKeyFromAKS();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(cipher.doFinal(key))); // generating private key.

    }

    public SecretKey decryptSecretKeyWithAES (byte[] key, String algorithm, GCMParameterSpec gcmParameterSpec) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        SecretKey secretKey = getAESKeyFromAKS();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);



        return new SecretKeySpec(cipher.doFinal(key), 0, 16, algorithm); // generating secret key.

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

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
