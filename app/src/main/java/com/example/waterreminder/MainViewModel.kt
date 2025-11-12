package com.example.waterreminder.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Gender { Male, Female }

class MainViewModel : ViewModel() {

    var name by mutableStateOf("")
        private set

    var gender by mutableStateOf<Gender?>(null)
        private set

    var drinkingGoals by mutableIntStateOf(0)
        private set


    val isNextEnabled: Boolean
        get() = name.isNotBlank() && gender != null && drinkingGoals > 0

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onGenderSelected(g: Gender) {
        gender = g
        drinkingGoals = updateDefaultDrinkingGoal()
    }

    fun onDrinkingGoalsChange(goals: Int){
        drinkingGoals = goals
    }

    fun updateDefaultDrinkingGoal():Int{
        return when{
            gender == Gender.Male && name.isNotBlank() -> 3700
            gender == Gender.Female && name.isNotBlank() -> 2700
            else -> 0
        }
    }

}
