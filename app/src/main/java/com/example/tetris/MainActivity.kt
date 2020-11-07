package com.example.tetris

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tetris.storage.AppPreferences
import com.google.android.material.snackbar.Snackbar
import java.lang.Integer.max
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var startGameButton: Button
    private lateinit var resetScoreButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button

    private lateinit var bestScoreTextView: TextView

    private lateinit var onClickListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        attachAllView()
    }

    override fun onResume() {
        super.onResume()
        setDefaultOptions()
        attachClickListener()
    }

    private fun attachAllView() {
        startGameButton = findViewById(R.id.start_game)
        resetScoreButton = findViewById(R.id.reset_score)
        optionsButton = findViewById(R.id.options)
        exitButton = findViewById(R.id.exit)

        bestScoreTextView = findViewById(R.id.best_score)

        onClickListener = View.OnClickListener { onClick(it) }
    }

    private fun onClick(view: View) {
        when(view.id) {
            R.id.start_game -> startGameActivity()
            R.id.exit -> exitFromApp()
            R.id.options -> startOptionsActivity()
            R.id.reset_score -> resetScore(view)
        }
    }

    private fun startGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun exitFromApp() {
        exitProcess(0)
    }

    private fun startOptionsActivity() {
        val intent = Intent(this, OptionsActivity::class.java)
        startActivity(intent)
    }

    private fun resetScore(view: View) {
        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        bestScoreTextView.text = getString(R.string.best_score) + " 0"
        val snackBar = Snackbar.make(view, "Score successfully reset", Snackbar.LENGTH_SHORT)
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackBar.show()
    }

    private fun attachClickListener() {
        startGameButton.setOnClickListener(onClickListener)
        resetScoreButton.setOnClickListener(onClickListener)
        exitButton.setOnClickListener(onClickListener)
        optionsButton.setOnClickListener(onClickListener)
    }

    private fun setDefaultOptions() {
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}