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

}