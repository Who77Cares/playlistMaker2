package com.bignerdranch.playlistmaker.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.MainActivity
import com.bignerdranch.playlistmaker.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_LIST: String = "search_list"

class SearchActivity: AppCompatActivity(), SearchAdapter.OnItemClickListener {

    private lateinit var searchEditText:EditText
    private lateinit var arrowBackButton:ImageView
    private lateinit var closeImageView:ImageView

    private lateinit var recyclerViewForSearch: RecyclerView
    private lateinit var recyclerView: RecyclerView

    private lateinit var placeholderLayoutNotFound: LinearLayout
    private lateinit var placeholderLayoutConnectionError: LinearLayout
    private lateinit var layoutForSearchList: LinearLayout
    private lateinit var updateButton:Button
    private lateinit var searchTracksClearButton: Button
    private lateinit var progressBar: ProgressBar

    private var searchText: String? = null

    private val adapter = SearchAdapter(this, isClickable = true)
    private val adapterForSearch = SearchAdapter(this, isClickable = false)

    private val tracks = ArrayList<Track>()
    private val searchTracks = ArrayList<Track>()

    private val gson = Gson()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)
    private val searchPreferences = SearchPreferences()


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private val searchRunnable = Runnable { fetchTracks(searchEditText.text.toString()) }
    private var handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.search_editText)
        arrowBackButton = findViewById(R.id.arrow_back_search)
        closeImageView = findViewById(R.id.close_ImageView_button)
        recyclerViewForSearch = findViewById(R.id.search_list)
        recyclerView = findViewById(R.id.track_list)
        placeholderLayoutNotFound = findViewById(R.id.placeholderLayout_notFound)
        placeholderLayoutConnectionError = findViewById(R.id.placeholderLayout_connectionError)
        updateButton = findViewById(R.id.updateButton)
        layoutForSearchList = findViewById(R.id.searched_tracks)
        searchTracksClearButton = findViewById(R.id.searched_tracksButton_clear)
        progressBar = findViewById(R.id.progressBar)




        // логика работы RecycleView
        adapter.tracks = tracks
        recyclerView.adapter = adapter

        adapterForSearch.tracks = searchTracks
        recyclerViewForSearch.adapter = adapterForSearch


        // Загружаем сохраненные данные о истории поиска песен
        val savedSearchTracks = searchPreferences.read(getSharedPreferences(SEARCH_LIST, MODE_PRIVATE))
        searchTracks.addAll(savedSearchTracks)
        adapterForSearch.notifyDataSetChanged()

        layoutForSearchList.visibility = if (searchTracks.isNotEmpty()) View.VISIBLE else View.GONE


        // Добавление TextWatcher после восстановления текста
        searchEditText.addTextChangedListener(simpleTextWatcher)


        // Очищаем содержимое EditText и прячем клаввиатуру при нажатии на крест
        closeImageView.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            tracks.clear()
            adapter.notifyDataSetChanged()
            updatePlaceholders(showNotFound = false, showConnectionError = false, showViewSearch = true)
        }

        arrowBackButton.setOnClickListener {
            val intent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(intent)
        }

        searchTracksClearButton.setOnClickListener {
            searchTracks.clear()
            adapterForSearch.notifyDataSetChanged()
            searchPreferences.write(getSharedPreferences(SEARCH_LIST, MODE_PRIVATE), searchTracks)
            layoutForSearchList.visibility = View.GONE

        }



        // поиск в iTunes
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                fetchTracks(searchEditText.text.toString())
                hideKeyboard()
