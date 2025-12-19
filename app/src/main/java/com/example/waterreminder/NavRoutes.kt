package com.example.waterreminder

data object NavRoutes {
    const val Login = "LoginPage"              // ✅ 新增
    const val Welcome = "WelcomePage"
    const val Dashboard = "Dashboard"
    const val AddRecord = "AddDrinkingRecord"  // ✅ 建议统一命名（可选）
    const val History = "DrinkingHistory"
    const val AddRecordSuccessful = "AddRecordSuccessful"
}