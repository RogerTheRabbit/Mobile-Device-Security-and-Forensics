package com.example.lab6;

import static com.example.lab6.Activity3.ALIAS_KEY;

import androidx.appcompat.app.AppCompatActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
    }


    public void login(View V) {
        String username = ((EditText) findViewById(R.id.edtUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.edtPassword)).getText().toString();

        AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = am.getAccountsByType(Activity2.ACC_TYPE);

        for (Account account: accounts) {
            if(account.name.compareTo(username) == 0) {
                if(am.getPassword(account).compareTo(password) == 0) {
                    Intent intent = new Intent(this, Activity3.class);
                    intent.putExtra(ALIAS_KEY, username + "_key");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "LOGIN FAILED: The password does not match", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        Toast.makeText(this, "LOGIN FAILED: No account found", Toast.LENGTH_LONG).show();
    }

    public void register(View V) {
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }
}