package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorage
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageImpl
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageUseCase
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageUseCaseImpl
import org.koin.dsl.module


val dataModule = module {

    single<ImgExternalStorage> {
        ImgExternalStorageImpl(get())
    }
    single<ImgExternalStorageUseCase> {
        ImgExternalStorageUseCaseImpl(get())
    }


}