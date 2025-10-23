package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import android.net.Uri

data class PlaylistModel(
    val coverUri: Uri,
    val name: String,
    val description: String,
    val tracks: MutableList<String> = mutableListOf()
) {
}