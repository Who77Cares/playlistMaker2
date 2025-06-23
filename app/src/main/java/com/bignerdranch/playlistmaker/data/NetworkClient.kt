package com.bignerdranch.playlistmaker.data

import com.bignerdranch.playlistmaker.data.models.Response

interface NetworkClient {

    fun doRequest(dto: Any): Response

}