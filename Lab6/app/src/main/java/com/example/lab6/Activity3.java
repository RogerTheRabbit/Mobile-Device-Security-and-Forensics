package com.example.lab6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricPrompt.PromptInfo;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class Activity3 extends AppCompatActivity {

    public static final String ALIAS_KEY = "aliasKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
    }

    public void create(View V) {

        String fileName = ((EditText)findViewById(R.id.edtFilename)).getText().toString();
        String plainText = ((EditText)findViewById(R.id.edtData)).getText().toString();
        String keyAlias = getIntent().getStringExtra(ALIAS_KEY);
        String path = getFilesDir().toString(); // /data/data/com.example.authDemo/files

        byte[] iv;
        byte[] cipherText;

        try {
            // Fetch the keystore service
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            // Check to see if alias is already in key store
            if(!ks.containsAlias(keyAlias)){
                Toast.makeText(this, "Key not found", Toast.LENGTH_LONG).show();
                return;
            }

            // Fetch the key
            SecretKey k = ((KeyStore.SecretKeyEntry) ks.getEntry(keyAlias, null)).getSecretKey();
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, k);
            iv = c.getIV();
            cipherText = c.doFinal(plainText.getBytes());
            FileOutputStream ctOut = new FileOutputStream(path + File.separator + fileName);
            for(Byte B : cipherText) {
                ctOut.write(B);
            }
            ctOut.flush();
            ctOut.close();


            // Save the Initialization Vector (IV)

            // Save the IV to a file (doesn't have to be a file)
            FileOutputStream ivOut = new FileOutputStream(path + File.separator + fileName + "_iv");
            for(Byte B : iv) {
                ivOut.write(B);
            }
            ivOut.flush();
            ivOut.close();

            ((EditText)findViewById(R.id.edtData)).setText("");

        } catch (UserNotAuthenticatedException e ) {
            Executor ex = ContextCompat.getMainExecutor(this);
            PromptInfo details = new PromptInfo.Builder()
                    .setTitle("Lab 6 App")
                    .setSubtitle("Please provide your pin, password, etc.")
                    .setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG |  // Allow the use of biometrics
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL   // Allow the use of pins/passwords/etc
                    )
                    .build();

            BiometricPrompt prompt = new BiometricPrompt(this, ex, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(), "Auth Error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(), "Auth Success", Toast.LENGTH_LONG).show();
                    create(V);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();

                    Toast.makeText(getApplicationContext(), "Auth Failure", Toast.LENGTH_LONG).show();
                }
            });
            prompt.authenticate(details);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void showFile(View V) {
        try {

            EditText edtFilename = (EditText) findViewById(R.id.edtFilename);

            String fileName = edtFilename.getText().toString();
            String keyAlias = getIntent().getStringExtra(ALIAS_KEY);
            String path = getFilesDir().toString(); // /data/data/com.example.authDemo/files

            byte[] iv;
            byte[] cipherText;

            // Fetch the keystore service
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            // Check to see if alias is already in key store
            if(!ks.containsAlias(keyAlias)) {
                // If the alias doesn't exist, throw an error and end
                Toast.makeText(this, "Key not found", Toast.LENGTH_LONG).show();
                return;
            }


            // If the alias does exist...
            // grab the key
            SecretKey k = ((KeyStore.SecretKeyEntry) ks.getEntry(keyAlias, null)).getSecretKey();
            // grab the IV
            File ivFile = new File(path + File.separator + fileName + "_iv");
            if(ivFile.exists()) {
                Path p = Paths.get(path + File.separator + fileName + "_iv");
                iv = Files.readAllBytes(p);
            } else {
                Toast.makeText(this, "IV file missing, cannot decrypt.", Toast.LENGTH_LONG).show();
                return;
            }

            // decrypt
            GCMParameterSpec params = new GCMParameterSpec(128, iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, k, params);
            Path p = Paths.get(path + File.separator + fileName);
            cipherText = Files.readAllBytes(p);
            byte[] plainText = c.doFinal(cipherText);
            String readablePlainText = new String(plainText, "UTF-8");
            ((EditText)findViewById(R.id.edtData)).setText(readablePlainText, TextView.BufferType.EDITABLE);

        } catch (UserNotAuthenticatedException e ) {
            Executor ex = ContextCompat.getMainExecutor(this);
            PromptInfo details = new PromptInfo.Builder()
                    .setTitle("Lab 6 App")
                    .setSubtitle("Please provide your pin, password, etc.")
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |  // Allow the use of biometrics
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL   // Allow the use of pins/passwords/etc
                    )
                    .build();

            BiometricPrompt prompt = new BiometricPrompt(this, ex, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(), "Auth Error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(), "Auth Success", Toast.LENGTH_LONG).show();
                    showFile(V);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();

                    Toast.makeText(getApplicationContext(), "Auth Failure", Toast.LENGTH_LONG).show();
                }
            });
            prompt.authenticate(details);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public void genNewKey(View V) {
        try {
            // Create a new key (auto-put into keystore)
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(getIntent().getStringExtra(ALIAS_KEY), KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationParameters(120, KeyProperties.AUTH_DEVICE_CREDENTIAL)
                .setKeySize(128)
                .build();
            KeyGenerator kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            kg.init(keySpec);
            kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}