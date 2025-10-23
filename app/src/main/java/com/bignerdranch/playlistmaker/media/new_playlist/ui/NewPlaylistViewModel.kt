package com.bignerdranch.playlistmaker.media.new_playlist.ui

import android.net.Uri
import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageUseCase
import kotlinx.coroutines.launch


class NewPlaylistViewModel(
    private val saveImgToExternalStorage: ImgExternalStorageUseCase,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    // Вложенный класс состояния кнопки.
    data class ButtonState(
        val isEnabled: Boolean,
        val backgroundColor: Int
    )


    private val buttonState = MutableLiveData<ButtonState>()
    fun observeButtonState(): LiveData<ButtonState> = buttonState

    private val currentImgUri = MutableLiveData<Uri>()
    fun observeCurrentImgUri(): LiveData<Uri> = currentImgUri


     fun updateButtonState(text: String?, enabledColor: Int, disabledColor: Int) {
        val enabled = !text.isNullOrBlank()
        val color = if (enabled) enabledColor else disabledColor
        buttonState.value = ButtonState(enabled, color)
    }


    fun saveImage(uri: Uri) {
        viewModelScope.launch {
            currentImgUri.value = saveImgToExternalStorage.saveImgToExternalStorage(uri)
        }
    }

    fun savePlaylist(playlist: PlaylistModel) {
        viewModelScope.launch {

            // тут должна быть логика провкерки на совпадение имен в бд playlist

            playlistInteractor.createPlaylist(playlist)
        }
    }
}
