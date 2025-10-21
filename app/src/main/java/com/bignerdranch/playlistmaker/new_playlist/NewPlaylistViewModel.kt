package com.bignerdranch.playlistmaker.new_playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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

// сохранение картинки во внутренее хранилище
    suspend fun saveImageToPrivateStorage(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            // создаём экземпляр класса File, который указывает на нужный каталог
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

            // Создаем папку
            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            // Создаем файл
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val file = File(filePath, fileName)

            // открываем потоки
            val inputStream = context.contentResolver.openInputStream(uri)
            // создаём исходящий поток байтов в созданный выше файл
            val outputStream = FileOutputStream(file)

            // Копируем/обрабатываем данные
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            // Закрываем потоки
            inputStream?.close()
            outputStream.close()

            // ПОЛУЧАЕМ URI сохраненного файла
            val savedFileUri = file.toUri()

            // Выводим в лог
            Log.d("SaveImage", "Файл сохранен: ${savedFileUri}")
            Log.d("SaveImage", "Полный путь: ${file.absolutePath}")
            Log.d("SaveImage", "Имя файла: ${file.name}")

        }
    }

    fun saveImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            saveImageToPrivateStorage(context, uri)
        }
    }

}