//                placeholderLayoutNotFound.visibility = View.GONE
                true
            } else {
                false
            }
        }

        // убираем список сохраненных песен при фокусе на editText
        searchEditText.setOnFocusChangeListener { view, hasFocus ->

            layoutForSearchList.visibility = if (hasFocus && searchEditText.text.isEmpty()) View.INVISIBLE else View.VISIBLE
        }

        // Повторный запрос в iTunes
        updateButton.setOnClickListener {
            fetchTracks(searchEditText.text.toString())
        }

    }

    // сохраняем историю поиска песен
    override fun onStop() {
        super.onStop()
        searchPreferences.write(getSharedPreferences(SEARCH_LIST, MODE_PRIVATE), searchTracks)

    }

    // реализация интерфейса из класса SearchAdapter для добавления нажатых треков в новый список
    override fun onItemClick(track: Track) {
        val existingTrack = searchTracks.find { it.trackId == track.trackId }

        if (existingTrack != null) {
            // Если трек найден, удаляем его
            searchTracks.remove(existingTrack)
        } else if (searchTracks.size >= 10) {
            // Если трек не найден, но в списке больше 10, удаляем последний
            searchTracks.removeAt(searchTracks.size - 1)
        }

        searchTracks.add(0, track)
        adapterForSearch.notifyDataSetChanged()

    }


    private val simpleTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // empty
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            closeImageView.visibility = if (p0.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchText = p0?.toString()

            layoutForSearchList.visibility = if (searchEditText.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE

            if (p0.isNullOrEmpty())  {
                updatePlaceholders(showNotFound = false, showConnectionError = false, showViewSearch = true)
                tracks.clear()
                adapter.notifyDataSetChanged()
            }

            searchDebounce()
        }
        override fun afterTextChanged(p0: Editable?) {
        }
    }


    // Сохранение состояния при изменении конфигурации устройста: введенный текст; список песен; плейсхолдеры
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchText = searchEditText.text.toString()
        outState.putString("searchText", searchText)

        val tracksJson = gson.toJson(tracks)
        outState.putString("tracks", tracksJson)


        outState.putInt("placeholderNotFoundVisibility", placeholderLayoutNotFound.visibility)
        outState.putInt("placeholderConnectionErrorVisibility", placeholderLayoutConnectionError.visibility)

    }

    // Восстановление состояния после изменения конфигурации устройста: введенный текст; список песен; плейсхолдеры
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText")
        searchEditText.setText(searchText)

        val savedTracksJson = savedInstanceState.getString("tracks")
        if (!savedTracksJson.isNullOrEmpty()) {
            val trackType = object : TypeToken<ArrayList<Track>>() {}.type
            val restoredTracks: ArrayList<Track> = gson.fromJson(savedTracksJson, trackType)
            tracks.clear()
            tracks.addAll(restoredTracks)
            adapter.notifyDataSetChanged()
        }


        placeholderLayoutNotFound.visibility = savedInstanceState.getInt("placeholderNotFoundVisibility", View.GONE)
        placeholderLayoutConnectionError.visibility = savedInstanceState.getInt("placeholderConnectionErrorVisibility", View.GONE)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // логика работы iTunesAPI
    private fun fetchTracks(searchQuery: String) {
        if (searchQuery.isEmpty()) return  // Не делать запрос, если строка пустая
        progressBar.visibility = View.VISIBLE
        iTunesService.findTrack(searchQuery).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                progressBar.visibility = View.GONE
                tracks.clear()
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    tracks.addAll(response.body()?.results!!)
                    adapter.notifyDataSetChanged()
                    updatePlaceholders(showNotFound = false, showConnectionError = false, showViewSearch = false)
                } else if (response.isSuccessful) {
                    adapter.notifyDataSetChanged()
                    updatePlaceholders(showNotFound = true, showConnectionError = false, showViewSearch = false)
                } else {
                    adapter.notifyDataSetChanged()
                    updatePlaceholders(showNotFound = false, showConnectionError = true, showViewSearch = false)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                tracks.clear()
                adapter.notifyDataSetChanged()
                updatePlaceholders(showNotFound = false, showConnectionError = true, showViewSearch = false)
            }
        })
    }


    private fun updatePlaceholders(showNotFound: Boolean, showConnectionError: Boolean, showViewSearch: Boolean) {
        placeholderLayoutNotFound.visibility = if (showNotFound) View.VISIBLE else View.GONE
        placeholderLayoutConnectionError.visibility = if (showConnectionError) View.VISIBLE else View.GONE
        layoutForSearchList.visibility = if (showViewSearch && searchTracks.isNotEmpty()) View.VISIBLE else View.GONE
    }

// отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (!searchEditText.text.isNullOrEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun isClickAllowed(): Boolean {
        return clickDebounce()
    }
}
