package com.bignerdranch.playlistmaker.media

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel

@Composable
fun PlaylistsCompose(
    onPlaylistClicked: (PlaylistModel) -> Unit,
    onCreatePlaylistClicked: () -> Unit
) {

    Text("Playlist tabs")
}