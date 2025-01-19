package com.ksv.pillsnumberone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ksv.pillsnumberone.entity.PillToDB

@Database(entities = [PillToDB::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract val getPillsDao: PillsDao

    companion object{
        private const val DB_NAME = "app_database"

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase{
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        DB_NAME
                    ).build()
                }
                return INSTANCE!!
            }
        }
    }
}