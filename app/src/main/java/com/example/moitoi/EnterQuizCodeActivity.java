package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EnterQuizCodeActivity extends AppCompatActivity {

    private static final String TAG = "EnterQuizCodeActivity";
    private EditText etQuizCode;
    private Button btnSubmitCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_quiz_code);

        // Initialiser les vues
        etQuizCode = findViewById(R.id.etQuizCode);
        btnSubmitCode = findViewById(R.id.btnSubmitCode);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Gérer le clic sur le bouton Soumettre
        btnSubmitCode.setOnClickListener(v -> validateAndSubmitCode());
    }

    private void validateAndSubmitCode() {
        String quizCode = etQuizCode.getText().toString().trim();
        Log.d(TAG, "Code saisi : " + quizCode);

        // Vérifier si le champ est vide
        if (TextUtils.isEmpty(quizCode)) {
            Toast.makeText(this, "Veuillez entrer un code de quiz", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Erreur : Champ de code vide");
            return;
        }

        // Vérifier le code dans Firestore au bon emplacement
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(quizCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Code valide : " + quizCode);
                        Toast.makeText(this, "Code valide !", Toast.LENGTH_SHORT).show();

                        // Naviguer vers ManageQuestionsActivity
                        Intent intent = new Intent(EnterQuizCodeActivity.this, DisplayQuestionsActivity.class);
                        intent.putExtra("QUIZ_CODE", quizCode); // Passer le code si nécessaire
                        startActivity(intent);
                        finish(); // Terminer l'activité actuelle
                    } else {
                        Toast.makeText(this, "Code invalide. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Erreur : Code invalide - " + quizCode);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de la vérification : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Erreur Firestore : ", e);
                });
    }
}
