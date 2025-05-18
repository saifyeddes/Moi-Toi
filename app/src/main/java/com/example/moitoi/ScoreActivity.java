package com.example.moitoi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ScoreActivity extends AppCompatActivity {
    private TextView tvScoreValue, tvScoreMessage;
    private ImageView ivScoreEmoji;
    private Button btnBackToResults;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvScoreValue = findViewById(R.id.tvScoreValue);
        tvScoreMessage = findViewById(R.id.tvScoreMessage);
        ivScoreEmoji = findViewById(R.id.ivScoreEmoji);
        btnBackToResults = findViewById(R.id.btnBackToResults);

        // Récupérer le score depuis l'intent
        if (getIntent().hasExtra("SCORE")) {
            score = getIntent().getIntExtra("SCORE", 0);
            updateScoreUI(score);
        }

        btnBackToResults.setOnClickListener(v -> finish());
    }

    private void updateScoreUI(int score) {
        // Afficher le score
        tvScoreValue.setText(String.format("%d/10", score));

        // Définir le smiley et le message en fonction du score
        if (score >= 5) {
            // Score réussi (5/10 ou plus)
            ivScoreEmoji.setImageResource(R.drawable.ic_sentiment_very_satisfied);
            tvScoreMessage.setText("Félicitations ! Vous avez réussi le quiz !");
            tvScoreMessage.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            // Score échoué (moins de 5/10)
            ivScoreEmoji.setImageResource(R.drawable.ic_sentiment_very_dissatisfied);
            tvScoreMessage.setText("Dommage ! Essayez de nouveau pour vous améliorer.");
            tvScoreMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }
}
