package com.bignerdranch.playlistmaker.search.data.repositoryImpl

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.search.data.client.StorageClient
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
): SearchHistoryRepository{
    override fun saveToHistory(t: Track) {
        val historyTracks = storage.getData() ?: arrayListOf()
        val existingTrack = historyTracks.find { it.trackId == t.trackId }

        if (existingTrack != null) {
            historyTracks.remove(existingTrack)
        } else if (historyTracks.size >= 10) {
            historyTracks.removeAt(historyTracks.size - 1)
        }

        historyTracks.add(0, t)
        storage.storageData(historyTracks)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getData() ?: listOf()
        return Resource.Success(tracks)
    }

    override fun clearHistory() {
       storage.clearData()
    }
}