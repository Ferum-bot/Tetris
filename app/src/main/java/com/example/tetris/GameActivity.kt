package com.example.tetris

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.storage.AppPreferences

class GameActivity : AppCompatActivity() {

    private lateinit var restartButton: Button

    private lateinit var bestScoreView: TextView
    private lateinit var currentScoreView: TextView

    private lateinit var clickListenerForButton: View.OnClickListener

    private var appReferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setDefaultOptions()
        attachAllView()
        updateAllValues()
    }

    private  fun updateAllValues() {
        updateBestScore()
        updateCurrentScore()
    }

    private fun updateBestScore() {
        bestScoreView.text = appReferences?.getHighScore().toString()
    }

    private fun updateCurrentScore() {
        currentScoreView.text = "0"
    }

    private fun setDefaultOptions() {
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        appReferences = AppPreferences(this)
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