package com.bignerdranch.playlistmaker.search.data.client

import com.bignerdranch.playlistmaker.search.data.models.Response

interface NetworkClient {

    fun doRequest(dto: Any): Response

}