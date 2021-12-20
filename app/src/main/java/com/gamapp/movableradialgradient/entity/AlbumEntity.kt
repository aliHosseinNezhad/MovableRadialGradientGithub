package com.gamapp.movableradialgradient.entity

data class AlbumEntity(
    val id: Long,
    val artist: String,
    val albumId: Long,
    val count: Int,
    val imageId:Long? = null,
    val albumName: String
)