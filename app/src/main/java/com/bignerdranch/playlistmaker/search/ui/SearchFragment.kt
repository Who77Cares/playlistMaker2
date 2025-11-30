package com.bignerdranch.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.databinding.FragmentSearchBinding
import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean

class SearchFragment: Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding  get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var iTunesAdapter: SearchAdapter
    private lateinit var prefsHistoryAdapter: SearchAdapter


    private val isClickAllowed = AtomicBoolean(true)
    private var searchText = ""


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        iTunesAdapter = SearchAdapter(
            onTrackClick = { track ->

                if (clickDebounce()) {
                    findNavController().navigate(
                        R.id.action_searchFragment_to_audioPlayerFragment,
                        AudioPlayerFragment.Companion.createArgs(track)
                    )

                    viewModel.addTrackToHistory(track)
                }
            }
        )

        prefsHistoryAdapter = SearchAdapter(
            onTrackClick = { track ->
                if (clickDebounce()) {
                    findNavController().navigate(
                        R.id.action_searchFragment_to_audioPlayerFragment,
                        AudioPlayerFragment.Companion.createArgs(track)
                    )
                }
            }
        )


        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // логика работы RecycleView
        binding.trackRecycleView.adapter = iTunesAdapter
        binding.trackHistoryRecycleView.adapter = prefsHistoryAdapter


        // загружаем историю из префов если экран первый раз запущен - иначе восстанавливаем поиск из сети
        viewModel.getCurrentState()?.let { state ->
            render(state)
        } ?: run {
            // Если состояния нет - загружаем историю
            viewModel.loadHistory()
        }


        // Cлушатель TextWatcher (изменения текста) в едиттексте
        binding.searchEditText.addTextChangedListener(textWatcher)


        viewModel.observerState().observe(viewLifecycleOwner) {
            render(it)
        }

        // Повторный запрос в iTunes
        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(binding.searchEditText.text.toString(), true)
        }


        binding.tracksHistoryClearButton.setOnClickListener {
            viewModel.clearHistory()
        }


        // Очищаем содержимое EditText и прячем клаввиатуру при нажатии на крест
        binding.closeImageViewButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
            viewModel.loadHistory()

        }

    }


    // отмена случайного двойного нажатия
    private fun clickDebounce(): Boolean {
        return if (isClickAllowed.getAndSet(false)) {
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed.set(true)
            }
            true
        } else {
            false
        }
    }



    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(p0: Editable?) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.closeImageViewButton.visibility =
                if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            searchText = s?.toString()?.trim() ?: ""

            viewModel.searchDebounce(newText = searchText)


            if (searchText.isEmpty()) {
                viewModel.loadHistory()
            }
        }
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
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

        iTunesAdapter.tracks.clear()
        iTunesAdapter.tracks.addAll(tracks)
        iTunesAdapter.notifyDataSetChanged()
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
            prefsHistoryAdapter.tracks.clear()
            prefsHistoryAdapter.tracks.addAll(historyTracks)
            prefsHistoryAdapter.notifyDataSetChanged()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}