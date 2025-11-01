package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data

import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.PlaylistTrackCrossEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.TrackToPlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.util.AppDatabase
import com.bignerdranch.playlistmaker.util.TrackMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val playlistMapper: PlaylistMapper,
    private val trackMapper: TrackMapper
): PlaylistRepository {

    // создаем плейлист в NewPlaylistViewModel
    override suspend fun createPlaylist(playlist: PlaylistModel) {
        db.playlistDao().createPlaylist(playlistMapper.mapToPlaylistEntity(playlist))
    }

    override fun getPlaylistById(playlistId: Long): Flow<PlaylistModel> {
        return db.playlistDao()
            .getPlaylistById(playlistId)
            .map { playlistEntity ->
                playlistMapper.mapToPlaylistModel(playlistEntity)
            }
    }


    // получаем в AudioPlayerViewModel и PlaylistViewModel
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return db.playlistDao()
            .getPlaylists()
            .map { playlists ->
                playlists.map { entity ->
                    playlistMapper.mapToPlaylistModel(entity)
                }
            }
    }



    override suspend fun addTrackToPlaylist(trackModel: Track, playlistId: Long): Boolean {
       return withContext(Dispatchers.IO) {

           val trackEntity = trackMapper.mapToPlaylistTrackEntity(trackModel)
           // проверяем имеется ли трек в бд
           val existingTrack = findTrackById(trackEntity.trackId)

           val trackId = if (existingTrack != null) {

               // проверяем есть ли трек в плейлисте. если есть - возвращаем false
               if (db.crossRefDao().isTrackInPlaylist(playlistId, existingTrack.trackId)) {
                   return@withContext false
               }

               // если трека нет в плейлисте, но трек уже есть в бд - возвращаем ID существующего трека
               existingTrack.trackId

           } else {
               // если трека нет в базе - создаем новый
               db.trackToPlaylistDao().insertTrack(trackEntity)
           }

           // позиция для нового трека
           val position = db.crossRefDao().getTrackCount(playlistId)

           val crossRef = PlaylistTrackCrossEntity(
               playlistId = playlistId,
               trackId = trackId,
               position = position
           )

           db.crossRefDao().insertCrossRef(crossRef)
           db.playlistDao().incrementTrackCount(playlistId)

           return@withContext true
       }

    }
    private suspend fun findTrackById(trackId: Long): TrackToPlaylistEntity? {
        return db.trackToPlaylistDao().findTrackById(trackId)
    }




    override fun getTracksFromPlaylist(playlistId: Long): Flow<List<Track>> {
        return db.crossRefDao()
            .getTracksForPlaylist(playlistId)
            .map { tracks ->
                tracks.map { entity ->
                    trackMapper.mapToTrackModel(entity)
                }
            }
    }



    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        db.crossRefDao().deleteTrackFromPlaylist(playlistId, trackId)
        db.playlistDao().decrementTrackCount(playlistId)

        // Проверить, имеется ли трек хотя бы в 1 плейлисте - если нет - удалить из таблицы "tracks_in_playlist"
        db.trackToPlaylistDao().deleteTrackIfUnused(trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        // получаем trackId из плейлиста
        val tracksIds = db.playlistDao().getTrackIdsFromPlaylist(playlistId)

        // УДАЛИТЬ СВЯЗИ из playlist_track_cross_ref
        db.playlistDao().deleteAllTracksFromPlaylist(playlistId)

        // удаляем сам плейлист
        db.playlistDao().deletePlaylist(playlistId)

        //  Очищаем неиспользуемые треки
        tracksIds.forEach { id ->
            db.trackToPlaylistDao().deleteTrackIfUnused(id)
        }

    }


    override suspend fun updatePlaylist(playlist: PlaylistModel) {
        db.playlistDao().updatePlaylist(playlistMapper.mapToPlaylistEntity(playlist))
    }


}




