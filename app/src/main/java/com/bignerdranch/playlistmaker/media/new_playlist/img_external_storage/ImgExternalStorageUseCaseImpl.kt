package com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage

import android.net.Uri

class ImgExternalStorageUseCaseImpl(
    private val repository: ImgExternalStorage
): ImgExternalStorageUseCase {
    override suspend fun saveImgToExternalStorage(uri: Uri): Uri {
        return repository.saveImage(uri)
    }
}