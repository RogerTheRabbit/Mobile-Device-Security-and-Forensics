package com.example.lab6;

import static com.example.lab6.Activity3.ALIAS_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

public class Activity2 extends AppCompatActivity {

    public static final String ACC_TYPE = "com.example.lab6.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
    }

    public void createAccount(View V) {

        String username = ((EditText) findViewById(R.id.edtUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.edtPassword)).getText().toString();

        AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account toAdd = new Account(username, ACC_TYPE);

        am.addAccountExplicitly(toAdd, password, null);

        String keyAlias = username + "_key";

        try {

            // Fetch the keystore service
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            // Check to see if alias is already in key store
            if(!ks.containsAlias(keyAlias)){
                // If the alias does nto exist, create a new key (auto-put into keystore)
                KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationParameters(120, KeyProperties.AUTH_DEVICE_CREDENTIAL)
                    .setKeySize(128)
                    .build();
                KeyGenerator kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                kg.init(keySpec);
                kg.generateKey();
            }

            // Check to see if alias is already in key store
            if(!ks.containsAlias(keyAlias)) {
                // If the alias doesn't exist, throw an error and end
                Toast.makeText(this, "Key not found", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Activity3.class);
        intent.putExtra(ALIAS_KEY, keyAlias);
        startActivity(intent);

    }

}