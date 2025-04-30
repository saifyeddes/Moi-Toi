package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageQuestionsActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private String[] questions = new String[10];  // Stocke les 10 questions
    private String[][] choices = new String[10][3];  // Stocke les 3 réponses pour chaque question
    private String[] answers = new String[10];  // Stocke les réponses sélectionnées

    private EditText etQuestion;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private EditText etChoice1, etChoice2, etChoice3;
    private Button btnNext, btnPrevious, btnConfirm;
    private TextView tvQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        etQuestion = findViewById(R.id.etQuestion);
        radioGroupChoices = findViewById(R.id.radioGroupChoices);
        rbChoice1 = findViewById(R.id.rbChoice1);
        rbChoice2 = findViewById(R.id.rbChoice2);
        rbChoice3 = findViewById(R.id.rbChoice3);
        etChoice1 = findViewById(R.id.etChoice1);
        etChoice2 = findViewById(R.id.etChoice2);
        etChoice3 = findViewById(R.id.etChoice3);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        loadQuizData();

        btnNext.setOnClickListener(v -> {
            saveAnswer();
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                displayQuestion();
            } else {
                submitQuiz();
            }

            // Afficher le bouton "Confirmer" à partir de la deuxième question
            if (currentQuestionIndex >= 1) {
                btnConfirm.setVisibility(View.VISIBLE); // Afficher le bouton Confirmer
            }
        });

        btnPrevious.setOnClickListener(v -> {
            saveAnswer();
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            submitQuiz(); // Soumettre le quiz
        });
    }

    private void loadQuizData() {
        // Simuler le chargement des données
        questions[0] = "Question 1";
        questions[1] = "Question 2";
        choices[0][0] = "Option 1";
        choices[0][1] = "Option 2";
        choices[0][2] = "Option 3";
        choices[1][0] = "Option 1";
        choices[1][1] = "Option 2";
        choices[1][2] = "Option 3";

        displayQuestion();  // Afficher la première question
    }

    private void displayQuestion() {
        etQuestion.setText(questions[currentQuestionIndex]);
        rbChoice1.setText(choices[currentQuestionIndex][0]);
        rbChoice2.setText(choices[currentQuestionIndex][1]);
        rbChoice3.setText(choices[currentQuestionIndex][2]);
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1));
    }

    private void saveAnswer() {
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId != -1) {
            // Récupère la réponse sélectionnée parmi les RadioButtons
            RadioButton selectedRadioButton = findViewById(selectedId);
            answers[currentQuestionIndex] = selectedRadioButton.getText().toString();
        }

        // Récupère la réponse saisie dans EditText pour chaque option
        String choice1Answer = etChoice1.getText().toString();
        String choice2Answer = etChoice2.getText().toString();
        String choice3Answer = etChoice3.getText().toString();

        // Stocker les réponses dans le tableau
        answers[currentQuestionIndex] = "Choice 1: " + choice1Answer + ", Choice 2: " + choice2Answer + ", Choice 3: " + choice3Answer;
    }

    private void submitQuiz() {
        // Soumettre les réponses dans Firestore
        Map<String, Object> userResponses = new HashMap<>();
        for (int i = 0; i < answers.length; i++) {
            userResponses.put("question_" + (i + 1), answers[i]);
        }

        // Enregistrement des réponses
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document("user_id") // Ajouter un ID unique pour chaque utilisateur
                .set(userResponses)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Quiz soumis avec succès", Toast.LENGTH_SHORT).show();
                    // Passer à la confirmation ou à une autre activité
                    Intent intent = new Intent(ManageQuestionsActivity.this, QuizConfirmationActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }
}
