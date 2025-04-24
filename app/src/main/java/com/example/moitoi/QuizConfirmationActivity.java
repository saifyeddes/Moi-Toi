package com.example.moitoi;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class QuizConfirmationActivity extends AppCompatActivity {

    private Button btnSubmitQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation);

        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);

        btnSubmitQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Final logic to save the quiz or submit it
                Toast.makeText(QuizConfirmationActivity.this, "Quiz soumis avec succès!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
