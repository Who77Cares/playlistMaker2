package com.bignerdranch.playlistmaker.data

import com.bignerdranch.playlistmaker.data.models.TrackRequest
import com.bignerdranch.playlistmaker.data.models.TrackResponse
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository  {
    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
           val track =  (response as TrackResponse).results.map {
                Track(it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl)
            }
            return track

        } else {
            return emptyList()
        }
    }
}