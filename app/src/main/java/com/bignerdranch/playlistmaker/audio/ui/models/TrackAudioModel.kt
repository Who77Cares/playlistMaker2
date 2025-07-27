package com.bignerdranch.playlistmaker.audio.ui.models

data class TrackAudioModel(
    val trackName: String,
    val artistName: String,
    val duration: String,
    val year: String,
    val album: String,
    val coverUrl: String,
    val style: String,
    val country: String,
    val previewUrl: String
) {
}