package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = registActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.i(LOG_TAG, "onCreate");

    }

    public void register (View view){
        Intent intent = new Intent(this, registActivity.class);
        startActivity(intent);
    }
    public void login (View view){

        Intent intent = new Intent(this, bej_activity.class);
        startActivity(intent);
    }

    //on start

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        if (isUserLoggedIn()) {
            startMainFunctionality();
        } else {
            startLoginActivity();
        }
    }
    private boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void startMainFunctionality() {
        Log.i(LOG_TAG, "Felhasználó be van jelentkezve. Az alkalmazás fő tevékenysége elindítva.");
    }

    private void startLoginActivity() {
        Log.i(LOG_TAG, "Felhasználó nincs bejelentkezve. A bejelentkezési tevékenység elindítva.");
    }

    //onstart vege



    //onstop

    @Override
    protected void onStop() {
        super.onStop();
        if (isUserLoggedOut()) {
            stopActiveServices();
        }
    }

    private boolean isUserLoggedOut() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    private void stopActiveServices() {
        Log.i(LOG_TAG, "Felhasználó kijelentkezett. Aktív szolgáltatások leállítva.");
    }

    //onstop
}