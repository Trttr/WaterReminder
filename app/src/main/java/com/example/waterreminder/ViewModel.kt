package com.example.waterreminder.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Gender { Male, Female }

data class UiState(
    val name: String = "",
    val gender: Gender? = null,
    val drinkingGoals: Int = 0,
    val drinkingCount: Int = 0,
) {
    val isNextEnabled: Boolean
        get() = name.isNotBlank() && gender != null && drinkingGoals > 0
}



class ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onGenderSelected(g: Gender) {
        val current = _uiState.value
        val defaultGoal = updateDefaultDrinkingGoal(
            name = current.name,
            gender = g
        )
        _uiState.value = current.copy(
            gender = g,
            drinkingGoals = defaultGoal
        )
    }

    fun onDrinkingGoalsChange(goals: Int) {
        _uiState.value = _uiState.value.copy(drinkingGoals = goals)
    }

    private fun updateDefaultDrinkingGoal(
        name: String,
        gender: Gender?
    ): Int {
        return when {
            gender == Gender.Male && name.isNotBlank() -> 3700
            gender == Gender.Female && name.isNotBlank() -> 2700
            else -> 0
        }
    }

    fun checkDrinkingStatus(): String {
        val state = _uiState.value
        val gender = state.gender
        val goals = state.drinkingGoals

        return when {
            gender == Gender.Male && goals < 3700 -> "\uD83E\uDD64 You need to drink more water!"
            gender == Gender.Female && goals < 2700 -> "\uD83E\uDD64 You need to drink more water!"
            gender != null && goals > 5000 -> "\uD83D\uDE35\u200D\uD83D\uDCAB You are over the limit!"
            else -> "\uD83D\uDCAA You are doing great!"
        }
    }

    fun getPercentage(): Float{
        return _uiState.value.drinkingCount.toFloat() / _uiState.value.drinkingGoals.toFloat().coerceIn(0f,1f)
    }
}


