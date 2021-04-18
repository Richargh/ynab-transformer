package de.richargh.ynabcsvtransformer.lang

fun <K, V> multiValueMapOf(vararg pairs: Pair<K, V>): MultiValueMap<K, V> =
        HashMultiValueMap.of(*pairs)

fun <K, V> mutableMultiValueMapOf(vararg pairs: Pair<K, V>): MutableMultiValueMap<K, V> =
        HashMultiValueMap.of(*pairs)