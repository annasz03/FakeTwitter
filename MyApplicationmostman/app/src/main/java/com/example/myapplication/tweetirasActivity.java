package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class tweetirasActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mauth;
    private EditText tweetEditText;
    private static final String LOG_TAG = tweetirasActivity.class.getName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tweetsCollection = db.collection("Tweets");
    public int id;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tweetiras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tweetEditText = findViewById(R.id.beiras);

        mauth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "UNAuthenticated user");
            finish();
        }
    }

    public void back(View view) {
        Intent intent = new Intent(this, homeActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }

    public void kozzetetel(View view) {
        String tweetText = tweetEditText.getText().toString().trim();
        if (!tweetText.isEmpty()) {
            new Thread(() -> addTweetToDatabase(tweetText)).start();
            Toast.makeText(this, "Tweet közzétéve", Toast.LENGTH_SHORT).show();
            tweetEditText.setText("");
            Intent intent = new Intent(this, homeActivity.class);
            intent.putExtra("SECRET_KEY", 99);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Kérjük, írja be a tweetet", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTweetToDatabase(String tweetText) {
        FirebaseUser currentUser = mauth.getCurrentUser();
        String displayName = currentUser != null ? currentUser.getDisplayName() : "Unknown";

        tweetsCollection
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int latestId = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Tweet tweet = document.toObject(Tweet.class);
                        latestId = tweet.getId();
                    }

                    int newId = latestId + 1;

                    Map<String, Object> tweetData = new HashMap<>();
                    tweetData.put("comment", 0);
                    tweetData.put("id", newId);
                    tweetData.put("like", 0);
                    tweetData.put("name", displayName);
                    tweetData.put("retweet", 0);
                    tweetData.put("test", tweetText);

                    if (selectedImageUri != null) {
                        String imageUriString = selectedImageUri.toString();
                        tweetData.put("imageresource", imageUriString);
                    }

                    String documentName = String.valueOf(newId);

                    tweetsCollection.document(documentName)
                            .set(tweetData)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(LOG_TAG, "Tweet hozzáadva az adatbázishoz: ");
                                sendNotification(tweetText); // Értesítés küldése
                            })
                            .addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Hiba történt a tweet hozzáadása közben", e);
                                Toast.makeText(this, "Hiba történt a tweet közzététele közben", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Hiba történt az utolsó tweet lekérdezése közben", e);
                    Toast.makeText(this, "Hiba történt az utolsó tweet lekérdezése közben", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotification(String tweetText) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.logo_of_twitter_svg)
                .setContentTitle("Új tweet")
                .setContentText(tweetText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
            }
        }
    }
}
