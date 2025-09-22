package com.bignerdranch.playlistmaker.search.ui.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.databinding.FragmentSearchBinding
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.search.ui.models.TrackState
import com.bignerdranch.playlistmaker.search.ui.presentation.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment: Fragment(), SearchAdapter.OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding  get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()


    private val adapter = SearchAdapter(false, this)
    private val adapterForHistoryTracks = SearchAdapter(true, this)
    private var isClickAllowed = true
    private var searchText = ""

    private var isHistoryLoaded = false


    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // логика работы RecycleView
        adapter.tracks = tracks
        binding.trackRecycleView.adapter = adapter

        adapterForHistoryTracks.tracks = historyTracks
        binding.trackHistoryRecycleView.adapter = adapterForHistoryTracks


        // костыли чтобы не показывалась история поиска после возврата из AdioPlayerFragment
        if (!isHistoryLoaded) {
            viewModel.loadHistory()
            isHistoryLoaded = true
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


    override fun onItemClick(
        track: Track,
        trackFromHistory: Boolean
    ) {
        if (clickDebounce()) {

            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track)
            )
        }

        if (trackFromHistory) return
        viewModel.addTrackToHistory(track)
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

    // отмена случайного двойного нажатия
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false

            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}