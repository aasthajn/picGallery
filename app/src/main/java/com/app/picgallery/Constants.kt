package com.app.picgallery

class Constants {
    companion object Gallery {
        const val BASE_URL = "https://acharyaprashant.org/api/v2/content/"
        const val MOCK_URL = "https://api.mocklets.com/p6796/"

        const val READ_TIMEOUT = 30
        const val WRITE_TIMEOUT = 30
        const val CONNECTION_TIMEOUT = 10
        const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
        const val IMAGE_LOAD_DELAY = 0L //change to 2000L for cancel
    }

}
