package com.bignerdranch.playlistmaker.new_playlist

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel

class NewPlaylistViewModel: ViewModel() {

    // Вложенный класс состояния кнопки
    data class ButtonState(
        val isEnabled: Boolean,
        val backgroundColor: Int
    )


    private val buttonState = MutableLiveData<ButtonState>()
    fun observeButtonState(): LiveData<ButtonState> = buttonState


     fun updateButtonState(text: String?, enabledColor: Int, disabledColor: Int) {
        val enabled = !text.isNullOrBlank()
        val color = if (enabled) enabledColor else disabledColor
        buttonState.value = ButtonState(enabled, color)

    }
}
