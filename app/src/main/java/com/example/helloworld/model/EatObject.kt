package com.example.helloworld.model

data class EatObject(
    val description: String="",
    val details: String="",
    val idAdd: String="",
    val date :String="",
    val image: String="",
    val eatId: String= description+date
)