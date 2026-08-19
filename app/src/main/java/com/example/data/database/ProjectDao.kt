package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?

    @Query("SELECT * FROM projects WHERE status = 'Active' ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestActiveProject(): Flow<Project?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Query("UPDATE projects SET currentRow = :newRow, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateProjectRow(id: Long, newRow: Int, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Query("DELETE FROM projects WHERE title IN ('Cozy Ribbed Blanket', 'Classic Granny Square Cardigan', 'Amigurumi Forest Bunny')")
    suspend fun deleteExampleProjects()
}
