package com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage

import android.net.Uri

interface ImgExternalStorage {

    suspend fun saveImage(uri: Uri): Uri

}