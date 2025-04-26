package com.example.moitoi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RepondreQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repondrequiz); // Assure-toi que ton fichier XML s'appelle bien repondrequiz.xml

        // Initialisation du bouton "Retour"
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ferme l'activité actuelle pour revenir à l'activité précédente
                finish();
            }
        });
    }
}
