package com.example.maracas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//room db
@Database(
    entities = [Shake::class],    //room contains
    version = 1,                  //db version control
    exportSchema = false          //do not export schema for now
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shakeDao(): ShakeDao //return ShakeDao
    //Singleton ：only one database instance in this app
    companion object {
        //
        @Volatile private var INSTANCE: AppDatabase? = null
        // get this instance
        fun get(context: Context): AppDatabase =     //Check if INSTANCE already has a value
            INSTANCE ?: synchronized(this) {  //If not, use synchronized(this) to lock, only one instance create in one thread
                Room.databaseBuilder(               //Room.databaseBuilder to actually construct the db
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maracas.db"
                ).build().also { INSTANCE = it }  // save
            }
    }
}