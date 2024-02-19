package com.example.axisandalliescompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class PurchaseUIState(
    val purchaseCounts: Map<PurchaseType, Int> = mapOf(),
    val showSummary: Boolean = false
)

class PurchaseViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var _purchaseUIState: MutableStateFlow<PurchaseUIState> = runBlocking {
        MutableStateFlow(
            PurchaseUIState(
                purchaseCounts = PurchaseType.values().fold(mutableMapOf()) { map, purchaseType ->
                    map[purchaseType] = 0
                    map
                }
            )
        )
    }

    val purchaseUIState: StateFlow<PurchaseUIState> = _purchaseUIState

    fun addUnit(purchaseType: PurchaseType) {
        val currentCounts: Map<PurchaseType, Int> = _purchaseUIState.value.purchaseCounts
        val newCounts: MutableMap<PurchaseType, Int> = mutableMapOf()
        PurchaseType.values().forEach {
            newCounts[it] = if (it == purchaseType) currentCounts[it]!! + 1 else currentCounts[it]!!
        }
        updatePurchaseUIState(PurchaseUIState(purchaseCounts = newCounts))
    }

    fun removeUnit(purchaseType: PurchaseType): Boolean {
        val currentCounts: Map<PurchaseType, Int> = _purchaseUIState.value.purchaseCounts
        if (currentCounts[purchaseType] == 0) return false
        val newCounts: MutableMap<PurchaseType, Int> = mutableMapOf()
        PurchaseType.values().forEach {
            newCounts[it] = if (it == purchaseType) currentCounts[it]!! - 1 else currentCounts[it]!!
        }
        updatePurchaseUIState(PurchaseUIState(purchaseCounts = newCounts))
        return true
    }

    fun openSummary() {
        updatePurchaseUIState(PurchaseUIState(purchaseCounts = _purchaseUIState.value.purchaseCounts, showSummary = true))
    }

    fun closeSummary() {
        updatePurchaseUIState(PurchaseUIState(purchaseCounts = _purchaseUIState.value.purchaseCounts, showSummary = false))
    }

    private fun updatePurchaseUIState(newState: PurchaseUIState) {
        _purchaseUIState.update { newState }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AxisAndAlliesCompanion).appRepository
                PurchaseViewModel(
                    appRepository = appRepository,
                )
            }
        }
    }
}