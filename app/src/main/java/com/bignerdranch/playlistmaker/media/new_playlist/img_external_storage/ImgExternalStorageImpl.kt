package com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImgExternalStorageImpl(private val context: Context) : ImgExternalStorage {

    override suspend fun saveImage(uri: Uri): Uri = withContext(Dispatchers.IO) {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) filePath.mkdirs()

        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        inputStream?.close()
        outputStream.close()


        Log.d("SaveImage", "Файл сохранен: ${file.toUri()}")
        Log.d("SaveImage", "Полный путь: ${file.absolutePath}")

        Log.d("SaveImage", "Имя файла: ${file.name}")
        file.toUri()
    }
}

