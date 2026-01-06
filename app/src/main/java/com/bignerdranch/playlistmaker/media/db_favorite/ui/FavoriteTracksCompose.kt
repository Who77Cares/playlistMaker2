package com.bignerdranch.playlistmaker.media.db_favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.search.domain.network.Track
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FavoriteTracksCompose(
    onTrackClicked: (Track) -> Unit
) {

    val viewModel: FavoriteTracksViewModel = koinViewModel()

    val state by viewModel.observeState().observeAsState(initial = FavoriteTrackState.Empty(message = "empty"))


    when (state) {
        is FavoriteTrackState.Content -> {
            val tracks = (state as FavoriteTrackState.Content).tracks
            FavoriteTracksList(tracks = tracks, onTrackClicked = onTrackClicked)
        }
        is FavoriteTrackState.Empty -> {
            EmptyFavoriteState()
        }
    }
}

@Composable
fun FavoriteTracksList(
    tracks: List<Track>,
    onTrackClicked: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(R.dimen.dimen24)),

    ) {
        items(tracks) { track ->
            TrackItem(track = track, onClick = { onTrackClicked(track) })
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),

        verticalAlignment = Alignment.CenterVertically
    ) {
        // Обложка трека
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .padding(end = 8.dp)
        )

        // Основной контент
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center // Центрируем содержимое
        ) {
            Text(
                text = track.trackName,
                color = colorResource(R.color.colors_FF000000_FFFFFFFF),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Исполнитель и время
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = track.artistName,
                    color = colorResource(R.color.color_AEAFB4_FFFFFFFF),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = colorResource(R.color.color_AEAFB4_FFFFFFFF),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(track.trackTimeMillis),
                    color = colorResource(R.color.color_AEAFB4_FFFFFFFF),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular))
                )
            }
        }

        // Стрелка
        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null,
            tint = colorResource(R.color.color_AEAFB4_FFFFFFFF),
            modifier = Modifier
                .size(30.dp)
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun EmptyFavoriteState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.error_nothing_found),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.your_media_empty),
            color = colorResource(R.color.colors_FF000000_FFFFFFFF),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
