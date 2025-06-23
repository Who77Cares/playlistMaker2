package com.bignerdranch.playlistmaker.data.models

class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response() {
}