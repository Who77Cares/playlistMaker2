package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import android.net.Uri

data class PlaylistModel(
// используется только для того чтобы по id находить такой же PlaylistEntity в room
    val id: Long,
    val coverUri: Uri,
    val name: String,
    val description: String,
    val tracks: MutableList<String> = mutableListOf()
) {
}