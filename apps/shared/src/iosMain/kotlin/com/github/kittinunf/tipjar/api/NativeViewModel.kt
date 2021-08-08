package com.github.kittinunf.tipjar.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual open class NativeViewModel {

    private val job = SupervisorJob()
    actual val scope = CoroutineScope(job + Dispatchers.Main)

    actual fun cancel() {
        job.cancel()
    }
}
