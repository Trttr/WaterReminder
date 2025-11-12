package com.example.waterreminder.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Gender { Male, Female }

class MainViewModel : ViewModel() {

    var name by mutableStateOf("")
        private set

    var gender by mutableStateOf<Gender?>(null)
        private set

    val isNextEnabled: Boolean
        get() = name.isNotBlank() && gender != null

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onGenderSelected(g: Gender) {
        gender = g
    }
}
