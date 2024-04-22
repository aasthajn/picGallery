## How to run
- Build project
  ```gradle
  ./gradlew clean assembleDebug
  ```
- Need to clear data from Settings to reset cache.
- Click on the image to enlarge it.

## Debug Configs
- For debugging, you can add delay in image download duration by updating `IMAGE_LOAD_DELAY` in `Contants.kt` file. Currently its set as 0, set it to 2000L to clearly handle cancel state.
  ```kotlin
  const val IMAGE_LOAD_DELAY = 2000L
  ```

- For simulating error, you can change the `BASE_URL` in the `Contants.kt` file. Have used mock API to simulate error state, where the first image url is wrong.
  ```kotlin
  const val BASE_URL = "https://api.mocklets.com/p6796/"
  ```

- For debugging image loader lifecycle, logs are added with tag `picGallery`.
