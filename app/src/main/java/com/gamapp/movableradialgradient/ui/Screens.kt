package com.gamapp.movableradialgradient.ui

import java.util.*

sealed class Screens(val name: String = UUID.randomUUID().toString()) {
    object Player:Screens()
    object Permission:Screens()
}