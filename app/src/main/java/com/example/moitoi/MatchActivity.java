package com.example.moitoi;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MatchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        findViewById(R.id.layoutMan).setOnClickListener(v ->
                Toast.makeText(this, "Homme sélectionné", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.layoutWoman).setOnClickListener(v ->
                Toast.makeText(this, "Femme sélectionnée", Toast.LENGTH_SHORT).show()
        );
    }
}
