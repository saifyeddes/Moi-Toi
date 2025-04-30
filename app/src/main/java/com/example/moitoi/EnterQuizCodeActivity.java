package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class EnterQuizCodeActivity extends AppCompatActivity {

    private EditText etQuizCode;
    private Button btnSubmitCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_quiz_code);

        etQuizCode = findViewById(R.id.etQuizCode);
        btnSubmitCode = findViewById(R.id.btnSubmitCode);

        btnSubmitCode.setOnClickListener(v -> {
            String quizCode = etQuizCode.getText().toString();
            verifyQuizCode(quizCode);
        });
    }

    private void verifyQuizCode(String code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(code)  // Recherche du quiz par son code
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Si le code est valide, afficher les questions
                            Intent intent = new Intent(EnterQuizCodeActivity.this, DisplayQuestionsActivity.class);
                            intent.putExtra("quizCode", code);  // Passer le code pour récupérer les questions
                            startActivity(intent);
                        } else {
                            Toast.makeText(EnterQuizCodeActivity.this, "Code invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
