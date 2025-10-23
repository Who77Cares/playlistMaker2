package com.bignerdranch.playlistmaker.media.new_playlist

import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.new_playlist.db_playlists.PlaylistEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistMapper {

    private val gson = Gson()

    @TypeConverter
    fun fromList(value: List<String>?): String {
        // если список null — сохраняем пустой JSON
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toList(value: String?): List<String> {
        // если в базе null или пустая строка — возвращаем пустой список
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    fun mapToPlaylistModel(playlistData: PlaylistEntity): PlaylistModel {
        return PlaylistModel(
            coverUri = playlistData.coverUri.toUri(),
            name = playlistData.name,
            description = playlistData.description,
            tracks = playlistData.tracks
        )
    }

    fun mapToPlaylistEntity(playlist: PlaylistModel): PlaylistEntity {
        return PlaylistEntity(
            id = 0L,
            creationTime = System.currentTimeMillis(),
            coverUri = playlist.coverUri.toString(),
            name = playlist.name,
            description = playlist.description,
            tracks = playlist.tracks
        )
    }


}