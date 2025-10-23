package com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage

import android.net.Uri

interface ImgExternalStorageUseCase  {

    suspend fun saveImgToExternalStorage(uri: Uri): Uri

}

//Сейчас Use Case просто пробрасывает вызов, но позже туда можно добавить:
//проверку формата файла,
//оптимизацию размера изображения,
//сохранение в нескольких местах,
//логи или аналитику.