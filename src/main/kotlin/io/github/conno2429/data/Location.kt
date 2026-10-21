package io.github.conno2429.data

data class Location(
    val id: String,
    val loc: String,
    var minWage: Double,
    var employeeList: MutableList<Employee>
)

fun Location.setMinWage(minWage: Double) {
    this.minWage = minWage
}

fun Location.updateMinWage() {
    //TODO: search function that takes in this.loc

    // this.minWage =
}
