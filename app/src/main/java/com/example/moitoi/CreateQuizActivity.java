package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateQuizActivity extends AppCompatActivity {
    private int currentQuestionIndex = 0;
    private final String[] questions = new String[10];
    private final String[][] choices = new String[10][3];
    private final int[] selectedChoices = new int[10]; // Stores the selected choice index for each question (0, 1, 2, or -1)

    private EditText etQuestion, etChoice1, etChoice2, etChoice3;
    private RadioButton rbChoice1, rbChoice2, rbChoice3;
    private Button btnNext, btnPrevious, btnConfirm, btnQuit;
    private TextView tvQuestionNumber;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        etQuestion = findViewById(R.id.etQuestion);
        etChoice1 = findViewById(R.id.etChoice1);
        etChoice2 = findViewById(R.id.etChoice2);
        etChoice3 = findViewById(R.id.etChoice3);
        rbChoice1 = findViewById(R.id.rbChoice1);
        rbChoice2 = findViewById(R.id.rbChoice2);
        rbChoice3 = findViewById(R.id.rbChoice3);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnQuit = findViewById(R.id.btnQuit);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        // Initialize selectedChoices array to -1 (no selection)
        for (int i = 0; i < selectedChoices.length; i++) {
            selectedChoices[i] = -1;
        }

        // Ensure RadioButtons are clickable
        rbChoice1.setClickable(true);
        rbChoice2.setClickable(true);
        rbChoice3.setClickable(true);

        // Set up RadioButton listeners using setOnCheckedChangeListener
        rbChoice1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(CreateQuizActivity.this, "RadioButton 1 clicked (Choice 1)", Toast.LENGTH_SHORT).show();
                    handleRadioButtonSelection(0);
                }
            }
        });
        rbChoice2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(CreateQuizActivity.this, "RadioButton 2 clicked (Choice 2)", Toast.LENGTH_SHORT).show();
                    handleRadioButtonSelection(1);
                }
            }
        });
        rbChoice3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(CreateQuizActivity.this, "RadioButton 3 clicked (Choice 3)", Toast.LENGTH_SHORT).show();
                    handleRadioButtonSelection(2);
                }
            }
        });

        // Confirm listeners are set up
        Toast.makeText(this, "RadioButton listeners set up", Toast.LENGTH_SHORT).show();

        // Set up Quit button
        btnQuit.setOnClickListener(v -> navigateToInterfaceChoix());

        // Display first question
        displayQuestion();
        updateButtons();

        // Next button logic
        btnNext.setOnClickListener(v -> {
            if (validateInput()) {
                saveAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < 10) {
                    Toast.makeText(this, "Navigating to Question " + (currentQuestionIndex + 1) + ", Selection for Q" + (currentQuestionIndex + 1) + ": " + (selectedChoices[currentQuestionIndex] == -1 ? "None" : "Choice " + (selectedChoices[currentQuestionIndex] + 1)), Toast.LENGTH_SHORT).show();
                    displayQuestion();
                    updateButtons();
                } else {
                    currentQuestionIndex = 9;
                    updateButtons();
                }
            }
        });
    }

    private void navigateToInterfaceChoix() {
        Intent intent = new Intent(CreateQuizActivity.this, InterfaceChoix.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleRadioButtonSelection(int choiceIndex) {
        // Uncheck all RadioButtons
        rbChoice1.setChecked(false);
        rbChoice2.setChecked(false);
        rbChoice3.setChecked(false);

        // Check the selected RadioButton
        if (choiceIndex == 0) rbChoice1.setChecked(true);
        else if (choiceIndex == 1) rbChoice2.setChecked(true);
        else rbChoice3.setChecked(true);

        // Update selection for the current question
        selectedChoices[currentQuestionIndex] = choiceIndex;

        // Save the answer to ensure the choice is stored
        saveAnswer();

        // Show Toast to confirm selection (1-based for user, 0-based internally)
        String selectedAnswer = choiceIndex == 0 ? etChoice1.getText().toString().trim() :
                choiceIndex == 1 ? etChoice2.getText().toString().trim() : etChoice3.getText().toString().trim();
        Toast.makeText(this, "Selected: Question " + (currentQuestionIndex + 1) + ", Choice " + (choiceIndex + 1) + " (index " + choiceIndex + "): " + selectedAnswer, Toast.LENGTH_LONG).show();
    }

    private boolean validateInput() {
        String questionText = etQuestion.getText().toString().trim();
        String choice1Text = etChoice1.getText().toString().trim();
        String choice2Text = etChoice2.getText().toString().trim();
        String choice3Text = etChoice3.getText().toString().trim();

        if (questionText.isEmpty()) {
            showAlert("Attention", "Veuillez saisir la question avant de continuer.");
            return false;
        }
        if (choice1Text.isEmpty() || choice2Text.isEmpty() || choice3Text.isEmpty()) {
            showAlert("Attention", "Veuillez remplir toutes les réponses avant de continuer.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void displayQuestion() {
        // Load saved question and choices or clear fields
        etQuestion.setText(questions[currentQuestionIndex] != null ? questions[currentQuestionIndex] : "");
        etChoice1.setText(choices[currentQuestionIndex][0] != null ? choices[currentQuestionIndex][0] : "");
        etChoice2.setText(choices[currentQuestionIndex][1] != null ? choices[currentQuestionIndex][1] : "");
        etChoice3.setText(choices[currentQuestionIndex][2] != null ? choices[currentQuestionIndex][2] : "");
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1));

        // Update RadioButton states
        rbChoice1.setChecked(false);
        rbChoice2.setChecked(false);
        rbChoice3.setChecked(false);

        // Check the RadioButton if this question has a selected answer
        int choiceIndex = selectedChoices[currentQuestionIndex];
        if (choiceIndex != -1) {
            switch (choiceIndex) {
                case 0:
                    rbChoice1.setChecked(true);
                    break;
                case 1:
                    rbChoice2.setChecked(true);
                    break;
                case 2:
                    rbChoice3.setChecked(true);
                    break;
            }
            Toast.makeText(this, "Restored selection: Question " + (currentQuestionIndex + 1) + ", Choice " + (choiceIndex + 1), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No selection for Question " + (currentQuestionIndex + 1), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAnswer() {
        String question = etQuestion.getText().toString().trim();
        String choice1 = etChoice1.getText().toString().trim();
        String choice2 = etChoice2.getText().toString().trim();
        String choice3 = etChoice3.getText().toString().trim();

        questions[currentQuestionIndex] = question;
        choices[currentQuestionIndex][0] = choice1;
        choices[currentQuestionIndex][1] = choice2;
        choices[currentQuestionIndex][2] = choice3;
    }

    private void submitQuiz() {
        // Check if all questions have a selection
        for (int i = 0; i < selectedChoices.length; i++) {
            if (selectedChoices[i] == -1) {
                showAlert("Attention", "Veuillez sélectionner une réponse pour la question " + (i + 1) + ".");
                return;
            }
        }

        // Log the selections for all questions
        StringBuilder selectionLog = new StringBuilder("Submitting Selections:\n");
        for (int i = 0; i < selectedChoices.length; i++) {
            selectionLog.append("Question ").append(i + 1).append(": Choice ").append(selectedChoices[i] + 1).append("\n");
        }
        Toast.makeText(this, selectionLog.toString(), Toast.LENGTH_LONG).show();

        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        Map<String, Object> quizData = new HashMap<>();
        for (int i = 0; i < questions.length; i++) {
            String questionText = questions[i] != null && !questions[i].isEmpty() ? questions[i] : "Non répondu";
            String rep1 = choices[i][0] != null && !choices[i][0].isEmpty() ? choices[i][0] : "Non répondu";
            String rep2 = choices[i][1] != null && !choices[i][1].isEmpty() ? choices[i][1] : "Non répondu";
            String rep3 = choices[i][2] != null && !choices[i][2].isEmpty() ? choices[i][2] : "Non répondu";
            // Store choix as 1-based to match UI (1, 2, 3) instead of 0-based (0, 1, 2)
            int choix = selectedChoices[i] + 1;
            quizData.put("question_" + (i + 1), questionText);
            quizData.put("reponse_" + (i + 1) + "_1", rep1);
            quizData.put("reponse_" + (i + 1) + "_2", rep2);
            quizData.put("reponse_" + (i + 1) + "_3", rep3);
            quizData.put("choix_" + (i + 1), choix);
        }

        db.collection("MoiEtToi")
                .document("quiz_1")
                .collection("user_responses")
                .document(code)
                .set(quizData)
                .addOnSuccessListener(aVoid -> new android.app.AlertDialog.Builder(CreateQuizActivity.this)
                        .setTitle("Code du quiz généré")
                        .setMessage("Votre code de quiz est : " + code)
                        .setPositiveButton("Quitter", (dialog, which) -> navigateToInterfaceChoix())
                        .show())
                .addOnFailureListener(e -> showAlert("Erreur", "Erreur lors de l'enregistrement : " + e.getMessage()));
    }

    private void updateButtons() {
        btnPrevious.setVisibility(currentQuestionIndex == 0 ? View.GONE : View.VISIBLE);
        btnConfirm.setVisibility(currentQuestionIndex >= 1 ? View.VISIBLE : View.GONE);
        btnNext.setVisibility(currentQuestionIndex == 9 ? View.GONE : View.VISIBLE);

        btnPrevious.setOnClickListener(v -> {
            if (validateInput()) {
                saveAnswer();
                currentQuestionIndex--;
                Toast.makeText(this, "Navigating to Question " + (currentQuestionIndex + 1) + ", Selection for Q" + (currentQuestionIndex + 1) + ": " + (selectedChoices[currentQuestionIndex] == -1 ? "None" : "Choice " + (selectedChoices[currentQuestionIndex] + 1)), Toast.LENGTH_SHORT).show();
                displayQuestion();
                updateButtons();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (validateInput()) {
                saveAnswer();
                submitQuiz();
            }
        });
    }
}