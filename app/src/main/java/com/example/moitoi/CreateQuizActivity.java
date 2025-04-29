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

import com.example.moitoi.QuizConfirmationActivity;
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

    private EditText etQuestion;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private Button btnNext;
    private TextView tvQuestionNumber;
    String userId = UUID.randomUUID().toString(); // Génère un identifiant unique pour chaque utilisateur

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        etQuestion = findViewById(R.id.etQuestion);
        radioGroupChoices = findViewById(R.id.radioGroupChoices);
        rbChoice1 = findViewById(R.id.rbChoice1);
        rbChoice2 = findViewById(R.id.rbChoice2);
        rbChoice3 = findViewById(R.id.rbChoice3);
        btnNext = findViewById(R.id.btnNext);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        // Charger les questions depuis Firestore
        loadQuizData();

        // Bouton suivant pour passer à la question suivante
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sauvegarder la réponse actuelle
                saveAnswer();

                // Passer à la question suivante
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.length) {
                    displayQuestion();
                } else {
                    // Fin du quiz, passer à la prochaine activité
                    submitQuiz();
                }
            }
        });
    }

    // Fonction pour charger les questions depuis Firestore
    private void loadQuizData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Accéder à la collection des quizzes
        db.collection("MoiEtToi")
                .document("quiz_1")  // Remplace par l'ID du quiz
                .collection("questions")  // Sous-collection des questions
                .get()  // Récupérer toutes les questions
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Récupère les questions et options depuis Firestore
                                questions[currentQuestionIndex] = document.getString("question_text");
                                choices[currentQuestionIndex][0] = document.getString("option_1");
                                choices[currentQuestionIndex][1] = document.getString("option_2");
                                choices[currentQuestionIndex][2] = document.getString("option_3");
                            }
                        }
                        displayQuestion();  // Afficher la première question
                    } else {
                        Toast.makeText(getApplicationContext(), "Erreur de chargement des questions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Affiche la question actuelle
    private void displayQuestion() {
        etQuestion.setText(questions[currentQuestionIndex]);
        rbChoice1.setText(choices[currentQuestionIndex][0]);
        rbChoice2.setText(choices[currentQuestionIndex][1]);
        rbChoice3.setText(choices[currentQuestionIndex][2]);
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1));
    }

    // Sauvegarde la réponse sélectionnée
    private void saveAnswer() {
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            answers[currentQuestionIndex] = selectedRadioButton.getText().toString();
        }
    }

    // Soumettre le quiz et enregistrer les réponses dans Firestore
    // Fonction pour soumettre le quiz et enregistrer les réponses dans Firestore
    private void submitQuiz() {
        // Crée une map avec les réponses
        Map<String, Object> userResponses = new HashMap<>();
        for (int i = 0; i < answers.length; i++) {
            userResponses.put("question_" + (i + 1), answers[i]);
        }

        // Initialisation de Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Créer un document parent (par exemple 'quiz_1')
        // Ajouter une sous-collection 'user_responses' sous ce document
        db.collection("MoiEtToi")  // Référence à la base 'moiettoi'
                .document("quiz_1")  // Document parent (ex. quiz_1)
                .collection("user_responses")  // Sous-collection des réponses des utilisateurs
                .document(userId)  // Créer un document pour chaque utilisateur avec un ID unique
                .set(userResponses)  // Utilise 'set()' pour enregistrer les données dans ce document
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Quiz soumis avec succès", Toast.LENGTH_SHORT).show();
                    // Naviguer vers l'écran suivant (confirmation ou autre)
                    Intent intent = new Intent(CreateQuizActivity.this, QuizConfirmationActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }

}
