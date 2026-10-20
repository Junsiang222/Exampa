package com.example.exampa

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface MovieApi {
    @GET("movies")
    suspend fun getAllMovie(): List<Movie>   // 拿全部帖子

    @GET("movies/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Movie   // 按 id 拿一条
}
object RetrofitClient {
    // 超时设 60 秒,服务器慢的时候不会马上失败
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: MovieApi = Retrofit.Builder()  // 改 1:PostApi
        .baseUrl("https://fcapi-1y70.onrender.com/")   // 最后要有 /   // 改 2:网址
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)  // 改 1:PostApi
}