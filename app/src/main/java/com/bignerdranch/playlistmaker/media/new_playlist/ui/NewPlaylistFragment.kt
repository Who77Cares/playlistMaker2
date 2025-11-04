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
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri
import com.bumptech.glide.Glide

class NewPlaylistFragment: Fragment() {

    companion object {
        const val PLAYLIST_ID = "playlist_id"
        const val PLAYLIST_NAME = "playlist_name"
        const val PLAYLIST_DESCRIPTION = "playlist_description"
        const val PLAYLIST_COVER_URI = "playlist_cover_uri"
        const val PLAYLIST_TRACK_SIZE = "playlist_track_ssize"

        fun createArgs(playlist: PlaylistModel): Bundle {
            return Bundle().apply {
                putInt(PLAYLIST_ID, playlist.id.toInt())
                putString(PLAYLIST_NAME, playlist.name)
                putString(PLAYLIST_DESCRIPTION, playlist.description)
                putString(PLAYLIST_COVER_URI, playlist.coverUri.toString())
                putInt(PLAYLIST_TRACK_SIZE, playlist.tracksSize)
            }
        }
    }

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

        setupParamsToEditPlaylistScreen()

        // инициализируемс начальное состояние кнопки
        binding.createNewPlaylistButton.isEnabled = false
        binding.createNewPlaylistButton.setBackgroundColor(disableColor)



        binding.arrowBack.setOnClickListener { showDialog() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { showDialog() }


        viewModel.observeButtonState().observe(viewLifecycleOwner) { state ->
            binding.createNewPlaylistButton.isEnabled = state.isEnabled
            binding.createNewPlaylistButton.setBackgroundColor(state.backgroundColor)

            binding.updatePlaylistButton.isEnabled = state.isEnabled
            binding.updatePlaylistButton.setBackgroundColor(state.backgroundColor)
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
                    // ВСЕГДА используем Glide для загрузки картинки
                    Glide.with(this)
                        .load(uri)
                        .fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .into(binding.imageSelectedImageView)

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

        binding.updatePlaylistButton.setOnClickListener {
            updatePlaylistData()
        }

        binding.imageSelectedImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // почему-то глючный риппл-эффект у кнопок Dialog
    fun showDialog() {

        // если id != null, значит мы пришли с экрана SinglePlaylistFragment
        val id = arguments?.getInt(PLAYLIST_ID)

        if (viewModel.anyFieldFilled() && id == null) {
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


    fun setupParamsToEditPlaylistScreen() {

        val id = arguments?.getInt(PLAYLIST_ID)

        if (id != null) {
            binding.createNewPlaylistButton.visibility = View.GONE
            binding.updatePlaylistButton.visibility = View.VISIBLE
            binding.title.text = requireContext().getString(R.string.change_playlist_title)

            viewModel.updateName(arguments?.getString(PLAYLIST_NAME) ?: "")
            viewModel.updateDescription(arguments?.getString(PLAYLIST_DESCRIPTION) ?: "")
            arguments?.getString(PLAYLIST_COVER_URI)?.toUri()?.let { viewModel.updateUri(it) }

            // ОБНОВЛЯЕМ UI с текущими значениями из ViewModel
            updateUIWithCurrentValues()
        }

    }
    private fun updateUIWithCurrentValues() {
        // Устанавливаем имя в поле ввода
        viewModel.observeCurrentName().value?.let { name ->
            binding.textInputEditTextName.setText(name)
        }

        // Устанавливаем описание в поле ввода
        viewModel.observeCurrentDescription().value?.let { description ->
            binding.textInputEditTextDescription.setText(description)
        }

        // Устанавливаем обложку
        viewModel.observeCurrentImgUri().value?.let { uri ->

                Glide.with(this)
                    .load(uri)
                    .fitCenter()
                    .placeholder(R.drawable.placeholder)
                    .into(binding.imageSelectedImageView)

                binding.imageSelectedImageView.visibility = View.VISIBLE
                binding.imageSelectImageView.visibility = View.GONE

        }

        // Обновляем состояние кнопки
        val currentName = viewModel.observeCurrentName().value ?: ""
        viewModel.updateButtonState(currentName, enableColor, disableColor)
    }


    private fun updatePlaylistData() {
        viewModel.updatePlaylist(
            PlaylistModel(
                id = arguments?.getInt(PLAYLIST_ID)?.toLong() ?: 0L,
                coverUri = viewModel.observeCurrentImgUri().value ?: Uri.EMPTY,
                name = viewModel.observeCurrentName().value ?: "",
                description = viewModel.observeCurrentDescription().value ?: "",
                tracksSize = arguments?.getInt(PLAYLIST_TRACK_SIZE) ?: 0
            )
        )

        findNavController().navigateUp()
    }

}