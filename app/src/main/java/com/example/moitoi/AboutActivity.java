package com.example.moitoi;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lie le layout XML à cette activité
        setContentView(R.layout.activity_about);
    }
}