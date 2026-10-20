package com.example.exampa

import java.sql.Date

data class Movie(
    val id: Int,
    val title: String,
    val director: String,
    val overview: String,
    val length: Int,
    val rating: Double,
    val release_date: String,
)
