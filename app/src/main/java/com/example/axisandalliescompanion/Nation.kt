package com.example.axisandalliescompanion

enum class Nation(private val printable: String) {
    GERMANY("Germany"),
    SOVIET_UNION("Soviet Union"),
    JAPAN("Japan"),
    UNITED_STATES("United States"),
    CHINA("China"),
    UNITED_KINGDOM_EUROPE("United Kingdom (Europe)"),
    UNITED_KINGDOM_PACIFIC("United Kingdom (Pacific)"),
    ITALY("Italy"),
    ANZAC("ANZAC"),
    FRANCE("France");

    override fun toString(): String {
        return this.printable
    }
}