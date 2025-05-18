package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class InterfaceChoix extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interfacechoix); // adapte au bon nom de fichier

        // Clic sur la carte "Créer un Quiz"
        LinearLayout btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
        btnCreateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action pour "Créer un Quiz", par exemple ouvrir une autre activité
                Intent intent = new Intent(InterfaceChoix.this, CreateQuizActivity.class); // Remplace par l'activité réelle
                startActivity(intent);
            }
        });

        // Clic sur la carte "Répondre au Quiz"
        LinearLayout btnAnswerQuiz = findViewById(R.id.btnAnswerQuiz);
        if (btnAnswerQuiz != null) {
            btnAnswerQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("InterfaceChoix", "Clic sur Répondre au Quiz");
                    try {
                        // Action pour "Répondre au Quiz"
                        Intent intent = new Intent(InterfaceChoix.this, EnterQuizCodeActivity.class);
                        startActivity(intent);
                        android.util.Log.d("InterfaceChoix", "Intent lancé avec succès");
                    } catch (Exception e) {
                        android.util.Log.e("InterfaceChoix", "Erreur lors du lancement de l'activité", e);
                    }
                }
            });
        } else {
            android.util.Log.e("InterfaceChoix", "btnAnswerQuiz est null");
        }
    }
}
