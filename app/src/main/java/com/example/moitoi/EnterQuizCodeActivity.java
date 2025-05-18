package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EnterQuizCodeActivity extends AppCompatActivity {

    private static final String TAG = "EnterQuizCodeActivity";
    private EditText etQuizCode;
    private Button btnSubmitCode, btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_enter_quiz_code);
            Log.d(TAG, "Activity layout set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting layout: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation de l'écran", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        try {
            etQuizCode = findViewById(R.id.etQuizCode);
            btnSubmitCode = findViewById(R.id.btnSubmitCode);
            btnBack = findViewById(R.id.btnBack);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation des composants", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Firestore
        try {
            db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firestore initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firestore: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation de la base de données", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Handle Submit button click
        btnSubmitCode.setOnClickListener(v -> validateAndSubmitCode());

        // Handle Back button click
        btnBack.setOnClickListener(v -> navigateToInterfaceChoix());
    }

    private void navigateToInterfaceChoix() {
        Log.d(TAG, "Navigating to InterfaceChoix");
        try {
            Intent intent = new Intent(EnterQuizCodeActivity.this, InterfaceChoix.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d(TAG, "Started InterfaceChoix activity");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to InterfaceChoix: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors du retour à l'écran principal", Toast.LENGTH_LONG).show();
        }
    }

    private void validateAndSubmitCode() {
        String quizCode = etQuizCode.getText().toString().trim();
        Log.d(TAG, "Code entered: " + quizCode);

        // Check if the field is empty
        if (TextUtils.isEmpty(quizCode)) {
            Toast.makeText(this, "Veuillez entrer un code de quiz", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error: Empty code field");
            return;
        }

        // Verify the code in Firestore
        Log.d(TAG, "Querying Firestore for quiz code: " + quizCode);
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(quizCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Firestore query completed");
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Valid code found: " + quizCode);
                        Toast.makeText(this, "Code valide !", Toast.LENGTH_SHORT).show();

                        // Navigate to DisplayQuestionsActivity
                        try {
                            Intent intent = new Intent(EnterQuizCodeActivity.this, DisplayQuestionsActivity.class);
                            intent.putExtra("quizCode", quizCode);
                            startActivity(intent);
                            Log.d(TAG, "Started DisplayQuestionsActivity with quizCode: " + quizCode);
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Error starting DisplayQuestionsActivity: " + e.getMessage(), e);
                            Toast.makeText(this, "Erreur lors du chargement des questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "Invalid code: " + quizCode);
                        Toast.makeText(this, "Code invalide. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                        // Do NOT navigate to InterfaceChoix here; stay on this screen
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore query failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur lors de la vérification: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Do NOT navigate to InterfaceChoix here; stay on this screen
                });
    }
}