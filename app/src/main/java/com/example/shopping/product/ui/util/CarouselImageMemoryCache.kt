package com.example.shopping.product.ui.util

import android.graphics.Bitmap
import android.util.LruCache

object CarouselImageMemoryCache {
    fun getBitmapFromMemCache(imageKey: String): Bitmap? {
        return memoryCache.get(imageKey)
    }

    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    val cacheSize = maxMemory / 9

    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }
}