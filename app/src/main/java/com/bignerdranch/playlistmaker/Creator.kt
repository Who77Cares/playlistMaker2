package com.bignerdranch.playlistmaker

import com.bignerdranch.playlistmaker.data.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    private fun getTrackRepository(): TrackRepository {
        return  TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}