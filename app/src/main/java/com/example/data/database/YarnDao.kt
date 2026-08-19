package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.YarnItem
import kotlinx.coroutines.flow.Flow

@Dao
interface YarnDao {
    @Query("SELECT * FROM yarn_stash ORDER BY brand ASC")
    fun getAllYarn(): Flow<List<YarnItem>>

    @Query("SELECT * FROM yarn_stash WHERE id = :id")
    suspend fun getYarnById(id: Long): YarnItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYarn(yarnItem: YarnItem): Long

    @Update
    suspend fun updateYarn(yarnItem: YarnItem)

    @Delete
    suspend fun deleteYarn(yarnItem: YarnItem)
}
