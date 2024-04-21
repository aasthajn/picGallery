package com.app.picgallery.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object DiskCache {
    fun getBitmapFromDisk(context: Context, url: String): Bitmap? {
        val filename = url.hashCode().toString()
        val file = File(context.cacheDir, filename)
        return if (file.exists()) BitmapFactory.decodeFile(file.path) else null
    }

    fun saveBitmapToDisk(context: Context, url: String, bitmap: Bitmap) {
        val filename = url.hashCode().toString()
        val file = File(context.cacheDir, filename)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
