package com.ksv.pillsnumberone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ksv.pillsnumberone.entity.PillToDB

@Database(entities = [PillToDB::class], version = 1, exportSchema = false)
abstract class PillsDataBase: RoomDatabase() {
    abstract val getPillsDao: PillsDao

    companion object{
        @Volatile
        private var INSTANCE: PillsDataBase? = null

        fun getInstance(context: Context): PillsDataBase{
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PillsDataBase::class.java,
                        "task_database"
                    ).build()
                }
                return INSTANCE!!
            }
        }
    }
}