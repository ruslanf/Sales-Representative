package studio.bz_soft.freightforwarder.root.extensions

enum class Sex {
    MALE,
    FEMALE;

    companion object {
        private val values = values()
        fun getByValue(value: Int) = values.firstOrNull { it.ordinal == value }
    }
}