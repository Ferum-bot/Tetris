package com.example.tetris

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class GameActivity : AppCompatActivity() {

    private lateinit var restartButton: Button

    private lateinit var bestScoreView: TextView
    private lateinit var currentScoreView: TextView

    private lateinit var clickListenerForButton: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setDefaultOptions()
        attachAllView()
    }

    private fun setDefaultOptions() {
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun attachAllView() {
        restartButton = findViewById(R.id.restart_game)

        bestScoreView = findViewById(R.id.best_score)
        currentScoreView = findViewById(R.id.current_score)
        clickListenerForButton = View.OnClickListener { onClick(it) }
    }

    private fun onClick(view: View) {

    }
}