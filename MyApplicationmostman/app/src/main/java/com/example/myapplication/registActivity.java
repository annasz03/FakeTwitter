package com.example.myapplication;

import android.annotation.SuppressLint;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class registActivity extends AppCompatActivity {
    private static final String LOG_TAG = registActivity.class.getName();
    private static final String PREF_KEY = registActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    EditText editPasswordRe;
    EditText editPassword;
    EditText editEmail;
    EditText editUsername;
    private SharedPreferences preferences;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regist_activty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editPasswordRe = findViewById(R.id.editPasswordRe);
        editPassword = findViewById(R.id.editPassword);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bej);
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        constraintLayout.startAnimation(animation);

        preferences= getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName =preferences.getString("username", "");
        String password =preferences.getString("password", "");
        editUsername.setText(userName);
        editPassword.setText(password);
        editPasswordRe.setText(password);

        mauth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");
    }

    private void startTwitter(){
        Intent intent = new Intent(this, bej_activity.class);
        startActivity(intent);
    }

    public void register(View view){
        String confirm = editPasswordRe.getText().toString();
        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();
        String userName = editUsername.getText().toString();

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Nem megfelelő e-mail formátum", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "A jelszónak legalább 8 karakter hosszúnak kell lennie", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirm.equals(password)) {
            Toast.makeText(this, "A megadott jelszavak nem egyeznek meg", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(LOG_TAG, "Regisztalt: "+ userName+", jelszo: "+   password);
        Intent intent = new Intent(this, bej_activity.class);
        startActivity(intent);

        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mauth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(LOG_TAG, "Felhasználónév beállítva: " + userName);
                                    } else {
                                        Log.e(LOG_TAG, "Felhasználónév beállítása sikertelen", task1.getException());
                                    }
                                });

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", userName);
                        editor.apply();

                        Log.d(LOG_TAG, "Felhasználó regisztrálva: " + user.getUid());
                        startTwitter();
                    } else {
                        Log.e(LOG_TAG, "Felhasználó regisztrálása sikertelen", task.getException());
                        Toast.makeText(registActivity.this, "Felhasználó regisztrálása sikertelen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }




    public void cancel(View view) {
        finish();
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

    //onstop vege

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}