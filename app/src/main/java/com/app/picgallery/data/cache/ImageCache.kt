package com.app.picgallery.data.cache

import android.graphics.Bitmap
import android.util.LruCache

object ImageCache {
    private const val cacheSize = 2* 1024 * 1024 // 4MiB
    private val memoryCache: LruCache<String, Bitmap> = LruCache(cacheSize)

    fun getFromMemoryCache(key: String): Bitmap? = memoryCache.get(key)

    fun setToMemoryCache(key: String, bitmap: Bitmap) {
        memoryCache.put(key, bitmap)
    }
}
