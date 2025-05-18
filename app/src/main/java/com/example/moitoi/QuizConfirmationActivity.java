package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "QuizConfirmationActivity";
    private Button btnReturn, btnViewAnswers, btnViewScore;
    private int score = 0;
    private int totalQuestions = 10;
    private String quizCode;
    private String responseId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation);

        // Initialize UI components
        btnReturn = findViewById(R.id.btnReturn);
        btnViewAnswers = findViewById(R.id.btnViewAnswers);
        btnViewScore = findViewById(R.id.btnViewScore);
        db = FirebaseFirestore.getInstance();

        // Retrieve quiz code and response ID
        quizCode = getIntent().getStringExtra("QUIZ_CODE");
        responseId = getIntent().getStringExtra("RESPONSE_ID");

        if (quizCode == null || responseId == null) {
            Log.e(TAG, "Missing quizCode or responseId");
            Toast.makeText(this, "Erreur: Données manquantes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Quiz code: " + quizCode + ", Response ID: " + responseId);

        // Calculate score
        calculateScore();
        
        // Setup view score button
        btnViewScore.setOnClickListener(v -> {
            if (quizCode != null && responseId != null) {
                Intent scoreIntent = new Intent(QuizConfirmationActivity.this, ScoreActivity.class);
                scoreIntent.putExtra("SCORE", score);
                scoreIntent.putExtra("TOTAL", totalQuestions);
                startActivity(scoreIntent);
            }
        });

        // Return to main menu
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(QuizConfirmationActivity.this, InterfaceChoix.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // View answers
        btnViewAnswers.setOnClickListener(v -> {
            if (quizCode != null && responseId != null) {
                Intent intent = new Intent(QuizConfirmationActivity.this, AnswersActivity.class);
                intent.putExtra("QUIZ_CODE", quizCode);
                intent.putExtra("RESPONSE_ID", responseId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Erreur: Impossible d'afficher les réponses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateScore() {
        // Fetch user answers
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_answers")
                .document(responseId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        Log.d(TAG, "User answers document: " + userDoc.getData().toString());

                        // Fetch quiz correct answers
                        db.collection("MoiEtToi")
                                .document("quiz_1")
                                .collection("user_responses")
                                .document(quizCode)
                                .get()
                                .addOnSuccessListener(quizDoc -> {
                                    if (quizDoc.exists()) {
                                        Log.d(TAG, "Quiz document: " + quizDoc.getData().toString());

                                        List<Integer> userAnswers = new ArrayList<>();
                                        List<Integer> correctAnswers = new ArrayList<>();

                                        // Set total questions
                                        totalQuestions = quizDoc.getData().size();
                                        
                                        // Load user answers
                                        for (int i = 1; i <= totalQuestions; i++) {
                                            String fieldName = "answer_" + i;
                                            Object answerObj = userDoc.get(fieldName);
                                            Log.d(TAG, "User answer field " + fieldName + ": " + answerObj);
                                            if (answerObj instanceof Long) {
                                                userAnswers.add(((Long) answerObj).intValue());
                                            } else if (answerObj instanceof String) {
                                                try {
                                                    userAnswers.add(Integer.parseInt((String) answerObj));
                                                } catch (NumberFormatException e) {
                                                    Log.e(TAG, "Error parsing user answer " + fieldName + ": " + answerObj);
                                                }
                                            }
                                        }
                                        Log.d(TAG, "User answers: " + userAnswers);

                                        // Load correct answers using "choix_<i>"
                                        for (int i = 1; i <= totalQuestions; i++) {
                                            String fieldName = "choix_" + i;
                                            Object correctObj = quizDoc.get(fieldName);
                                            Log.d(TAG, "Correct answer field " + fieldName + ": " + correctObj);
                                            if (correctObj instanceof Long) {
                                                correctAnswers.add(((Long) correctObj).intValue());
                                            } else if (correctObj instanceof String) {
                                                try {
                                                    correctAnswers.add(Integer.parseInt((String) correctObj));
                                                } catch (NumberFormatException e) {
                                                    Log.e(TAG, "Error parsing correct answer " + fieldName + ": " + correctObj);
                                                }
                                            }
                                        }
                                        Log.d(TAG, "Correct answers: " + correctAnswers);

                                        int correctCount = 0;
                                        for (int i = 0; i < userAnswers.size(); i++) {
                                            if (i < correctAnswers.size() && userAnswers.get(i).equals(correctAnswers.get(i))) {
                                                correctCount++;
                                            }
                                        }
                                        // Calculer le score sur 10
                                        score = (int) Math.round((double) correctCount / totalQuestions * 10);
                                        Log.d(TAG, "Correct answers count: " + correctCount + "/" + totalQuestions);
                                        Log.d(TAG, "Score calculated: " + score + "/10");
                                    } else {
                                        Log.e(TAG, "Quiz document not found");
                                        Toast.makeText(this, "Erreur: Quiz introuvable", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error fetching quiz data: " + e.getMessage(), e);
                                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    finish();
                                });
                    } else {
                        Log.e(TAG, "User answers document not found");
                        Toast.makeText(this, "Erreur: Réponses introuvables", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user answers: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }
}