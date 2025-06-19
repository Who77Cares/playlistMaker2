package com.bignerdranch.playlistmaker.data.network

import com.bignerdranch.playlistmaker.data.NetworkClient
import com.bignerdranch.playlistmaker.data.models.Response
import com.bignerdranch.playlistmaker.data.models.TrackRequest
import com.bignerdranch.playlistmaker.data.models.TrackResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(iTunesApi::class.java)



    override fun doRequest(dto: Any): Response {
        if (dto is TrackRequest) {
            val call = itunesService.findTrack(dto.expression)
            val resp = call.execute() // какие-то приколы с многопоточкой
            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

//    private fun isConnected(): Boolean {
//        val connectivityManager = context.getSystemService(
//            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//        if (capabilities != null) {
//            when {
//                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
//                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
//                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
//            }
//        }
//        return false
//    }
}