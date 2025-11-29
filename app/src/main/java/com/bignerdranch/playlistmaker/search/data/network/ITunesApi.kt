package com.bignerdranch.playlistmaker.search.data.network

import com.bignerdranch.playlistmaker.search.data.network.models.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    @GET("/search?entity=song")
    suspend fun findTrack(@Query("term") text: String) : TrackResponse

}