package com.bignerdranch.playlistmaker.ui.searchTrack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.unsorted.MainActivity
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.TrackState
import com.bignerdranch.playlistmaker.databinding.TrackViewBinding
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.models.Track
import com.bignerdranch.playlistmaker.search.SearchPreferences
import com.bignerdranch.playlistmaker.ui.presentation.SearchViewModel
import com.bignerdranch.playlistmaker.unsorted.AudioPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_LIST: String = "search_list"

class SearchActivity: AppCompatActivity(), SearchAdapter.OnItemClickListener {


    private var viewModel: SearchViewModel? = null

    private lateinit var searchEditText:EditText
    private lateinit var arrowBackButton:ImageView
    private lateinit var closeImageView:ImageView

    private lateinit var trackHistoryRecycleView: RecyclerView
    private lateinit var trackRecycleView: RecyclerView

    private lateinit var placeholderLayoutNotFound: LinearLayout
    private lateinit var placeholderLayoutConnectionError: LinearLayout

    private lateinit var layoutForHistoryTracks: LinearLayout
    private lateinit var updateButton:Button
    private lateinit var tracksHistoryClearButton: Button
    private lateinit var progressBar: ProgressBar

    private var searchText: String = ""


    private val adapter = SearchAdapter(false, this)
    private val adapterForHistoryTracks = SearchAdapter(true, this)

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    private var handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true


    private val searchPreferences = SearchPreferences()




    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)



        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observerState()?.observe(this) {
            render(it)
        }

        searchEditText = findViewById(R.id.search_editText)
        arrowBackButton = findViewById(R.id.arrow_back_search)
        closeImageView = findViewById(R.id.close_ImageView_button)

        trackHistoryRecycleView = findViewById(R.id.track_history_list)
        trackRecycleView = findViewById(R.id.track_list)

        placeholderLayoutNotFound = findViewById(R.id.placeholderLayout_notFound)
        placeholderLayoutConnectionError = findViewById(R.id.placeholderLayout_connectionError)
        updateButton = findViewById(R.id.updateButton)
        layoutForHistoryTracks = findViewById(R.id.searched_tracks)
        tracksHistoryClearButton = findViewById(R.id.searched_tracksButton_clear)
        progressBar = findViewById(R.id.progressBar)


        // логика работы RecycleView
        adapter.tracks = tracks
        trackRecycleView.adapter = adapter

        adapterForHistoryTracks.tracks = historyTracks
        trackHistoryRecycleView.adapter = adapterForHistoryTracks


        viewModel?.loadHistory()
        viewModel?.updateHistory()


        // Добавление TextWatcher после восстановления текста
        searchEditText.addTextChangedListener(simpleTextWatcher)


        // Очищаем содержимое EditText и прячем клаввиатуру при нажатии на крест
        closeImageView.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            viewModel?.updateHistory()

        }

        arrowBackButton.setOnClickListener {
            val intent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(intent)
        }

        tracksHistoryClearButton.setOnClickListener {
            historyTracks.clear()
            adapterForHistoryTracks.notifyDataSetChanged()
            searchPreferences.write(getSharedPreferences(SEARCH_LIST, MODE_PRIVATE), historyTracks)
            layoutForHistoryTracks.visibility = View.GONE

        }

//
//        // убираем список сохраненных песен при фокусе на editText
//        searchEditText.setOnFocusChangeListener { view, hasFocus ->
//
//            layoutForHistoryTracks.visibility =
//                if (hasFocus && searchEditText.text.isEmpty()) View.INVISIBLE else View.VISIBLE
//        }



        // Повторный запрос в iTunes
        updateButton.setOnClickListener {

            viewModel?.searchDebounce(searchEditText.text.toString())
        }




    }

    // сохраняем историю поиска песен
    override fun onStop() {
        super.onStop()
        viewModel?.saveHistory()
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

        if (trackFromHistory) return

        // добавляем в список истории
        viewModel?.addTrackToHistory(track)

    }


    private val simpleTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // empty
        }
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            closeImageView.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            searchText = s?.toString()?.trim() ?: ""

            if(searchText.isEmpty()) {
                viewModel?.updateHistory()
                return
            }

            viewModel?.searchDebounce(newText = searchText)

        }
        override fun afterTextChanged(p0: Editable?) {
        }
    }



    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }



    // отмена случайного двойного нажатия
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


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

        trackRecycleView.visibility = View.VISIBLE

        layoutForHistoryTracks.visibility = View.GONE
        placeholderLayoutNotFound.visibility = View.GONE
        placeholderLayoutConnectionError.visibility = View.GONE
        progressBar.visibility = View.GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()

    }

    private fun showEmpty(emptyMessage: String) {
        placeholderLayoutNotFound.visibility = View.VISIBLE

        trackRecycleView.visibility = View.GONE
        layoutForHistoryTracks.visibility = View.GONE
        placeholderLayoutConnectionError.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
        placeholderLayoutConnectionError.visibility = View.VISIBLE

        placeholderLayoutNotFound.visibility = View.GONE
        trackRecycleView.visibility = View.GONE
        layoutForHistoryTracks.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showLoading() {

        progressBar.visibility = View.VISIBLE

        placeholderLayoutConnectionError.visibility = View.GONE
        placeholderLayoutNotFound.visibility = View.GONE
        trackRecycleView.visibility = View.GONE
        layoutForHistoryTracks.visibility = View.GONE

    }

    private fun showHistory(historyTracks: List<Track>) {

        layoutForHistoryTracks.visibility = View.VISIBLE

        placeholderLayoutNotFound.visibility = View.GONE
        placeholderLayoutConnectionError.visibility = View.GONE
        trackRecycleView.visibility = View.GONE
        progressBar.visibility = View.GONE

        adapterForHistoryTracks.tracks.clear()
        adapterForHistoryTracks.tracks.addAll(historyTracks)
        adapterForHistoryTracks.notifyDataSetChanged()
    }
}
