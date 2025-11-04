package com.bignerdranch.playlistmaker.di

import android.content.Context
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.SearchHistoryInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.TrackInteractorImpl
import com.bignerdranch.playlistmaker.settings.data.sharing.SettingsNavigator
import com.bignerdranch.playlistmaker.settings.data.sharing.SettingsNavigatorImpl
import com.bignerdranch.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.interactorImpl.SettingsInteractorImpl
import com.bignerdranch.playlistmaker.settings.domain.interactorImpl.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<SettingsNavigator> {
        SettingsNavigatorImpl(context = get())
    }

    single<SharingInteractor> {

        val ctx = get<Context>()
        val res = ctx.resources

        SharingInteractorImpl(
            navigator = get(),
            supportEmail = res.getString(R.string.sendToSupport_email),
            supportSubject = res.getString(R.string.sendToSupport_theme),
            supportBody = res.getString(R.string.sendToSupport_text),
            termsUrl = res.getString(R.string.sendToSupport_text)
        )
    }


}