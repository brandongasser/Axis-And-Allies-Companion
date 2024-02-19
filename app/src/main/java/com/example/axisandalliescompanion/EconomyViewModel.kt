package com.example.axisandalliescompanion

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class EconomyUIState(
    val economies: Map<Nation, Int> = mapOf()
)

class EconomyViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var _economyUIState: MutableStateFlow<EconomyUIState> = runBlocking {
        MutableStateFlow(
            EconomyUIState(
                economies = Nation.values().map {
                    appRepository.getEconomyEntryByNation(it)
                }.fold(mutableMapOf()) { map, economy ->
                    map[economy.nation] = economy.ipcs
                    map
                }
            )
        )
    }

    val economyUIState: StateFlow<EconomyUIState> = _economyUIState

    suspend fun setEconomy(nation: Nation, ipcs: Int) {
        appRepository.setEconomy(ipcs, nation)
        val newEconomies = mutableMapOf<Nation, Int>()
        Nation.values().forEach {
            if (it == nation) {
                newEconomies[it] = ipcs
            } else {
                newEconomies[it] = economyUIState.value.economies[it]!!
            }
        }
        updateEconomyUIState(economyUIState.value.copy(economies=newEconomies))
    }

    suspend fun addToEconomy(nation: Nation, amount: Int) {
        if (amount < 0) {
            return
        }
        appRepository.addToEconomy(amount, nation)
        val newEconomies = mutableMapOf<Nation, Int>()
        Nation.values().forEach {
            if (it == nation) {
                newEconomies[it] = economyUIState.value.economies[it]!! + amount
            } else {
                newEconomies[it] = economyUIState.value.economies[it]!!
            }
        }
        updateEconomyUIState(economyUIState.value.copy(economies=newEconomies))
    }

    suspend fun removeFromEconomy(nation: Nation, amount: Int): Boolean {
        if (amount < 0 || economyUIState.value.economies[nation]!! < amount) {
            return false
        }
        appRepository.removeFromEconomy(amount, nation)
        val newEconomies = mutableMapOf<Nation, Int>()
        Nation.values().forEach {
            if (it == nation) {
                newEconomies[it] = economyUIState.value.economies[it]!! - amount
            } else {
                newEconomies[it] = economyUIState.value.economies[it]!!
            }
        }
        updateEconomyUIState(economyUIState.value.copy(economies=newEconomies))
        return true
    }

    suspend fun resetEconomies() {
        appRepository.resetEconomies()
        updateEconomyUIState(economyUIState.value.copy(economies=mapOf(
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
        )))
    }

    private fun updateEconomyUIState(newState: EconomyUIState) {
        _economyUIState.update { newState }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AxisAndAlliesCompanion).appRepository
                EconomyViewModel(
                    appRepository = appRepository,
                )
            }
        }
    }
}