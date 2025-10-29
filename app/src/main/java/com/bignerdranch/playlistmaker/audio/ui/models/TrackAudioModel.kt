package com.bignerdranch.playlistmaker.audio.ui.models

// берем за основу для Entity
data class TrackAudioModel(
    val trackName: String,
    val artistName: String,
    val duration: String,
    val year: String,
    val album: String,
    val coverUrl: String,
    val style: String,
    val country: String,
    val previewUrl: String) {

    // Функция для получения качественной обложки
    fun getHighQualityCover(): String {
        return coverUrl.replaceAfterLast('/', "512x512bb.jpg")
    }

}