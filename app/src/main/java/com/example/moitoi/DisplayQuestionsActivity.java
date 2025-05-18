package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayQuestionsActivity extends AppCompatActivity {

    private static final String TAG = "DisplayQuestionsActivity";
    private TextView tvQuestionNumber, tvQuestion;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private Button btnSubmitAnswer;

    private List<String> questions = new ArrayList<>();
    private List<String[]> choices = new ArrayList<>();
    private List<Integer> userAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private String quizCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_display_questions);
            Log.d(TAG, "DisplayQuestionsActivity layout set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting layout: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation de l'écran", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Configure Toolbar (make it optional)
        try {
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Quiz");
                    Log.d(TAG, "Toolbar configured successfully");
                } else {
                    Log.w(TAG, "getSupportActionBar returned null; action bar not available");
                }
            } else {
                Log.w(TAG, "Toolbar not found in layout; proceeding without toolbar");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configuring toolbar: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur de configuration de la barre d'outils", Toast.LENGTH_LONG).show();
            // Do NOT finish the activity; proceed to display questions
        }

        // Initialize UI components
        try {
            tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
            tvQuestion = findViewById(R.id.tvQuestion);
            radioGroupChoices = findViewById(R.id.radioGroupChoices);
            rbChoice1 = findViewById(R.id.rbChoice1);
            rbChoice2 = findViewById(R.id.rbChoice2);
            rbChoice3 = findViewById(R.id.rbChoice3);
            btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
            Log.d(TAG, "UI components initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur d'initialisation des composants", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Retrieve quiz code from intent
        quizCode = getIntent().getStringExtra("quizCode");
        if (quizCode == null || quizCode.isEmpty()) {
            Log.e(TAG, "Missing or empty quizCode");
            Toast.makeText(this, "Erreur: Code de quiz manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Quiz code received: " + quizCode);

        // Load questions from Firestore
        loadQuizQuestions();

        // Handle submit answer button
        btnSubmitAnswer.setOnClickListener(v -> {
            if (saveAnswer()) {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    displayQuestion();
                } else {
                    submitAnswers();
                }
            }
        });
    }

    private void loadQuizQuestions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Loading questions for quizCode: " + quizCode);

        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(quizCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Firestore query completed");
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Quiz document found");
                        questions.clear();
                        choices.clear();

                        // Load up to 10 questions
                        for (int i = 1; i <= 10; i++) {
                            String questionText = documentSnapshot.getString("question_" + i);
                            String option1 = documentSnapshot.getString("reponse_" + i + "_1");
                            String option2 = documentSnapshot.getString("reponse_" + i + "_2");
                            String option3 = documentSnapshot.getString("reponse_" + i + "_3");

                            // Only add valid questions with all required fields
                            if (questionText != null && !questionText.isEmpty() &&
                                    option1 != null && !option1.isEmpty() &&
                                    option2 != null && !option2.isEmpty() &&
                                    option3 != null && !option3.isEmpty()) {
                                questions.add(questionText);
                                choices.add(new String[]{option1, option2, option3});
                            }
                        }

                        if (!questions.isEmpty()) {
                            Log.d(TAG, "Loaded " + questions.size() + " valid questions");
                            displayQuestion();
                        } else {
                            Log.e(TAG, "No valid questions found");
                            Toast.makeText(DisplayQuestionsActivity.this, "Aucune question valide trouvée", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Quiz document not found for code: " + quizCode);
                        Toast.makeText(DisplayQuestionsActivity.this, "Quiz introuvable", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading questions: " + e.getMessage(), e);
                    Toast.makeText(DisplayQuestionsActivity.this, "Erreur lors de la récupération des questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            tvQuestion.setText(questions.get(currentQuestionIndex));
            rbChoice1.setText(choices.get(currentQuestionIndex)[0]);
            rbChoice2.setText(choices.get(currentQuestionIndex)[1]);
            rbChoice3.setText(choices.get(currentQuestionIndex)[2]);
            tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1));
            radioGroupChoices.clearCheck();
            btnSubmitAnswer.setText(currentQuestionIndex == questions.size() - 1 ? "Terminer" : "Suivant");
            Log.d(TAG, "Displaying question " + (currentQuestionIndex + 1));
        }
    }

    private boolean saveAnswer() {
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Log.d(TAG, "No answer selected for question " + (currentQuestionIndex + 1));
            Toast.makeText(this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
            return false;
        }

        int selectedIndex;
        if (selectedId == R.id.rbChoice1) {
            selectedIndex = 1;
        } else if (selectedId == R.id.rbChoice2) {
            selectedIndex = 2;
        } else {
            selectedIndex = 3;
        }
        userAnswers.add(selectedIndex);
        Log.d(TAG, "Saved answer for question " + (currentQuestionIndex + 1) + ": " + selectedIndex);
        return true;
    }

    private void submitAnswers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> responseData = new HashMap<>();

        for (int i = 0; i < userAnswers.size(); i++) {
            responseData.put("answer_" + (i + 1), userAnswers.get(i));
        }
        responseData.put("quizCode", quizCode);

        String responseId = String.valueOf((int) (Math.random() * 900000) + 100000);
        Log.d(TAG, "Submitting answers with responseId: " + responseId);
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_answers")
                .document(responseId)
                .set(responseData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Answers submitted successfully");
                    Toast.makeText(DisplayQuestionsActivity.this, "Réponses soumises avec succès", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DisplayQuestionsActivity.this, QuizConfirmationActivity.class);
                    intent.putExtra("quizCode", quizCode);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error submitting answers: " + e.getMessage(), e);
                    Toast.makeText(DisplayQuestionsActivity.this, "Erreur lors de l'enregistrement des réponses: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Back button pressed");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}