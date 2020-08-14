package com.jocoos.flipflop.sample.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

interface DefaultCoroutineScope : CoroutineScope
interface IOCoroutineScope : CoroutineScope
interface MainCoroutineScope : CoroutineScope

fun DefaultCoroutineScope(
    job: Job = SupervisorJob()
): DefaultCoroutineScope = object : DefaultCoroutineScope {
    override val coroutineContext = job + Dispatchers.Default
}

fun DefaultCoroutineScope(
    context: CoroutineContext
): DefaultCoroutineScope = object : DefaultCoroutineScope {
    override val coroutineContext = context + Dispatchers.Default
}

fun IOCoroutineScope(
    job: Job = SupervisorJob()
): IOCoroutineScope = object : IOCoroutineScope {
    override val coroutineContext = job + Dispatchers.IO
}

fun IOCoroutineScope(
    context: CoroutineContext
): IOCoroutineScope = object : IOCoroutineScope {
    override val coroutineContext = context + Dispatchers.IO
}

fun MainCoroutineScope(
    job: Job = SupervisorJob()
): MainCoroutineScope = object : MainCoroutineScope {
    override val coroutineContext = job + Dispatchers.Main
}

fun MainCoroutineScope(
    context: CoroutineContext
): MainCoroutineScope = object : MainCoroutineScope {
    override val coroutineContext = context + Dispatchers.Main
}