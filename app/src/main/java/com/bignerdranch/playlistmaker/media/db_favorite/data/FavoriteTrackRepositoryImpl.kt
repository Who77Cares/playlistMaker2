package com.bignerdranch.playlistmaker.media.db_favorite.data

import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.util.AppDatabase
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    val db: AppDatabase,
    val mapper: TrackMapper
): FavoriteTrackRepository {

    override suspend fun addTrack(track: Track) {
        db.trackDao().addTrackToFavorite(mapper.mapToEntity(track))
    }

    override suspend fun removeTrack(track: Track) {
        db.trackDao().removeTrackFromFavorite(mapper.mapToEntity(track))
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return db.trackDao().isTrackFavorite(trackId)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return db.trackDao()
            .getAllTracks()// Flow<List<TrackEntity>>
            .map { list -> mapper.convertFromTrackEntity(list) } // map преобразует List<TrackEntity> -> List<Track>
    }

}