package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE completedAt >= :startOfDay ORDER BY completedAt DESC")
    fun getTodaySessions(startOfDay: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE completedAt >= :startOfDay")
    fun getTodayFocusMinutes(startOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions")
    fun getTotalSessionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long
}
