package io.supportgenie.androidlibrary.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.concurrent.Executors


val NETWORK = Executors.newFixedThreadPool(6).asCoroutineDispatcher()

val networkScope = CoroutineScope(NETWORK)