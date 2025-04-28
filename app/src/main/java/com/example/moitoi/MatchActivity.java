package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MatchActivity extends AppCompatActivity {
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Récupère le bouton "JOUER MAINTENANT"
        Button playNowButton = findViewById(R.id.playNowButton);
        Button aboutButton = findViewById(R.id.aboutButton);

        // Action lorsque l'utilisateur clique sur "JOUER MAINTENANT"
        playNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(MatchActivity.this,InterfaceChoix
                    .class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(v -> {
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
                    Toast.makeText(getApplicationContext(), "Ajouté avec succès dans Firestore", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE_USERS", "Erreur Firestore", e);
                });
    }
}
