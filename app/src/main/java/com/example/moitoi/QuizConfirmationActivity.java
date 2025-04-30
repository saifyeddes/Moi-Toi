package com.example.moitoi;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class QuizConfirmationActivity extends AppCompatActivity {

    private TextView tvGeneratedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation);

        tvGeneratedCode = findViewById(R.id.tvGeneratedCode);

        // Récupérer le code du quiz depuis l'intent
        String quizCode = getIntent().getStringExtra("quizCode");

        // Afficher le code généré
        tvGeneratedCode.setText(quizCode);
    }
}
