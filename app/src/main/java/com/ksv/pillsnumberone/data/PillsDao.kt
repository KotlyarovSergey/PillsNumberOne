package com.ksv.pillsnumberone.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ksv.pillsnumberone.entity.PillToDB
import kotlinx.coroutines.flow.Flow

@Dao
interface PillsDao {
    @Insert
    suspend fun insert(pill: PillToDB)

    @Update
    suspend fun update(pill: PillToDB)

    @Delete
    suspend fun delete(pill: PillToDB)

    @Query("SELECT * FROM pills")
    fun getAll(): Flow<List<PillToDB>>

    @Query("SELECT * FROM pills WHERE pill_id=:id")
    fun getPill(id: Long): PillToDB
}