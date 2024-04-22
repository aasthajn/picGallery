package com.app.picgallery

interface Constants {
    companion object {
        const val BASE_URL = "https://acharyaprashant.org/api/v2/content/"
//        const val BASE_URL = "https://api.mocklets.com/p6796/"
        const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
        const val IMAGE_LOAD_DELAY = 0L //change to 2000L for cancel
    }

}
