package com.gamapp.movableradialgradient.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val uniqueId: Int = 0,
    val id: Long,
    val title:String,
    val author:String,
    val fileName:String,
    val duration:Int,
    val favorite:Boolean,
)
