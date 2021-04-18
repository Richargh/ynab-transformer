package de.richargh.ynabcsvtransformer.lang

interface MultiValueMap<K, V>: Iterable<Map.Entry<K, List<V>>> {
    val keys: Set<K>
    val flatValues: List<V>
    operator fun get(key: K): List<V>

    val size: Int
    fun isEmpty(): Boolean
    fun containsKey(key: K): Boolean
//    fun containsValue(value: V): Boolean
}

interface MutableMultiValueMap<K, V>: MultiValueMap<K, V>{
    fun put(key: K, value: V)
    fun deleteMapping(key: K, value: V)
}
