package com.bignerdranch.playlistmaker.di


import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.SearchHistoryInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.interactorImpl.TrackInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

}