package com.example.tetris.views

import android.view.View
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Message
import android.util.AttributeSet
import android.widget.Toast
import android.os.Handler
import androidx.annotation.Dimension
import com.example.tetris.GameActivity
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstants
import com.example.tetris.models.AppModel
import com.example.tetris.models.Block
import java.util.jar.Attributes
import java.util.logging.LogRecord

class TetrisView: View{

    private val paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var activity: GameActivity? = null
    private val viewHandler = ViewHandler(this)
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffset: Dimension = Dimension(0, 0)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    companion object {
        private val DELAY = 200
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET_VALUE = 10
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)
        if (model != null) {
            for (i in 0 until FieldConstants.ROW_COUNT.value) {
                for (j in 0 until FieldConstants.COLUMN_COUNT.value) {
                    drawCell(canvas, i, j)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val cellWidth = (w - 2 * FRAME_OFFSET_VALUE) / FieldConstants.COLUMN_COUNT.value
        val cellHeight = (h - 2 * FRAME_OFFSET_VALUE) / FieldConstants.COLUMN_COUNT.value
        val n = cellHeight.coerceAtMost(cellWidth)
        this.cellSize = Dimension(n, n)
        val offsetX = (w - FieldConstants.COLUMN_COUNT.value * n) / 2
        val offsetY = (h - FieldConstants.ROW_COUNT.value * n) / 2
        this.frameOffset = TetrisView.Dimension(offsetX, offsetY)
    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawRect(frameOffset.width.toFloat(), frameOffset.height.toFloat(),
            width - frameOffset.width.toFloat(),height - frameOffset.height.toFloat(), paint)
        paint.color = Color.BLACK
        canvas.drawRect(frameOffset.width.toFloat() + 2f,  frameOffset.height.toFloat() + 2f,
            width - frameOffset.width.toFloat() - 2f, height - frameOffset.height.toFloat() - 2f, paint);
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val cellStatus = model?.getCellStatus(row, col)
        if (cellStatus != CellConstants.EMPTY.value) {
            val color =
                if (CellConstants.EPHEMERAL.value == cellStatus) {
                    model?.currentBlock?.color
                }
                else {
                    Block.getColor(cellStatus as Byte)
                }
            drawCell(canvas, col, row, color as Int)
        }
    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int, rgbColor: Int) {
        paint.color = rgbColor
        val top: Float = (frameOffset.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val left: Float = (frameOffset.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val bottom: Float = (frameOffset.height + (y + 1) * cellSize.height - BLOCK_OFFSET).toFloat()
        val right: Float = (frameOffset.width + (x + 1) * cellSize.width - BLOCK_OFFSET).toFloat()
        val rectangle = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
    }

    private class ViewHandler(private val owner: TetrisView): Handler() {
        override fun handleMessage(message: Message) {
            if (message.what == 0) {
                if (owner.model != null) {
                    if (owner.model!!.isGameOver()) {
                        owner.model?.endGame()
                        Toast.makeText(owner.activity, "Game Over", Toast.LENGTH_LONG).show()
                    }
                    if (owner.model!!.isGameActive()) {
                        owner.setGameCommandWithDelay(AppModel.Motions.DOWN)
                    }
                }
            }
        }

        fun sleep(delay: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }

    }

    private data class Dimension(val width: Int, val height: Int)

    fun setModel(model: AppModel) {
        this.model = model
    }

    fun setActivity(gameActivity: GameActivity) {
        this.activity = gameActivity
    }

    fun setGameCommand(move: AppModel.Motions) {
        if (model != null && (model?.currentState == AppModel.Statuses.ACTIVE.name)) {
            if (AppModel.Motions.DOWN == move) {
                model?.generateField(move.name)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }
    }

    fun setGameOnPause() {
        this.model?.setGameOnPause()
    }

    fun continueGame() {
        this.model?.continueGame()
    }

    fun setGameCommandWithDelay(move: AppModel.Motions) {
        if (model != null && model!!.isGamePaused()) {
            return
        }
        val now = System.currentTimeMillis()
        if (now - lastMove > DELAY) {
            model?.generateField(move.name)
            invalidate()
            lastMove = now
        }
        updateScores()
        viewHandler.sleep(DELAY.toLong())
    }

    private fun updateScores() {
        activity?.currentScoreView?.text = "${model?.currentScore}"
        activity?.bestScoreView?.text = "${activity?.appReferences?.getHighScore()}"
    }

}