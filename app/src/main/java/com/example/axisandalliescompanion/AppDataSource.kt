package com.example.axisandalliescompanion

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Economy(val nation: Nation, val ipcs: Int)

class AppDataSource(applicationContext: Context) {
    private val dao = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "AppDatabase"
    ).build().economyDao()

    suspend fun getEconomyEntryByNationId(nationId: Int): List<Economy> {
        return withContext(Dispatchers.IO) {
            dao.getEconomyEntryByNationId(nationId)
                ?.map { Economy(Nation.values()[it.nationId], it.ipcs) } ?: listOf()
        }
    }

    suspend fun createEconomyEntry(nationId: Int, ipcs: Int) {
        withContext(Dispatchers.IO) {
            dao.createEconomyEntry(EconomyEntry(nationId, ipcs))
        }
    }

    suspend fun deleteEconomyEntry(nationId: Int) {
        withContext(Dispatchers.IO) {
            dao.deleteEconomyEntry(nationId)
        }
    }

    suspend fun addToEconomy(amount: Int, nationId: Int) {
        withContext(Dispatchers.IO) {
            val economy = getEconomyEntryByNationId(nationId)[0]
            dao.updateEconomyEntry(EconomyEntry(nationId, economy.ipcs + amount))
        }
    }

    suspend fun removeFromEconomy(amount: Int, nationId: Int) {
        withContext(Dispatchers.IO) {
            val economy = getEconomyEntryByNationId(nationId)[0]
            dao.updateEconomyEntry(EconomyEntry(nationId, economy.ipcs - amount))
        }
    }

    suspend fun setEconomy(ipcs: Int, nationId: Int) {
        withContext(Dispatchers.IO) {
            dao.updateEconomyEntry(EconomyEntry(nationId, ipcs))
        }
    }
}