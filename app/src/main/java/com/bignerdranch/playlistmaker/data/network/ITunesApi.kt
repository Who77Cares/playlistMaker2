package com.bignerdranch.playlistmaker.data.network

import com.bignerdranch.playlistmaker.data.models.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    @GET("/search?entity=song")
    fun findTrack(@Query("term") text: String) : Call<TrackResponse>

}