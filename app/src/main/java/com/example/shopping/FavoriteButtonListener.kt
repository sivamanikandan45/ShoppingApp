package com.example.shopping


interface FavoriteButtonListener {

    /*fun addToFavoriteButton(position: Int)

    fun removeFromFavoriteButton(position: Int)*/

    fun isFavorite(position: Int):Boolean

    fun handle(position: Int):Boolean
}