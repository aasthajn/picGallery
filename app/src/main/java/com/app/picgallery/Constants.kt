package com.app.picgallery

class Constants {
    companion object Gallery {
        const val BASE_URL = "https://acharyaprashant.org/api/v2/content/"

        const val READ_TIMEOUT = 30
        const val WRITE_TIMEOUT = 30
        const val CONNECTION_TIMEOUT = 10
        const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
    }

}
