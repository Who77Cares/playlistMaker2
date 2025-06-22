package com.bignerdranch.playlistmaker

import android.content.Context
import com.bignerdranch.playlistmaker.data.SearchHistoryRepositoryImpl
import com.bignerdranch.playlistmaker.data.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.data.storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.bignerdranch.playlistmaker.domain.impl.TrackInteractorImpl
import com.bignerdranch.playlistmaker.domain.models.Track
import com.bignerdranch.playlistmaker.ui.presentation.SearchViewModel
import com.google.gson.reflect.TypeToken

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return  TrackRepositoryImpl(RetrofitNetworkClient(context = context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context = context))
    }

    private fun  getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient(
                context = context,
                dataKey = "HISTORY",
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

}