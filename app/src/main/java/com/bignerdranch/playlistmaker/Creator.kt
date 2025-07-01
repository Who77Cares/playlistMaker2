package com.bignerdranch.playlistmaker

import android.content.Context
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.SearchHistoryRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.SearchHistoryInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.TrackInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.models.Track
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