package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EnterQuizCodeActivity extends AppCompatActivity {

    private EditText etQuizCode;
    private Button btnSubmitCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_quiz_code);

        etQuizCode = findViewById(R.id.etQuizCode);
        btnSubmitCode = findViewById(R.id.btnSubmitCode);

        // Récupérer le code du quiz passé depuis CreateQuizActivity
        String quizCode = getIntent().getStringExtra("quizCode");

        // Action pour le bouton Soumettre
        btnSubmitCode.setOnClickListener(v -> {
            String enteredCode = etQuizCode.getText().toString();

            // Vérifier si le code correspond au code unique généré
            if (enteredCode.equals(quizCode)) {
                Intent intent = new Intent(EnterQuizCodeActivity.this, QuizConfirmationActivity.class);
                intent.putExtra("quizCode", quizCode);  // Passer le code à la page suivante
                startActivity(intent);
            } else {
                etQuizCode.setError("Code incorrect");
            }
        });
    }
}
