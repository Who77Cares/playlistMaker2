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
import com.bignerdranch.playlistmaker.settings.data.settings.SettingsRepository
import com.bignerdranch.playlistmaker.settings.data.settings.SettingsRepositoryImpl
import com.bignerdranch.playlistmaker.settings.data.sharing.ExternalNavigator
import com.bignerdranch.playlistmaker.settings.data.sharing.ExternalNavigatorImpl
import com.bignerdranch.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.interactorImpl.SettingsInteractorImpl
import com.bignerdranch.playlistmaker.settings.domain.interactorImpl.SharingInteractorImpl
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings
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


    // настройки
    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            PrefsStorageClient(
                context = context,
                dataKey = "theme_settings",
                type = object : TypeToken<ThemeSettings>() {}.type
            )
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val resources = context.resources
        return SharingInteractorImpl(
            navigator = provideExternalNavigator(context),
            shareUrl = resources.getString(R.string.share_url),
            supportEmail = resources.getString(R.string.sendToSupport_email),
            supportSubject = resources.getString(R.string.sendToSupport_theme),
            supportBody = resources.getString(R.string.sendToSupport_text),
            termsUrl = resources.getString(R.string.userAgreement_url)
        )
    }

}