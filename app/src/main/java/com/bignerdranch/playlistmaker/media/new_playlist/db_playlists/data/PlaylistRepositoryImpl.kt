package com.bignerdranch.playlistmaker.new_playlist.db_playlists

import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistRepository
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val mapper: PlaylistMapper
): PlaylistRepository {

    // создаем в NewPlaylistViewModel
    override suspend fun createPlaylist(playlist: PlaylistModel) {
        db.playlistDao().createPlaylist(mapper.mapToPlaylistEntity(playlist))
    }

    // получаем в AudioPlayerViewModel и PlaylistViewModel
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return db.playlistDao()
            .getPlaylists()
            .map { playlists ->
                playlists.map { entity ->
                    mapper.mapToPlaylistModel(entity)
                }
            }
    }

    override fun addTrackToPlaylist(
        playlistId: Long,
        trackId: String
    ): Flow<String?> = flow {
        val playlist = db.playlistDao().getPlaylistById(playlistId)
        if (trackId in playlist.tracks) {
            emit(null)
        } else {
            playlist.tracks.add(trackId)
            db.playlistDao().updatePlaylist(playlist)
            emit(playlist.name) // возвращаем имя плейлиста для вывода в тост если трек успешно добавлен
        }
    }.flowOn(Dispatchers.IO)

}


