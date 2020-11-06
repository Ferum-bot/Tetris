package com.example.tetris

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import java.lang.Integer.max

class MainActivity : AppCompatActivity() {

    private lateinit var startGameButton: Button
    private lateinit var resetScoreButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        attachAllView()
        setDefaultOptions()
    }

    private fun attachAllView() {
        startGameButton = findViewById(R.id.start_game)
        resetScoreButton = findViewById(R.id.reset_score)
        optionsButton = findViewById(R.id.options)
        exitButton = findViewById(R.id.exit)
    }

    private fun setDefaultOptions() {
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val arrayOfButtons = arrayOf(startGameButton, resetScoreButton, optionsButton, exitButton)
        var maxWidth = 0
        for (el in arrayOfButtons) {
            Log.i("Width", "${el.layoutParams.width}")
            if (el.layoutParams.width > maxWidth) {
                maxWidth = el.layoutParams.width
            }
        }
        for (el in arrayOfButtons) {
            //el.layoutParams.width = maxWidth
        }
    }
}