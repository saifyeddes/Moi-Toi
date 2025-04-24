package com.example.moitoi;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class ManageQuestionsActivity extends AppCompatActivity {

    private Button btnNext, btnPrevious;
    private int currentQuestionIndex = 1;  // Current question index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        final TextView tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        // Update the question number
        tvQuestionNumber.setText("Question " + currentQuestionIndex);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionIndex++;
                tvQuestionNumber.setText("Question " + currentQuestionIndex);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 1) {
                    currentQuestionIndex--;
                    tvQuestionNumber.setText("Question " + currentQuestionIndex);
                }
            }
        });
    }
}
