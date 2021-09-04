package de.richargh.ynabcsvtransformer.read

sealed class DomainName {
    override fun toString(): String = javaClass.simpleName

    object BookingDate : DomainName()
    object Beneficiary : DomainName()
    object Description : DomainName()
    sealed class MoneyFlow : DomainName() {
        sealed class PlusMinusFlow: MoneyFlow() {
            object Flow: PlusMinusFlow()
        }
        sealed class InOutFlow: MoneyFlow() {
            object InFlow: InOutFlow()
            object OutFlow: InOutFlow()
        }
        sealed class MarkerFlow: MoneyFlow() {
            object Flow: MarkerFlow()
            class Marker(
                    val inFlowMarker: String,
                    val outFlowMarker: String): MarkerFlow(){

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    return javaClass == other?.javaClass
                }

                override fun hashCode() = javaClass.hashCode()

                companion object {
                    fun any() = Marker("", "")
                }
            }
        }
    }


    companion object {
        val objects get() = DomainName::class.sealedSubclasses.mapNotNull { it.objectInstance }.toSet()
        fun of(rawValue: String): DomainName {
            return DomainName::class.sealedSubclasses.singleOrNull { it.simpleName == rawValue }?.objectInstance
                    ?: throw RuntimeException("The key $rawValue does not exist in the domain.")
        }
    }
}