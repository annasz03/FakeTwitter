package com.example.myapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class profileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mauth;

    private TextView felhasznalonev;

    private RecyclerView recycleview;
    private ArrayList<Tweet> tweetek;
    private TweetAdapter mAdapter;
    private int gridNumber=1;

    private FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();
    private CollectionReference mTweets=mFirestore.collection("Tweets");

    private static final String LOG_TAG= profileActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        felhasznalonev = findViewById(R.id.felhnev);
        felhasznalonev.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        mauth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            Log.d(LOG_TAG, "Authenticated user");
        }else {
            Log.d(LOG_TAG, "UNAuthenticated user");
            finish();
        }

        recycleview= findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new GridLayoutManager(this, gridNumber));
        tweetek= new ArrayList<>();
        mAdapter= new TweetAdapter(this, tweetek);
        recycleview.setAdapter(mAdapter);

        querydata();
        initializeDataIfNeeded();
    }
    private void querydata() {
        new Thread(() -> {
            mTweets.whereEqualTo("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    .orderBy("id", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Tweet tweet = document.toObject(Tweet.class);
                            tweetek.add(tweet);
                        }
                        if (tweetek.isEmpty()) {
                            Log.d(LOG_TAG, "Nincs találat a lekérdezésben.");
                        }
                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Hiba történt a lekérdezés során", e);
                    });
        }).start();
    }

    private void initializeDataIfNeeded() {
        mTweets.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                intializeData();
            }
        }).addOnFailureListener(e -> {
            Log.e(LOG_TAG, "Sikertelen lekérdezés az adatbázisról", e);
        });
    }

    private void intializeData() {
    }

    public void back (View view){
        Intent intent = new Intent(this,homeActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }

    public void logout(View view) {
        mauth.signOut();
        Intent intent = new Intent(this, bej_activity.class);
        startActivity(intent);
        finish();
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

    //onstart

    //onstop

    @Override
    protected void onStop() {
        super.onStop();
        if (isUserLoggedOut()) {
            stopActiveServices();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isUserLoggedOut() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    private void stopActiveServices() {
        Log.i(LOG_TAG, "Felhasználó kijelentkezett. Aktív szolgáltatások leállítva.");
    }

    //onstop vege

    public void torles(View view) {
        int position = recycleview.getChildLayoutPosition((View) view.getParent().getParent());

        if (position != RecyclerView.NO_POSITION) {
            Tweet torlendoTweet = tweetek.get(position);
            tweetek.remove(position);

            mAdapter.notifyItemRemoved(position);
            new Thread(() -> {
                String id = String.valueOf(torlendoTweet.getId());
                mTweets.document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(LOG_TAG, "Tweet sikeresen törölve az adatbázisból.");
                            Toast.makeText(this, "Tweet sikeresen törölve", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(LOG_TAG, "Hiba történt a Tweet törlése közben az adatbázisból.", e);

                            tweetek.add(position, torlendoTweet);
                            mAdapter.notifyItemInserted(position);
                        });
            }).start();
        }
    }


    public void update(View view, String newText) {
        int position = recycleview.getChildLayoutPosition((View) view.getParent().getParent());
        if (position != RecyclerView.NO_POSITION) {
            Tweet updateTweet = tweetek.get(position);

            updateTweet.setTest(newText);

            new Thread(() -> {
                String id = String.valueOf(updateTweet.getId());
                mTweets.document(id)
                        .set(updateTweet)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(LOG_TAG, "Tweet sikeresen frissítve az adatbázisban.");
                            Toast.makeText(this, "Tweet sikeresen frissítve", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(LOG_TAG, "Hiba történt a Tweet frissítése közben az adatbázisban.", e);

                            tweetek.set(position, updateTweet);

                            mAdapter.notifyItemChanged(position);
                        });
            }).start();
        }
    }
    public void showUpdateDialog(View view) {
        int position = recycleview.getChildLayoutPosition((View) view.getParent().getParent());

        if (position != RecyclerView.NO_POSITION) {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.update, null);
                EditText editTextTweet = dialogView.findViewById(R.id.editTextTweet);
                builder.setView(dialogView);
                builder.setPositiveButton("Frissítés", (dialog, which) -> {
                    String newText = editTextTweet.getText().toString().trim();
                    if (!newText.isEmpty()) {
                        update(view, newText);
                    } else {
                        Toast.makeText(this, "Kérjük, írja be a tweetet", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Mégse", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }

    public void liking(View view) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) recycleview.findContainingViewHolder(view);
        int position = holder.getAdapterPosition();

        if (position != RecyclerView.NO_POSITION) {
            Tweet likedTweet = tweetek.get(position);

            if (!likedTweet.isLiked()) {
                likedTweet.setLike(likedTweet.getLike() + 1);
                likedTweet.setLiked(true);

                new Thread(() -> {
                    String id = String.valueOf(likedTweet.getId());
                    mTweets.document(id)
                            .set(likedTweet)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(LOG_TAG, "Tweet sikeresen like-olva az adatbázisban.");
                                runOnUiThread(() -> mAdapter.notifyItemChanged(position));
                            })
                            .addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Hiba történt a Tweet like-olása közben az adatbázisban.", e);
                                likedTweet.setLike(likedTweet.getLike() - 1);
                                likedTweet.setLiked(false);
                                runOnUiThread(() -> mAdapter.notifyItemChanged(position));
                            });
                }).start();
            } else {
                Toast.makeText(this, "Ezt a tweetet már likeolta!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
