package com.bignerdranch.playlistmaker.search.data.client

interface StorageClient<T> {

    fun storageData(data:T)
    fun getData(): T?
    fun clearData()

}

