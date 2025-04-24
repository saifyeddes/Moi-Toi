package com.example.moitoi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val btnAnswer = findViewById<Button>(R.id.btnAnswer)

        btnCreate.setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }

        btnAnswer.setOnClickListener {
            startActivity(Intent(this, AnswerQuizActivity::class.java))
        }
    }
}
