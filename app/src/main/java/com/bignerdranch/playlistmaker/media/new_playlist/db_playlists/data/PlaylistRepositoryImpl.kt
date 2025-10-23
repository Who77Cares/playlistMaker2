package com.bignerdranch.playlistmaker.new_playlist.db_playlists

import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistRepository
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.util.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val mapper: PlaylistMapper
): PlaylistRepository {

    override suspend fun createPlaylist(playlist: PlaylistModel) {
        db.playlistDao().createPlaylist(mapper.mapToPlaylistEntity(playlist))
    }

    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return db.playlistDao()
            .getPlaylists()
            .map { playlists ->
                playlists.map { entity ->
                    mapper.mapToPlaylistModel(entity)
                }
            }
    }

}