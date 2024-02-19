package com.example.axisandalliescompanion

class AppRepository(private val appDataSource: AppDataSource) {
    private suspend fun createEconomyEntry(nation: Nation, ipcs: Int) {
        appDataSource.createEconomyEntry(nation.ordinal, ipcs)
    }

    private suspend fun deleteEconomyEntry(nation: Nation) {
        appDataSource.deleteEconomyEntry(nation.ordinal)
    }

    suspend fun resetEconomies() {
        val ipcCounts = mapOf(
            Pair(Nation.GERMANY, 30),
            Pair(Nation.SOVIET_UNION, 37),
            Pair(Nation.JAPAN, 26),
            Pair(Nation.UNITED_STATES, 52),
            Pair(Nation.CHINA, 12),
            Pair(Nation.UNITED_KINGDOM_EUROPE, 28),
            Pair(Nation.UNITED_KINGDOM_PACIFIC, 17),
            Pair(Nation.ITALY, 10),
            Pair(Nation.ANZAC, 10),
            Pair(Nation.FRANCE, 19)
        )

        Nation.values().forEach {
            suspend {
                deleteEconomyEntry(it)
                createEconomyEntry(it, ipcCounts[it] ?: 0)
            }()
        }
    }

    suspend fun addToEconomy(amount: Int, nation: Nation) {
        appDataSource.addToEconomy(amount, nation.ordinal)
    }

    suspend fun removeFromEconomy(amount: Int, nation: Nation) {
        appDataSource.removeFromEconomy(amount, nation.ordinal)
    }

    suspend fun setEconomy(ipcs: Int, nation: Nation) {
        appDataSource.setEconomy(ipcs, nation.ordinal)
    }

    suspend fun initializeEconomies() {
        if (appDataSource.getEconomyEntryByNationId(Nation.GERMANY.ordinal).isEmpty()) {
            resetEconomies()
        }
    }

    suspend fun getEconomyEntryByNation(nation: Nation): Economy {
        return appDataSource.getEconomyEntryByNationId(nation.ordinal)[0]
    }
}