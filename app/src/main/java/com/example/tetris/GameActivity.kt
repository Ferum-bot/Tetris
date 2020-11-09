package com.example.tetris

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.models.AppModel
import com.example.tetris.storage.AppPreferences
import com.example.tetris.views.TetrisView

class GameActivity : AppCompatActivity() {

    private lateinit var tetrisView: TetrisView

    private val appModel: AppModel = AppModel()

    private lateinit var restartButton: Button
    private lateinit var pauseButton: Button

    lateinit var bestScoreView: TextView
    lateinit var currentScoreView: TextView

    private lateinit var clickListenerForButton: View.OnClickListener
    private lateinit var touchListenerForTetrisView: View.OnTouchListener

    var appReferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setDefaultOptions()
        attachAllView()
        updateAllValues()
    }

    override fun onResume() {
        super.onResume()
        setClickListeners()
    }

    private fun setClickListeners() {
        restartButton.setOnClickListener(clickListenerForButton)
        pauseButton.setOnClickListener(clickListenerForButton)
        tetrisView.setOnTouchListener(this::onTouchTetrisView)
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
        pauseButton = findViewById(R.id.paused)

        bestScoreView = findViewById(R.id.best_score)
        currentScoreView = findViewById(R.id.current_score)

        clickListenerForButton = View.OnClickListener { onClickButton(it) }
        //touchListenerForTetrisView = View.OnTouchListener()

        tetrisView = findViewById(R.id.tetris_view)
        tetrisView.setActivity(this)
        appModel.setPreferences(appReferences!!)
        tetrisView.setModel(appModel)
    }

    private fun onClickButton(view: View) {
        when(view.id) {
            R.id.restart_game -> {
                tetrisView.continueGame()
                pauseButton.text = getString(R.string.pause)
                appModel.resetGame()
                updateCurrentScore()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            }
            R.id.paused -> doPauseAction(view as Button)
        }
    }

    private fun doPauseAction(button: Button) {
        when(button.text) {
            getString(R.string.start) -> {
                button.text = getString(R.string.pause)
                appModel.startGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            }
            getString(R.string.pause) -> {
                button.text = getString(R.string.continue_game)
                tetrisView.setGameOnPause()
            }
            getString(R.string.continue_game) -> {
                button.text = getString(R.string.pause)
                tetrisView.continueGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            }
        }
    }

    private fun onTouchTetrisView(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            pauseButton.text = getString(R.string.pause)
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        }
        else {
            if (appModel.isGameActive()) {
                when(resolveTouchDirection(view, event)) {
                    0 -> moveTetromino(AppModel.Motions.LEFT)
                    1 -> moveTetromino(AppModel.Motions.ROTATE)
                    2 -> moveTetromino(AppModel.Motions.DOWN)
                    3 -> moveTetromino(AppModel.Motions.RIGHT)
                }
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int
        direction =
            if (y > x) {
                if (x > 1 - y) 2 else 0
            }
            else {
                if (x > 1 - y) 3 else 1
            }
        return  direction
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }
}