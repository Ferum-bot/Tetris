package com.example.tetris.storage

import android.content.Context
import android.content.SharedPreferences


class AppPreferences(context: Context) {
    var date = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveHighScore(score: Int) {
        date.edit().putInt("HIGH_SCORE", score).apply()
    }

    fun getHighScore(): Int {
        return date.getInt("HIGH_SCORE", 0)
    }

    fun clearHighScore() {
        date.edit().putInt("HIGH_SCORE", 0).apply()
    }
}