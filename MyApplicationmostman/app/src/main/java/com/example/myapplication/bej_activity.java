package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class bej_activity extends AppCompatActivity {
    private static final String LOG_TAG= MainActivity.class.getName();
    private static final int SECRET_KEY=99;

    private FirebaseAuth mauth;
    private SharedPreferences preferences;
    private static final String PREF_KEY =MainActivity.class.getPackage().toString();

    EditText userNameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bej);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userNameET = findViewById(R.id.editUsername);
        passwordET = findViewById(R.id.editTextTextPassword);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bej);
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        constraintLayout.startAnimation(animation);

        preferences= getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mauth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }
    public void register (View view){
        Intent intent = new Intent(this, registActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }
    public void login (View view){
        String userName = userNameET.getText().toString();
        String password= passwordET.getText().toString();

        mauth.signInWithEmailAndPassword(userName, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mauth.getCurrentUser();
                        String displayName = user.getDisplayName();
                        if (displayName == null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName) // Felhasználónév beállítása
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(LOG_TAG, "Felhasználónév beállítva: " + userName);
                                        } else {
                                            Log.e(LOG_TAG, "Felhasználónév beállítása sikertelen", task1.getException());
                                        }
                                    });
                        }
                        Log.d(LOG_TAG, "Felhasználó bejelentkezett: " +user.getDisplayName());
                        startTwitter();
                    } else {
                        Log.e(LOG_TAG, "Bejelentkezés sikertelen", task.getException());
                        Toast.makeText(bej_activity.this, "Bejelentkezés sikertelen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void startTwitter(){
        Intent intent = new Intent(this, homeActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
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

    //onstop vege

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("username", userNameET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

}