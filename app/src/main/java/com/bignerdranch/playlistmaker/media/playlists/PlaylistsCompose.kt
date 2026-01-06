package com.bignerdranch.playlistmaker.media.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun PlaylistsCompose(
    onPlaylistClicked: (PlaylistModel) -> Unit,
    onCreatePlaylistClicked: () -> Unit
) {
    val viewModel: PlaylistViewModel = koinViewModel()
    val state by viewModel.observeStateLiveData().observeAsState(
        initial = PlaylistViewModel.PlaylistState.Empty
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.colors_FFFFFFFF_1A1B22))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.dimen24)), // marginTop 24dp
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка создания плейлиста (wrap_content)
            Button(
                onClick = onCreatePlaylistClicked,
                modifier = Modifier
                    .wrapContentSize()
                    .height(48.dp),

                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.colors_1A1B22_FFFFFF), // backgroundTint
                    contentColor = colorResource(R.color.colors_FFFFFFFF_1A1B22) // textColor
                ),
                shape = RoundedCornerShape(54.dp) // cornerRadius 54dp
            ) {
                Text(
                    text = stringResource(R.string.new_playlist),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium))
                )
            }

            // Контент
            when (state) {
                is PlaylistViewModel.PlaylistState.Content -> {
                    val playlists = (state as PlaylistViewModel.PlaylistState.Content).playlists
                    PlaylistGrid(
                        playlists = playlists,
                        onPlaylistClicked = onPlaylistClicked
                    )
                }
                is PlaylistViewModel.PlaylistState.Empty -> {
                    EmptyPlaylistState()
                }
            }
        }
    }
}



@Composable
fun PlaylistGrid(
    playlists: List<PlaylistModel>,
    onPlaylistClicked: (PlaylistModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onClick = { onPlaylistClicked(playlist) }
            )
        }
    }
}


@Composable
fun PlaylistItem(
    playlist: PlaylistModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        elevation = 0.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // ВСЁ РЕШЕНИЕ В ЭТОЙ ОДНОЙ СТРОКЕ:
                AsyncImage(
                    model = playlist.coverUri?.toString(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder)
                )
            }


            // Информация о плейлисте
            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = playlist.name,
                    color = colorResource(R.color.colors_FF000000_FFFFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Количество треков с plural
                val trackCount = playlist.tracksSize
                Text(
                    text = getTracksText(trackCount),
                    color = colorResource(R.color.color_AEAFB4_FFFFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontWeight = FontWeight.Normal,

                )
            }
        }
    }
}


@Composable
fun getTracksText(count: Int): String {
    val context = LocalContext.current
    return context.resources.getQuantityString(
        R.plurals.tracks_count,
        count,
        count
    )
}


@Composable
fun EmptyPlaylistState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 45.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Изображение
        Image(
            painter = painterResource(R.drawable.error_nothing_found),
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize() // wrap_content
        )

        // Текст
        Text(
            text = stringResource(R.string.your_playlist_empty),
            color = colorResource(R.color.colors_1A1B22_FFFFFF),
            fontSize = 19.sp, // textSize="19sp"
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .wrapContentSize() // wrap_content
                .padding(top = dimensionResource(R.dimen.dimen12))
        )
    }
}