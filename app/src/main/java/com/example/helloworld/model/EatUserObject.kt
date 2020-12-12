package com.example.helloworld.model

data class EatUserObject(
    val uid: String?=null,
    val eatId: String?=null,
    var repeat: Int?=0,
    var scanned: Boolean?=null
)
