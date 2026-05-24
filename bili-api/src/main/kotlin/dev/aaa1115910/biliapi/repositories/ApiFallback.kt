package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType

internal suspend inline fun <T> preferApiOrFallbackToApp(
    preferApiType: ApiType,
    operation: String,
    crossinline web: suspend () -> T,
    crossinline app: suspend () -> T
): T {
    return when (preferApiType) {
        ApiType.App -> app()
        ApiType.Web -> runCatching { web() }.getOrElse { webError ->
            println("Web api failed for $operation, fallback to app: ${webError.message}")
            runCatching { app() }.getOrElse { appError ->
                appError.addSuppressed(webError)
                throw appError
            }
        }
    }
}
