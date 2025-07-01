package com.bignerdranch.playlistmaker.search.ui.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.playlistmaker.mainScreen.MainActivity
import com.bignerdranch.playlistmaker.databinding.ActivitySearchBinding
import com.bignerdranch.playlistmaker.search.ui.models.TrackState
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.search.ui.presentation.SearchViewModel
import com.bignerdranch.playlistmaker.audio.ui.ui.AudioPlayer


class SearchActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener {

    private var viewModel: SearchViewModel? = null
    private lateinit var binding: ActivitySearchBinding


    private var searchText: String = ""


    private val adapter = SearchAdapter(false, this)
    private val adapterForHistoryTracks = SearchAdapter(true, this)

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true


    private var handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observerState()?.observe(this) {
            render(it)
        }

        // логика работы RecycleView
        adapter.tracks = tracks
        binding.trackRecycleView.adapter = adapter

        adapterForHistoryTracks.tracks = historyTracks
        binding.trackHistoryRecycleView.adapter = adapterForHistoryTracks


        viewModel?.loadHistory()


        // Cлушатель TextWatcher (изменения текста) в едиттексте
        binding.searchEditText.addTextChangedListener(textWatcher)


        // Очищаем содержимое EditText и прячем клаввиатуру при нажатии на крест
        binding.closeImageViewButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
            viewModel?.loadHistory()

        }

        binding.arrowBackButton.setOnClickListener {
            val intent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.tracksHistoryClearButton.setOnClickListener {
            viewModel?.clearHistory()

        }

        // Повторный запрос в iTunes
        binding.updateButton.setOnClickListener {
            viewModel?.searchDebounce(binding.searchEditText.text.toString())
        }

    }

    // открываем трек
    override fun onItemClick(track: Track, trackFromHistory: Boolean) {
        if (clickDebounce()) {
            val intent = Intent(this, AudioPlayer::class.java).apply {
                putExtra("trackName", track.trackName)
                putExtra("artistName", track.artistName)
                putExtra("durationTime", track.trackTimeMillis)
                putExtra("album", track.collectionName)
                putExtra("songYear", track.releaseDate)
                putExtra("songStyle", track.primaryGenreName)
                putExtra("songCountry", track.country)
                putExtra("songCover", track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                putExtra("previewUrl", track.previewUrl)
            }
            startActivity(intent)
        }
        // если нажатый трек не из истории -> добавляем в историю
        if (trackFromHistory) return
        viewModel?.addTrackToHistory(track)
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(p0: Editable?) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.closeImageViewButton.visibility =
                if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            searchText = s?.toString()?.trim() ?: ""

            viewModel?.searchDebounce(newText = searchText)

            if (searchText.isEmpty()) {
                viewModel?.loadHistory()
            }
        }
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    // отмена случайного двойного нажатия
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    // обработка состояния и ниже функции "отрисовывающие" нужное состояние
    private fun render(state: TrackState) {
        Log.d("SearchActivity", "Render called with state: ${state::class.simpleName}")
        when (state) {
            is TrackState.Content -> showContent(state.tracks)
            is TrackState.Empty -> showEmpty(state.message)
            is TrackState.Error -> showError(state.errorMessage)
            TrackState.Loading -> showLoading()
            is TrackState.History -> showHistory(state.historyTracks)
        }
    }


    private fun showContent(tracks: List<Track>) {
        binding.apply {
            trackRecycleView.visibility = View.VISIBLE

            layoutForHistoryTracks.visibility = View.GONE
            placeholderLayoutNotFound.visibility = View.GONE
            placeholderLayoutConnectionError.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()

    }


    private fun showEmpty(emptyMessage: String) {
        binding.apply {
            placeholderLayoutNotFound.visibility = View.VISIBLE

            trackRecycleView.visibility = View.GONE
            layoutForHistoryTracks.visibility = View.GONE
            placeholderLayoutConnectionError.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }


    private fun showError(errorMessage: String) {
        binding.apply {
            placeholderLayoutConnectionError.visibility = View.VISIBLE

            placeholderLayoutNotFound.visibility = View.GONE
            trackRecycleView.visibility = View.GONE
            layoutForHistoryTracks.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }


    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            placeholderLayoutConnectionError.visibility = View.GONE
            placeholderLayoutNotFound.visibility = View.GONE
            trackRecycleView.visibility = View.GONE
            layoutForHistoryTracks.visibility = View.GONE
        }
    }


    private fun showHistory(historyTracks: List<Track>) {
        if (historyTracks.isNotEmpty()) {
            binding.layoutForHistoryTracks.visibility = View.VISIBLE
            adapterForHistoryTracks.tracks.clear()
            adapterForHistoryTracks.tracks.addAll(historyTracks)
            adapterForHistoryTracks.notifyDataSetChanged()
        } else {
            binding.layoutForHistoryTracks.visibility = View.GONE
        }

        binding.apply {
            placeholderLayoutNotFound.visibility = View.GONE
            placeholderLayoutConnectionError.visibility = View.GONE
            trackRecycleView.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }


}
