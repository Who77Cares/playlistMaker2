package com.bignerdranch.playlistmaker.media.new_playlist.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val enableColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.color_3772E7) }
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



        binding.arrowBack.setOnClickListener { showDialog() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { showDialog() }


        viewModel.observeButtonState().observe(viewLifecycleOwner) { state ->
            binding.createNewPlaylistButton.isEnabled = state.isEnabled
            binding.createNewPlaylistButton.setBackgroundColor(state.backgroundColor)
        }




        binding.textInputEditTextName.doAfterTextChanged { editable ->
            viewModel.updateButtonState(editable.toString(), enableColor, disableColor)
            viewModel.updateName(editable.toString())
        }

        binding.textInputEditTextDescription.doAfterTextChanged { editable ->
            viewModel.updateDescription(editable.toString())
        }


        // логика выбора и установки картинки в ImageView
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.imageSelectedImageView.setImageURI(uri)
                    binding.imageSelectedImageView.visibility = View.VISIBLE
                    binding.imageSelectImageView.visibility = View.GONE

                    // сохраняем картинку во внутренее хранилище
                    viewModel.saveImage(uri)

                    viewModel.updateUri(uri)

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.imageSelectImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        binding.createNewPlaylistButton.setOnClickListener {
            createNewPlaylist()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // почему-то глючный риппл-эффект у кнопок Dialog
    fun showDialog() {
        if (viewModel.anyFieldFilled()) {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")

                .setNeutralButton("Отмена") { dialog, which ->
                    Log.d("DIALOG", "Нажата отмена")
                }

                .setPositiveButton("Завершить") { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()

        } else {
            findNavController().navigateUp()
        }
    }

    fun createNewPlaylist() {

        viewModel.savePlaylist(
            PlaylistModel(
                // room должна за нас сгенеритьщ айдишник
                id = 0L,
                coverUri = viewModel.observeCurrentImgUri().value ?: Uri.EMPTY,
                name = viewModel.observeCurrentName().value ?: "",
                description = viewModel.observeCurrentDescription().value ?: "",
                tracksSize = 0
            )
        )

        Toast.makeText(
            requireContext(),
            "Плейлист ${viewModel.observeCurrentName().value!!} создан",
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigateUp()
    }

}