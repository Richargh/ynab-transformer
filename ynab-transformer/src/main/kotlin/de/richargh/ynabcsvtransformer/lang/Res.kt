package de.richargh.ynabcsvtransformer.lang

sealed class Res<T: Any> {
    class Ok<T: Any>(val value: T): Res<T>(){
        override fun toString() = "Ok($value)"
    }

    class Fail<T: Any>(val messages: List<String>): Res<T>(){
        override fun toString() = "Fail($messages)"
    }
}

fun <T:Any> ok(value: T) = Res.Ok(value)
fun <T:Any> fail(vararg messages: String) = Res.Fail<T>(messages.asList())