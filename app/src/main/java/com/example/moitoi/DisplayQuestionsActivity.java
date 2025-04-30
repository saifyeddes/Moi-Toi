package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DisplayQuestionsActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestion;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private Button btnSubmitAnswer;

    private List<String> questions = new ArrayList<>();
    private List<String[]> choices = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private String quizCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_questions);

        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroupChoices = findViewById(R.id.radioGroupChoices);
        rbChoice1 = findViewById(R.id.rbChoice1);
        rbChoice2 = findViewById(R.id.rbChoice2);
        rbChoice3 = findViewById(R.id.rbChoice3);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);

        quizCode = getIntent().getStringExtra("quizCode");

        loadQuizQuestions();

        btnSubmitAnswer.setOnClickListener(v -> {
            saveAnswer();
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                Toast.makeText(DisplayQuestionsActivity.this, "Quiz terminé", Toast.LENGTH_SHORT).show();
                // Passer à la prochaine activité si nécessaire (Confirmation, etc.)
            }
        });
    }

    private void loadQuizQuestions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("questions") // Here, we're querying the collection, not a specific document
                .whereEqualTo("quizCode", quizCode) // Assuming quizCode is stored in the question documents
                .get()  // This returns a QuerySnapshot
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Check if the QuerySnapshot contains any documents
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Iterate through each document in the QuerySnapshot
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Retrieve each field from the document
                                String questionText = document.getString("question_text");
                                String option1 = document.getString("option_1");
                                String option2 = document.getString("option_2");
                                String option3 = document.getString("option_3");

                                // Add data to the lists for display
                                questions.add(questionText);
                                choices.add(new String[]{option1, option2, option3});
                            }
                            displayQuestion();  // Call the method to display the first question
                        } else {
                            Toast.makeText(DisplayQuestionsActivity.this, "Aucune question trouvée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DisplayQuestionsActivity.this, "Erreur lors de la récupération des questions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            tvQuestion.setText(questions.get(currentQuestionIndex));
            rbChoice1.setText(choices.get(currentQuestionIndex)[0]);
            rbChoice2.setText(choices.get(currentQuestionIndex)[1]);
            rbChoice3.setText(choices.get(currentQuestionIndex)[2]);
            tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1));
        }
    }

    private void saveAnswer() {
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId != -1) {
            // Récupérer la réponse sélectionnée parmi les RadioButtons
            RadioButton selectedRadioButton = findViewById(selectedId);
            // Sauvegarder la réponse sélectionnée pour cette question (vous pouvez sauvegarder dans une base de données ou une liste)
        }
    }
}
