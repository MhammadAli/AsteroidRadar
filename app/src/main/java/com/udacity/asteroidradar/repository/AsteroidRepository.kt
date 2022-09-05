package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {


    val asteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getAllAsteroids()

    val todayAsteroidList: LiveData<List<Asteroid>>
        get() = database.asteroidDao().getTodayAsteroid(Constants.getCurrentDate())

    val pictureOfDay: LiveData<PictureOfDay>
        get() = database.pictureDao().get()


    suspend fun refreshAsteroidList() {
        withContext(Dispatchers.IO) {
            try {
                val asteroid = Api.RETROFIT_SERVICE.getAllAsteroids()
                val json = JSONObject(asteroid)
                val data = parseAsteroidsJsonResult(json)
                database.asteroidDao().updateData(data)
                Log.e("data", "")

            } catch (e: Exception) {
                Log.e("data", "")
            }
        }
    }


    suspend fun getPictureOfTheDate() {
        withContext(Dispatchers.IO) {
            try {
                val response = Api.RETROFIT_SERVICE.getPictureOfDay(
                    API_KEY
                )
                val pic = Moshi.Builder()
                    .build()
                    .adapter(PictureOfDay::class.java)
                    .fromJson(response)
                    ?:
                    // Return an empty picture
                    PictureOfDay(-1, "image", "", "")
                database.pictureDao().updateData(pic)
                Log.e("data", "")

            } catch (e: Exception) {
                Log.e("data", "")

            }

        }
    }



}