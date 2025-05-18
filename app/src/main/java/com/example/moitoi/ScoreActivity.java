package com.example.moitoi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    private TextView tvScoreValue;
    private Button btnBackToResults;
    private int score = 0;
    private int totalQuestions = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvScoreValue = findViewById(R.id.tvScoreValue);
        btnBackToResults = findViewById(R.id.btnBackToResults);

        // Récupérer le score depuis l'intent
        if (getIntent().hasExtra("SCORE")) {
            score = getIntent().getIntExtra("SCORE", 0);
            // Toujours afficher sur 10
            tvScoreValue.setText(String.format("%d/10", score));
        }

        btnBackToResults.setOnClickListener(v -> finish());
    }
}
