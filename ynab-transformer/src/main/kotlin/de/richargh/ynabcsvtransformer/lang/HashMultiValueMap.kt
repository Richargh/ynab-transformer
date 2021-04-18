package de.richargh.ynabcsvtransformer.lang

class HashMultiValueMap<K, V> : MutableMultiValueMap<K, V> {

    private val map = HashMap<K, MutableList<V>>()

    override fun iterator(): Iterator<Map.Entry<K, List<V>>> = map.iterator()

    override val keys: Set<K> get() = map.keys
    override val flatValues: List<V> get() = map.values.flatten()

    override operator fun get(key: K): List<V> = map[key] ?: emptyList()

    override fun isEmpty() = map.isEmpty()

    override val size get() = map.size

    override fun containsKey(key: K) = map.containsKey(key)

    override fun put(key: K, value: V) {
        map.compute(key) { _, elements ->
            (elements ?: mutableListOf()).apply { add(value) }
        }
    }

    override fun deleteMapping(key: K, value: V) {
        map.compute(key) { _, elements ->
            when (elements) {
                null -> null
                else -> {
                    elements.remove(value)
                    if (elements.isEmpty()) null
                    else elements
                }
            }
        }
    }

    companion object {
        fun <K, V> of(vararg pairs: Pair<K, V>): HashMultiValueMap<K, V> {
            val map = HashMultiValueMap<K, V>()
            pairs.forEach { map.put(it.first, it.second) }
            return map
        }
    }
}