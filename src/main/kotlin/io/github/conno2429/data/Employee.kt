package io.github.conno2429.data

enum class EmployeeType {
    HOURLY, MANAGER
}

data class Employee(
    val id: String,
    var type: EmployeeType,
    val first: String,
    val last: String,
    var regHours: Double,
    var otHours: Double,
    var dubHours: Double,
    var tip: Double,
    var pto: Double,
    var sicKHours: Double
)
