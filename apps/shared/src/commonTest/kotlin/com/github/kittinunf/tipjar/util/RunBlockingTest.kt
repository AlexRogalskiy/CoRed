package com.github.kittinunf.tipjar.util

import kotlinx.coroutines.CoroutineScope

expect fun <T> runBlockingTest(block: suspend CoroutineScope.() -> T): T
