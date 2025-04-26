package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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

        // Initialisation des questions et des choix
        loadQuestionData();

        // Affiche la première question
        displayQuestion();

        // Bouton suivant pour passer à la question suivante
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sauvegarder la réponse actuelle
                saveAnswer();

                // Passer à la question suivante
                currentQuestionIndex++;
                if (currentQuestionIndex < 10) {
                    displayQuestion();
                } else {
                    // Fin du quiz, passer à la prochaine activité


                }
            }
        });
    }

    // Fonction pour charger les questions et les réponses (à améliorer pour charger depuis une base de données ou autre)
    private void loadQuestionData() {
        for (int i = 0; i < 10; i++) {
            questions[i] = "Question " + (i + 1);
            choices[i][0] = "Réponse 1";
            choices[i][1] = "Réponse 2";
            choices[i][2] = "Réponse 3";
        }
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
}
