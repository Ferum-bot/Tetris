package com.example.tetris.models

import android.graphics.Point
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstants
import com.example.tetris.helpers.array2dOfByte
import com.example.tetris.storage.AppPreferences

class AppModel {

    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE;
    }

    var currentScore = 0

    private var preferences: AppPreferences? = null

    var currentBlock: Block? = null
    var currentState: String = Statuses.AWAITING_START.name

    private var field: Array<ByteArray> = array2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    fun setPreferences(preferences: AppPreferences) {
        this.preferences = preferences
    }

    fun getCellStatus(i : Int, j : Int): Byte? {
        return field[i][j];
    }

    fun setCellStatus(i: Int, j: Int, status: Byte?) {
        if (status != null) {
            field[i][j] = status;
        }
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    private fun boostScore() {
        currentScore += 10
        if (currentScore > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(currentScore)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        if (position.y < 0 || position.x < 0) {
            return false
        }
        else {
            if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
                return false
            }
            else {
                if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
                    return false
                }
                else {
                    for (i in shape.indices) {
                        for (j in shape[i].indices) {
                            val y = position.y + i
                            val x = position.x + j
                            if (shape[i][j] != CellConstants.EMPTY.value && field[y][x] != CellConstants.EMPTY.value) {
                                return false
                            }
                        }
                    }
                    return true
                }
            }
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }
}