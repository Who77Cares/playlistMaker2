package com.bignerdranch.playlistmaker.data

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.data.models.TrackRequest
import com.bignerdranch.playlistmaker.data.models.TrackResponse
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository  {

    override fun searchTrack(expression: String): Resource<List<Track>> {

        val response = networkClient.doRequest(TrackRequest(expression))

        return  when(response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                val trackResponce = response as TrackResponse
                val result = trackResponce.results

                if (result.isEmpty()) {
                    Resource.Error("Ничего не найдено")
                } else {
                    Resource.Success(
                        result.map {
                            Track(
                                it.trackName,
                                it.artistName,
                                it.trackTimeMillis,
                                it.artworkUrl100,
                                it.trackId,
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName,
                                it.country,
                                it.previewUrl
                            )
                        }
                    )
                }
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }


//        if (response.resultCode == 200) {
//            val track = (response as TrackResponse).results.map {
//                Track(
//                    it.trackName,
//                    it.artistName,
//                    it.trackTimeMillis,
//                    it.artworkUrl100,
//                    it.trackId,
//                    it.collectionName,
//                    it.releaseDate,
//                    it.primaryGenreName,
//                    it.country,
//                    it.previewUrl
//                )
//            }
//            return track
//
//        } else {
//            return emptyList()
//        }
    }
}