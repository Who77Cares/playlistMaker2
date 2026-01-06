package com.bignerdranch.playlistmaker.search.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.db_favorite.ui.TrackItem

import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchCompose(
    onTrackClicked: (Track) -> Unit
) {
    val viewModel: SearchViewModel = koinViewModel()
    val state by viewModel.state.observeAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    // Загружаем историю при первом отображении
    LaunchedEffect(Unit) {
        viewModel.getCurrentState()?.let {
            // Уже есть состояние
        } ?: run {
            viewModel.loadHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.colors_FFFFFFFF_1A1B22))
            .padding(
                top = dimensionResource(R.dimen.dimen14),
                start = dimensionResource(R.dimen.dimen16),
                end = dimensionResource(R.dimen.dimen16)
            )
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.search),
            color = colorResource(R.color.colors_FF000000_FFFFFFFF),
            fontSize = dimensionResource(R.dimen.sp22).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Поле поиска
        SearchTextField(
            onTextChanged = { text ->
                viewModel.searchDebounce(text)
            },
            onClearClick = {
                viewModel.loadHistory()
            }
        )

        // Основной контент
        when (val currentState = state) {
            is TrackState.Loading -> {
                LoadingState()
            }
            is TrackState.Content -> {
                TrackListState(
                    tracks = currentState.tracks,
                    onTrackClick = { track ->
                        coroutineScope.launch {
                            if (viewModel.clickDebounce()) {
                                onTrackClicked(track)
                                viewModel.addTrackToHistory(track)
                            }
                        }
                    }
                )
            }
            is TrackState.Empty -> {
                EmptyState(message = currentState.message)
            }
            is TrackState.Error -> {
                ErrorState(
                    errorMessage = currentState.errorMessage,
                    onRetry = {
                        viewModel.retryLastSearch()
                    }
                )
            }
            is TrackState.History -> {
                HistoryState(
                    historyTracks = currentState.historyTracks,
                    onTrackClick = { track ->
                        coroutineScope.launch {
                            if (viewModel.clickDebounce()) {
                                onTrackClicked(track)
                            }
                        }
                    },
                    onClearHistory = {
                        viewModel.clearHistory()
                    }
                )
            }
            null -> {
                // Ничего не показываем пока грузится начальное состояние
            }
        }
    }
}

@Composable
fun SearchTextField(
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = colorResource(R.color.searh_box),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка поиска
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null,
                tint = colorResource(R.color.search_hint),
                modifier = Modifier.padding(end = 8.dp)
            )

            // Поле ввода
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    onTextChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            keyboardController?.hide()
                        }
                    },
                textStyle = TextStyle(
                    color = colorResource(R.color.main_text_buttons),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular))
                ),
                cursorBrush = SolidColor(colorResource(R.color.color_3772E7)),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            color = colorResource(R.color.search_hint),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.ys_display_regular))
                        )
                    }
                    innerTextField()
                }
            )

            // Кнопка очистки (крестик)
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        text = ""
                        onTextChanged("")
                        onClearClick()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close_icon),
                        contentDescription = null,
                        tint = colorResource(R.color.close_button)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackListState(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(R.dimen.dimen24))
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
fun HistoryState(
    historyTracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    if (historyTracks.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // ← ВАЖНО
        ) {
            // Верхняя часть: заголовок и список
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок истории
                Text(
                    text = stringResource(R.string.your_search),
                    color = colorResource(R.color.colors_1A1B22_FFFFFF),
                    fontSize = 19.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.dimen12))
                )

                // Список истории
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.dimen24))
                ) {
                    items(historyTracks) { track ->
                        TrackItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }

            Button(
                onClick = onClearHistory,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.colors_1A1B22_FFFFFF),
                    contentColor = colorResource(R.color.colors_FFFFFFFF_1A1B22)
                ),
                shape = RoundedCornerShape(54.dp)
            ) {
                Text(
                    text = stringResource(R.string.clean_search_list),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium))
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(
            color = colorResource(R.color.color_3772E7),
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.error_nothing_found),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = message,
            color = colorResource(R.color.colors_1A1B22_FFFFFF),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.dimen12))
        )
    }
}

@Composable
fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.error_connection),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = errorMessage,
            color = colorResource(R.color.colors_1A1B22_FFFFFF),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.dimen12))
        )

        Text(
            text = stringResource(R.string.connection_error_text2),
            color = colorResource(R.color.colors_1A1B22_FFFFFF),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.dimen24)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.colors_1A1B22_FFFFFF),
                contentColor = colorResource(R.color.colors_FFFFFFFF_1A1B22)
            ),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(R.string.reConnect),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium))
            )
        }
    }
}