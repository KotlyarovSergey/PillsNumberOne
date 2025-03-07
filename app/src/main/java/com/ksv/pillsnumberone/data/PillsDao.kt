package com.ksv.pillsnumberone.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ksv.pillsnumberone.entity.PillToDB
import kotlinx.coroutines.flow.Flow

@Dao
interface PillsDao {
    @Insert
    suspend fun insert(pill: PillToDB)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPills(pills: List<PillToDB>)

    @Update
    suspend fun update(pill: PillToDB)

    @Update
    suspend fun updateBoth(pill1: PillToDB, pill2: PillToDB)

    @Update
    suspend fun updateList(pills: List<PillToDB>)

    @Delete
    suspend fun delete(pill: PillToDB)

    @Delete
    suspend fun deletePills(pills: List<PillToDB>)


    @Query("SELECT * FROM pills")
    fun getAll(): Flow<List<PillToDB>>

    @Query("SELECT * FROM pills WHERE pill_id=:id")
    fun getPill(id: Long): PillToDB
}