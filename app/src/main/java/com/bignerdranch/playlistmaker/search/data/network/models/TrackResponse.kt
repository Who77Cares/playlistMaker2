package com.bignerdranch.playlistmaker.search.data.network.models

class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response() {
}

