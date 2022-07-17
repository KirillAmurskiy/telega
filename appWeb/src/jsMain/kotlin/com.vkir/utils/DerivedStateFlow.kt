package com.vkir.utils

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

/**
 * Does not produce the same value in a raw, so respect "distinct until changed emissions"
 * */
class DerivedStateFlow<T>(
    private val getValue: () -> T,
    private val flow: Flow<T>
) : StateFlow<T> {

    override val replayCache: List<T>
        get () = listOf(value)

    override val value: T
        get () = getValue()


    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        coroutineScope { flow.distinctUntilChanged().stateIn(this).collect(collector) }
    }
}

fun <T1, R> StateFlow<T1>.mapState(transform: (a: T1) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(this.value) },
        flow = this.map { a -> transform(a) }
    )
}

fun <T1, T2, R> combineStates(flow: StateFlow<T1>, flow2: StateFlow<T2>, transform: (a: T1, b: T2) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(flow.value, flow2.value) },
        flow = combine(flow, flow2) { a, b -> transform(a, b) }
    )
}

fun <T1, T2, T3, R> combineStates(flow: StateFlow<T1>, flow2: StateFlow<T2>, flow3: StateFlow<T3>, transform: (a: T1, b: T2, c: T3) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(flow.value, flow2.value, flow3.value) },
        flow = combine(flow, flow2, flow3) { a, b, c -> transform(a, b, c) }
    )
}

fun <T1, T2, T3, T4, R> combineStates(flow: StateFlow<T1>, flow2: StateFlow<T2>, flow3: StateFlow<T3>, flow4: StateFlow<T4>, transform: (a: T1, b: T2, c: T3, d: T4) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(flow.value, flow2.value, flow3.value, flow4.value) },
        flow = combine(flow, flow2, flow3, flow4) { a, b, c, d -> transform(a, b, c, d) }
    )
}

inline fun <reified T, R> combineStates(flows: List<StateFlow<T>>, crossinline transform: (a: List<T>) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(flows.map { it.value }) },
        flow = combine(flows) { transform(it.toList()) }
    )
}