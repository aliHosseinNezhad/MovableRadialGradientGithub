package com.gamapp.movableradialgradient.filemanager

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.gamapp.movableradialgradient.entity.AlbumEntity
import com.gamapp.movableradialgradient.entity.ArtistEntity
import com.gamapp.movableradialgradient.entity.AudioEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MusicAccessManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    private fun Cursor.captureMusics(list: MutableList<AudioEntity>) {
        val idColumn = getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val durationColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val sizeColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        while (moveToNext()) {
            try {
                val id = getLong(idColumn)
                val name = getString(nameColumn)
                val duration = getInt(durationColumn)
                val size = getInt(sizeColumn)
                val title = getString(titleColumn)
                val artist = getString(artistColumn)
                list += AudioEntity(
                    id = id,
                    title = title,
                    artist = artist,
                    displayName = name,
                    duration = duration,
                    size = size
                )
            } catch (e: Exception) {

            }
        }
    }

    private fun Cursor.captureAlbums(list: MutableList<AlbumEntity>) {
        val idIndex = getColumnIndex(MediaStore.Audio.Albums._ID)
        val albumIdIndex = getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)
        val numberOfSongsIndex = getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        val artistIndex = getColumnIndex(MediaStore.Audio.Albums.ARTIST)
        while (moveToNext()) {
            val id = getLong(idIndex)
            val albumId = getLong(albumIdIndex)
            val artist = getString(artistIndex)
            val count = getInt(numberOfSongsIndex)
            list += AlbumEntity(
                id = id,
                artist = artist,
                albumId = albumId,
                count = count
            )
        }
    }

    private fun Cursor.captureArtists(list: MutableList<ArtistEntity>) {
        val idIndex = getColumnIndex(MediaStore.Audio.Artists._ID)
        val artistIndex = getColumnIndex(MediaStore.Audio.Artists.ARTIST)
        val countIndex = getColumnIndex(MediaStore.Audio.Artists._COUNT)
        val tracksIndex = getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        while (moveToNext()) {
            val id = getLong(idIndex)
            val artist = getString(artistIndex)
            val tracks = getInt(tracksIndex)
            list += ArtistEntity(id = id, artist = artist, count = tracks)
        }
    }


    // total list
    fun getMusicList(): List<AudioEntity> {
        val list = mutableListOf<AudioEntity>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
        )
        val sortOrder = "${MediaStore.Audio.Media.ALBUM_ID} DESC"
        val query = contentResolver.query(
            collection,
            null,
            selection,
            selectionArgs,
            sortOrder
        )
        query?.captureMusics(list)
        return list
    }

    fun getAlbumList(): List<AlbumEntity> {
        val list = mutableListOf<AlbumEntity>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Albums.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            }

        val sortOrder = "${MediaStore.Audio.Albums.NUMBER_OF_SONGS} DESC"
        val cursor = contentResolver.query(
            collection,
            null,
            null,
            null,
            sortOrder
        )
        cursor?.captureAlbums(list)
        return list
    }

    fun getArtistList(): MutableList<ArtistEntity> {
        val list = mutableListOf<ArtistEntity>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Artists.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
            }

        val sortOrder = "${MediaStore.Audio.Artists.NUMBER_OF_TRACKS} DESC"
        contentResolver.query(
            collection,
            null,
            null,
            null,
            sortOrder
        )?.captureArtists(list)
        return list
    }
    //

    fun getMusicsByAlbumId(albumId: Long): List<AudioEntity> {
        val list = mutableListOf<AudioEntity>()
        val selection = "${MediaStore.Audio.Albums.ALBUM_ID} == ?"
        val selectionArgs = arrayOf(
            albumId.toString()
        )
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )?.captureMusics(list)
        return list
    }

    fun getMusicsByArtistId(artistId: Long): List<AudioEntity> {
        val list = mutableListOf<AudioEntity>()
        val selection = "${MediaStore.Audio.Media.ARTIST_ID} == ?"
        val selectionArgs = arrayOf(
            artistId.toString()
        )
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )?.captureMusics(list)
        return list
    }
}