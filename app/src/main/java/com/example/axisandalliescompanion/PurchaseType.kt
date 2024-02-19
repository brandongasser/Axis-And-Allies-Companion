package com.example.axisandalliescompanion

enum class PurchaseType(val cost: Int, private val printable: String) {
    INFANTRY(3, "Infantry"),
    ARTILLERY(4, "Artillery"),
    MECHANIZED_INFANTRY(4, "Mechanized Infantry"),
    TANK(6, "Tank"),
    AAA(6, "AAA"),
    FIGHTER(10, "Fighter"),
    TACTICAL_BOMBER(11, "Tactical Bomber"),
    STRATEGIC_BOMBER(12, "Strategic Bomber"),
    SUBMARINE(6, "Submarine"),
    TRANSPORT(7, "Transport"),
    DESTROYER(8, "Destroyer"),
    CRUISER(12, "Cruiser"),
    CARRIER(16, "Carrier"),
    BATTLESHIP(20, "Battleship"),
    MINOR_INDUSTRIAL_COMPLEX(12, "Minor Industrial Complex"),
    MAJOR_INDUSTRIAL_COMPLEX(30, "Major Industrial Complex"),
    INDUSTRIAL_COMPLEX_UPGRADE(20, "Industrial Complex Upgrade"),
    AIR_BASE(15, "Air Base"),
    NAVAL_BASE(15, "Naval Base"),
    FACILITY_REPAIR(1, "Facility Repair"),
    RESEARCH(5, "Research");

    override fun toString(): String {
        return printable
    }
}