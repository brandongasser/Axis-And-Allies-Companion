package com.example.axisandalliescompanion

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(primaryKeys = ["nation_id"])
data class EconomyEntry(
    @ColumnInfo(name = "nation_id") val nationId: Int,
    val ipcs: Int
)

@Dao
interface EconomyDao {
    @Query("Select * from EconomyEntry where nation_id = :nationId")
    fun getEconomyEntryByNationId(nationId: Int): List<EconomyEntry>?

    @Insert
    fun createEconomyEntry(economyEntry: EconomyEntry)

    @Query("Delete from EconomyEntry where nation_id = :nationId")
    fun deleteEconomyEntry(nationId: Int)

    @Update
    fun updateEconomyEntry(economyEntry: EconomyEntry)
}

@Database(entities = [EconomyEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun economyDao() : EconomyDao
}