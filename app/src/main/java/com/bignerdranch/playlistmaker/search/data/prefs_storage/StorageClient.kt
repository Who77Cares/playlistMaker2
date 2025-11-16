package com.bignerdranch.playlistmaker.search.data.prefs_storage

interface StorageClient<T> {

    fun storageData(data:T)
    fun getData(): T?
    fun clearData()

}

