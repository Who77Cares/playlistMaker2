package com.bignerdranch.playlistmaker.new_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment: Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val enableColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.color_3772E7_3772E7) }
    private val disableColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.colors_AEAFB4_AEAFB4) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // инициализируемс начальное состояние кнопки
        binding.createNewPlaylistButton.isEnabled = false
        binding.createNewPlaylistButton.setBackgroundColor(disableColor)

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // обновляем состояние кнопки
        viewModel.observeButtonState().observe(viewLifecycleOwner) { state ->
            binding.createNewPlaylistButton.isEnabled = state.isEnabled
            binding.createNewPlaylistButton.setBackgroundColor(state.backgroundColor)
        }

        binding.TextInputEditTextName.doAfterTextChanged { editable ->
            viewModel.updateButtonState(editable.toString(), enableColor, disableColor)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}