package com.bignerdranch.playlistmaker.data

interface StorageClient<T> {

    fun storageData(data:T)
    fun getData(): T?
    fun clearData()

}