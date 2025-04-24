package com.example.moitoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MatchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        Button startChatBtn = findViewById(R.id.btnStartChat);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une autre activité (par exemple MainActivity)
                startActivity(new Intent(MatchActivity.this, MainActivity.class));
            }
        });
    }
}
