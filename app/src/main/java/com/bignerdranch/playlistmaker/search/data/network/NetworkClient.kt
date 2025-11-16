package com.bignerdranch.playlistmaker.search.data.network

import com.bignerdranch.playlistmaker.search.data.network.models.Response

interface NetworkClient {
   suspend fun doRequest(dto: Any): Response
}

