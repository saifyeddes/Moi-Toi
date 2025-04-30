package com.example.moitoi;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class QuizConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation);

        // Récupérer le TextView pour afficher le message de confirmation
        tvConfirmationMessage = findViewById(R.id.tvConfirmationMessage);

        // Affichage du message "Quiz soumis avec succès !"
        tvConfirmationMessage.setText("Quiz soumis avec succès !");

        // Optionnel : Sauvegarder un statut dans Firestore ou autres actions nécessaires
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MoiEtToi")
                .document("quiz_1")
                .update("quiz_status", "submitted")  // Mettre à jour un champ de statut, si nécessaire
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Statut mis à jour", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                });
    }
}
