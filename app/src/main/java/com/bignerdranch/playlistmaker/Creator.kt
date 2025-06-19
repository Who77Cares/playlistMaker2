package com.bignerdranch.playlistmaker

import android.content.Context
import com.bignerdranch.playlistmaker.data.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return  TrackRepositoryImpl(RetrofitNetworkClient(context = context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context = context))
    }
}