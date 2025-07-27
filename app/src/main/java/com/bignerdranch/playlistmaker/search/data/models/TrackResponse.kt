package com.bignerdranch.playlistmaker.search.data.models

class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response() {
}