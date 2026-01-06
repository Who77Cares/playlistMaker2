package com.bignerdranch.playlistmaker.media

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.bignerdranch.playlistmaker.search.domain.network.Track

@Composable
fun FavoriteTracksCompose(
    onTrackClicked: (Track) -> Unit
) {

    Text("Фаворит Таб")

}