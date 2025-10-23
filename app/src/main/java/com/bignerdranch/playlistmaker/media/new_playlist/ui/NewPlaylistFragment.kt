package com.bignerdranch.playlistmaker.media.new_playlist.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
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


        viewModel.observeButtonState().observe(viewLifecycleOwner) { state ->
            binding.createNewPlaylistButton.isEnabled = state.isEnabled
            binding.createNewPlaylistButton.setBackgroundColor(state.backgroundColor)
        }




        binding.textInputEditTextName.doAfterTextChanged { editable ->
            viewModel.updateButtonState(editable.toString(), enableColor, disableColor)
        }


        // логика выбора и установки картинки в ImageView
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.imageSelectImageView.setImageURI(uri)

                    // сохраняем картинку во внутренее хранилище
                    viewModel.saveImage(uri)

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.imageSelectImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.savePlaylist(
                PlaylistModel(
                    coverUri = viewModel.observeCurrentImgUri().value ?: Uri.EMPTY,
                    name = binding.textInputLayoutName.editText?.text.toString() ,
                    description = binding.textInputLayoutDescription.editText?.text.toString(),
                )
            )

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}