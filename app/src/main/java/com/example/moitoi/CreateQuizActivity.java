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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateQuizActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private String[] questions = new String[10];  // Stocke les 10 questions
    private String[][] choices = new String[10][3];  // Stocke les 3 réponses pour chaque question
    private String[] answers = new String[10];  // Stocke les réponses sélectionnées
    private String quizCode;  // Variable pour stocker le code unique du quiz

    private EditText etQuestion;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private EditText etChoice1, etChoice2, etChoice3;
    private Button btnNext;
    private TextView tvQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        etQuestion = findViewById(R.id.etQuestion);
        radioGroupChoices = findViewById(R.id.radioGroupChoices);
        rbChoice1 = findViewById(R.id.rbChoice1);
        rbChoice2 = findViewById(R.id.rbChoice2);
        rbChoice3 = findViewById(R.id.rbChoice3);
        etChoice1 = findViewById(R.id.etChoice1);
        etChoice2 = findViewById(R.id.etChoice2);
        etChoice3 = findViewById(R.id.etChoice3);
        btnNext = findViewById(R.id.btnNext);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        loadQuizData();

        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                saveAnswer();  // Sauvegarder la réponse actuelle
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.length) {
                    displayQuestion();
                } else {
                    submitQuiz();
                }
            } else {
                Toast.makeText(CreateQuizActivity.this, "Veuillez remplir tous les champs et sélectionner une réponse correcte.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuizData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                questions[currentQuestionIndex] = document.getString("question_text");
                                choices[currentQuestionIndex][0] = document.getString("option_1");
                                choices[currentQuestionIndex][1] = document.getString("option_2");
                                choices[currentQuestionIndex][2] = document.getString("option_3");
                            }
                        }
                        displayQuestion();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erreur de chargement des questions", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private boolean validateFields() {
        // Vérifier si la question est remplie
        if (etQuestion.getText().toString().isEmpty()) {
            return false;
        }

        // Vérifier si une réponse est sélectionnée
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return false;
        }

        // Vérifier si tous les choix sont remplis
        if (etChoice1.getText().toString().isEmpty() || etChoice2.getText().toString().isEmpty() || etChoice3.getText().toString().isEmpty()) {
            return false;
        }

        return true; // Si tout est validé, retourner true
    }

    private void submitQuiz() {
        quizCode = UUID.randomUUID().toString();  // Code unique pour ce quiz

        Map<String, Object> userResponses = new HashMap<>();
        for (int i = 0; i < answers.length; i++) {
            userResponses.put("question_" + (i + 1), answers[i]);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(quizCode)  // Utiliser le code unique comme ID du document
                .set(userResponses)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(CreateQuizActivity.this, EnterQuizCodeActivity.class);
                    intent.putExtra("quizCode", quizCode);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }
}
