package com.bignerdranch.playlistmaker.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteTracksCompose
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.media.playlists.PlaylistsCompose

import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.launch

@Composable
fun MediaContainerCompose(
    onTrackClicked: (Track) -> Unit,
    onPlaylistClicked: (PlaylistModel) -> Unit,
    onCreatePlaylistClicked: () -> Unit

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colors_FFFFFFFF_1A1B22))
            .padding(
                top = dimensionResource(R.dimen.dimen14),
                start = dimensionResource(R.dimen.dimen16),
                end = dimensionResource(R.dimen.dimen16)
            )
    ) {
        Text(
            text = stringResource(R.string.media),
            color = colorResource(R.color.colors_FF000000_FFFFFFFF),
            fontSize = dimensionResource(R.dimen.sp22).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tab Layout с Pager
        MediaTabs(
            onTrackClicked = onTrackClicked,
            onPlaylistClicked = onPlaylistClicked,
            onCreatePlaylistClicked = onCreatePlaylistClicked
        )

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaTabs(
    onTrackClicked: (Track) -> Unit,
    onPlaylistClicked: (PlaylistModel) -> Unit,
    onCreatePlaylistClicked: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()


    TabRow(
        selectedTabIndex = pagerState.currentPage,
        contentColor = colorResource(R.color.colors_1A1B22_FFFFFF),
        backgroundColor = colorResource(R.color.colors_FFFFFFFF_1A1B22),
        indicator = { tabPosition ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(
                    currentTabPosition = tabPosition[pagerState.currentPage]
                ),
                height = 2.dp
            )
        }

        ) {

        Tab(
            selected = pagerState.currentPage == 0,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            text = {
                Text(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),

                    text = stringResource(R.string.tab1),
                    color = if (pagerState.currentPage == 0) {
                        colorResource(R.color.colors_1A1B22_FFFFFF)
                    } else {
                        colorResource(R.color.colors_1A1B22_FFFFFF)
                    }
                )
            }
        )

        // Вкладка 2: Плейлисты
        Tab(
            selected = pagerState.currentPage == 1,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            },
            text = {
                Text(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),

                    text = stringResource(R.string.tab2),
                    color = if (pagerState.currentPage == 1) {
                        colorResource(R.color.colors_1A1B22_FFFFFF)
                    } else {
                        colorResource(R.color.colors_1A1B22_FFFFFF)
                    }
                )
            }
        )

    }

    // Horizontal Pager
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> FavoriteTracksCompose(onTrackClicked = onTrackClicked)
            1 -> PlaylistsCompose(
                onPlaylistClicked = onPlaylistClicked,
                onCreatePlaylistClicked = onCreatePlaylistClicked,
            )
        }
    }
}