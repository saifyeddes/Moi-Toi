package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MatchActivity extends AppCompatActivity {
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        LinearLayout layoutMan = findViewById(R.id.layoutMan);

        layoutMan.setOnClickListener(v -> {
            Intent intent = new Intent(MatchActivity.this, InterfaceChoix.class);
            startActivity(intent);
        });


        firestore = FirebaseFirestore.getInstance();

        Map<String, Object> users = new HashMap<>();
        users.put("firstname", "EASY");
        users.put("lastname", "TUTO");
        users.put("numero", "58457112");



        firestore.collection("users").add(users)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Success (users)", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failure (users): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE_USERS", "Error adding users", e);
                });


    }
}
