package com.example.axisandalliescompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CollectIncomeUIState(
    val baseIncome: Int = 0,
    val convoyDisruptions: Int = 0,
    val warBonds: Int = 0,
    val nationalObjectives: Int = 0
)

class CollectIncomeViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var _collectIncomeUIState: MutableStateFlow<CollectIncomeUIState> = MutableStateFlow(
        CollectIncomeUIState(0, 0, 0, 0)
    )

    val collectIncomeUIState: StateFlow<CollectIncomeUIState> = _collectIncomeUIState

    fun setBaseIncome(newValue: Int) {
        val newState = CollectIncomeUIState(
            baseIncome = newValue,
            convoyDisruptions = collectIncomeUIState.value.convoyDisruptions,
            warBonds = collectIncomeUIState.value.warBonds,
            nationalObjectives = collectIncomeUIState.value.nationalObjectives
        )
        updateCollectIncomeUIState(newState)
    }

    fun setConvoyDisruptions(newValue: Int) {
        val newState = CollectIncomeUIState(
            baseIncome = collectIncomeUIState.value.baseIncome,
            convoyDisruptions = newValue,
            warBonds = collectIncomeUIState.value.warBonds,
            nationalObjectives = collectIncomeUIState.value.nationalObjectives
        )
        updateCollectIncomeUIState(newState)
    }

    fun setWarBonds(newValue: Int) {
        val newState = CollectIncomeUIState(
            baseIncome = collectIncomeUIState.value.baseIncome,
            convoyDisruptions = collectIncomeUIState.value.convoyDisruptions,
            warBonds = newValue,
            nationalObjectives = collectIncomeUIState.value.nationalObjectives
        )
        updateCollectIncomeUIState(newState)
    }

    fun setNationalObjectives(newValue: Int) {
        val newState = CollectIncomeUIState(
            baseIncome = collectIncomeUIState.value.baseIncome,
            convoyDisruptions = collectIncomeUIState.value.convoyDisruptions,
            warBonds = collectIncomeUIState.value.warBonds,
            nationalObjectives = newValue
        )
        updateCollectIncomeUIState(newState)
    }

    private fun updateCollectIncomeUIState(newState: CollectIncomeUIState) {
        _collectIncomeUIState.update { newState }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appRepository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AxisAndAlliesCompanion).appRepository
                CollectIncomeViewModel(
                    appRepository = appRepository,
                )
            }
        }
    }
}