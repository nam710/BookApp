package com.example.bookapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1)
abstract class BookDatabase : RoomDatabase(){

    companion object {
        private var instance: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "books-db"
                ).build()
            }
            return instance as BookDatabase
        }
    }

    abstract fun bookDao() : BookDao

}