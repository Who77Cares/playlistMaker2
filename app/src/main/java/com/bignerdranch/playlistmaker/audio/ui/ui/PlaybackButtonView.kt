package com.bignerdranch.playlistmaker.audio.ui.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bignerdranch.playlistmaker.R
import kotlin.math.min

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
): View(context, attrs, defStyleAttr, defStyleRes) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying: Boolean = false

    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private var iconScale = 0.7f


    // Callback для обработки нажатий
    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null


    init {
        // Загружаем атрибуты из XML
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.PlaybackButtonView,
                defStyleAttr,
                0
            )

            try {
                val playIconResId = typedArray.getResourceId(
                    R.styleable.PlaybackButtonView_playIcon,
                    0
                )
                val pauseIconResId = typedArray.getResourceId(
                    R.styleable.PlaybackButtonView_pauseIcon,
                    0
                )

                if (playIconResId != 0) {
                    val drawable = ContextCompat.getDrawable(context, playIconResId)
                    playBitmap = drawable?.toBitmap()
                }

                if (pauseIconResId != 0) {
                    val drawable = ContextCompat.getDrawable(context, pauseIconResId)
                    pauseBitmap = drawable?.toBitmap()
                }
            } finally {
                typedArray.recycle()
            }
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Устанавливаем область для отрисовки иконки
        val size = min(w, h).toFloat()
        val iconSize = size * iconScale  // Размер иконки

        // Вычисляем отступы для центрирования
        val horizontalPadding = (w - iconSize) / 2
        val verticalPadding = (h - iconSize) / 2

        // Устанавливаем область для отрисовки иконки
        imageRect = RectF(
            horizontalPadding,
            verticalPadding,
            horizontalPadding + iconSize,
            verticalPadding + iconSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentBitmap = if (isPlaying) pauseBitmap else playBitmap
        currentBitmap?.let { bitmap ->
            // Используем RectF для правильного масштабирования иконки
            canvas.drawBitmap(bitmap, null, imageRect, null)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Начинаем отслеживать касание
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Пользователь поднял палец - меняем состояние
                togglePlaybackState()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePlaybackState() {
        isPlaying = !isPlaying
        invalidate() // Перерисовываем View

        // Уведомляем слушателя об изменении состояния
        onPlaybackStateChanged?.invoke(isPlaying)
    }

    // метод для изменения состояния извне
    fun setPlaybackState(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Устанавливаем размеры View на основе размеров иконок
        val desiredWidth = (playBitmap?.width ?: 100).coerceAtLeast(pauseBitmap?.width ?: 100)
        val desiredHeight = (playBitmap?.height ?: 100).coerceAtLeast(pauseBitmap?.height ?: 100)

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}