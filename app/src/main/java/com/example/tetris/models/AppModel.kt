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

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when(action) {
                Motions.LEFT.name -> coordinate?.x = currentBlock?.position?.x?.minus(1)
                Motions.RIGHT.name -> coordinate?.x = currentBlock?.position?.x?.plus(1)
                Motions.DOWN.name -> coordinate?.y = currentBlock?.position?.y?.plus(1)
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if(!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)
                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()
                    if (!blocAdditionPossible()) {
                        currentState = Statuses.OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            }
            else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            for (j in 0 until FieldConstants.COLUMN_COUNT.value) {
                if (!ephemeralCellOnly || field[i][j] == CellConstants.EPHEMERAL.value) {
                    field[i][j] = CellConstants.EMPTY.value
                }
            }
        }
    }

    private fun persistCellData() {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            for (j in 0 until FieldConstants.COLUMN_COUNT.value) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.staticValue
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun assessField() {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            var emptyCells = 0;
            for (j in field[i].indices) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty) {
                    emptyCells++
                }
            }
            if (emptyCells == 0) {
                shiftRows(i)
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field){
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null) {
                for (i in shape.indices) {
                    for (j in shape[i].indices) {
                        val y = position.y + i
                        val x = position.x + j
                        if (shape[i][j] != CellConstants.EMPTY.value) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun blocAdditionPossible(): Boolean {
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)) {
            return false
        }
        return true
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for (j in 0 until field[0].size) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun resetGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        currentScore = 0
        currentState = Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        currentScore = 0
    }

    private fun assessFiled() {
        for (i in field.indices) {
            var emptyCells = 0
            for (j in field[i].indices) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty) {
                    emptyCells++
                }
            }
            if (emptyCells == 0) {
                shiftRows(i)
            }
        }
    }

}