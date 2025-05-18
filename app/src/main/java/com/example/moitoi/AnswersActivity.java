package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AnswersActivity extends AppCompatActivity {
    private static final String TAG = "AnswersActivity";
    private RecyclerView rvAnswers;
    private AnswersAdapter adapter;
    private List<AnswerItem> answerItems = new ArrayList<>();
    private String quizCode;
    private String responseId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        // Initialize UI components
        rvAnswers = findViewById(R.id.rvAnswers);
        Button btnShowScore = findViewById(R.id.btnShowScore);
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        adapter = new AnswersAdapter(answerItems);
        rvAnswers.setLayoutManager(new LinearLayoutManager(this));
        rvAnswers.setAdapter(adapter);

        // Retrieve quiz code and response ID from intent
        quizCode = getIntent().getStringExtra("QUIZ_CODE");
        responseId = getIntent().getStringExtra("RESPONSE_ID");

        if (quizCode == null || responseId == null) {
            Log.e(TAG, "Missing quizCode or responseId");
            Toast.makeText(this, "Erreur: Données manquantes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load answers
        loadAnswers();

        // Set up button to show score
        btnShowScore.setOnClickListener(v -> {
            Intent intent = new Intent(AnswersActivity.this, QuizConfirmationActivity.class);
            intent.putExtra("QUIZ_CODE", quizCode);
            intent.putExtra("RESPONSE_ID", responseId);
            startActivity(intent);
            finish();
        });
    }

    private void loadAnswers() {
        // Fetch user answers
        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_answers")
                .document(responseId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        // Fetch quiz correct answers
                        db.collection("MoiEtToi")
                                .document("quiz_1")
                                .collection("user_responses")
                                .document(quizCode)
                                .get()
                                .addOnSuccessListener(quizDoc -> {
                                    if (quizDoc.exists()) {
                                        answerItems.clear();
                                        
                                        // Process each question
                                        for (int i = 1; i <= 10; i++) {
                                            String userAnswerField = "answer_" + i;
                                            String correctAnswerField = "choix_" + i;
                                            
                                            Object userAnswerObj = userDoc.get(userAnswerField);
                                            Object correctAnswerObj = quizDoc.get(correctAnswerField);
                                            
                                            if (userAnswerObj != null && correctAnswerObj != null) {
                                                int userAnswer = 0;
                                                int correctAnswer = 0;
                                                
                                                // Parse user answer
                                                if (userAnswerObj instanceof Long) {
                                                    userAnswer = ((Long) userAnswerObj).intValue();
                                                } else if (userAnswerObj instanceof String) {
                                                    try {
                                                        userAnswer = Integer.parseInt((String) userAnswerObj);
                                                    } catch (NumberFormatException e) {
                                                        Log.e(TAG, "Error parsing user answer " + i, e);
                                                        continue;
                                                    }
                                                }
                                                
                                                // Parse correct answer
                                                if (correctAnswerObj instanceof Long) {
                                                    correctAnswer = ((Long) correctAnswerObj).intValue();
                                                } else if (correctAnswerObj instanceof String) {
                                                    try {
                                                        correctAnswer = Integer.parseInt((String) correctAnswerObj);
                                                    } catch (NumberFormatException e) {
                                                        Log.e(TAG, "Error parsing correct answer " + i, e);
                                                        continue;
                                                    }
                                                }
                                                
                                                // Convert to letters (A=1, B=2, etc.)
                                                String userAnswerStr = String.valueOf((char) ('A' + userAnswer - 1));
                                                String correctAnswerStr = String.valueOf((char) ('A' + correctAnswer - 1));
                                                
                                                // Create answer item
                                                AnswerItem item = new AnswerItem(
                                                        i,
                                                        userAnswerStr,
                                                        correctAnswerStr,
                                                        userAnswer == correctAnswer
                                                );
                                                answerItems.add(item);
                                            }
                                        }
                                        
                                        // Update the adapter
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, "Quiz document not found");
                                        Toast.makeText(this, "Erreur: Quiz introuvable", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error fetching quiz data: " + e.getMessage(), e);
                                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Log.e(TAG, "User answers document not found");
                        Toast.makeText(this, "Erreur: Réponses introuvables", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user answers: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
