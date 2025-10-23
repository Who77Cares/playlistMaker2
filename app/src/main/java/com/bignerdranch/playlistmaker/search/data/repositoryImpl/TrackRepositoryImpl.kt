package com.bignerdranch.playlistmaker.search.data.repositoryImpl

import com.bignerdranch.playlistmaker.util.Resource
import com.bignerdranch.playlistmaker.search.data.client.NetworkClient
import com.bignerdranch.playlistmaker.search.data.models.TrackRequest
import com.bignerdranch.playlistmaker.search.data.models.TrackResponse
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository  {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(TrackRequest(expression))

        when(response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                val trackResponse = response as TrackResponse
                val result = trackResponse.results



                if (result.isEmpty()) {
                    emit(Resource.Error("Ничего не найдено"))
                } else {
                    val data =
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
                                it.previewUrl ?: ""

                            )

                        }
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}