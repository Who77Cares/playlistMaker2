package com.bignerdranch.playlistmaker.search

import android.content.SharedPreferences
import com.bignerdranch.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val KEY_FOR_SEARCH_LIST: String = "key_for_search_list"

class SearchPreferences {

    fun read(sharedPreferences: SharedPreferences): ArrayList<Track> {
        val json = sharedPreferences.getString(KEY_FOR_SEARCH_LIST, null)
        return if (json.isNullOrEmpty()) {
            ArrayList()  // Возвращаем пустой список, если данных нет
        } else {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            Gson().fromJson(json, type) ?: ArrayList()  // Если десериализация возвращает null, то возвращаем пустой список
        }
    }

    fun write(sharedPreferences: SharedPreferences, searchTracks: ArrayList<Track>) {
        val json = Gson().toJson(searchTracks)
        sharedPreferences.edit()
            .putString(KEY_FOR_SEARCH_LIST, json)
            .apply()
    }

}