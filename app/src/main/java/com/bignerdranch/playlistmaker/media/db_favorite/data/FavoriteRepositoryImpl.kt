package com.bignerdranch.playlistmaker.media.db_favorite.data

import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.media.db_favorite.data.db.AppDatabase
import com.bignerdranch.playlistmaker.media.db_favorite.data.db.TrackEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track

class FavoriteRepositoryImpl(
    val db: AppDatabase,
    val mapper: TrackMapper
): FavoriteRepository {

    override suspend fun addTrack(track: Track) {
        db.trackDao().addTrackToFavorite(mapper.mapToEntity(track))
    }

    override suspend fun removeTrack(track: Track) {
        db.trackDao().removeTrackFromFavorite(mapper.mapToEntity(track))
    }



    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return db.trackDao().isTrackFavorite(trackId)
    }

}