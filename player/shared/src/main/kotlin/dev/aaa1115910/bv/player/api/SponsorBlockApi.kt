package dev.aaa1115910.bv.player.api

import dev.aaa1115910.bv.player.entity.SponsorBlockSegment
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * API client for BilibiliSponsorBlock
 * API documentation: https://github.com/hanydd/BilibiliSponsorBlock/wiki/API
 */
object SponsorBlockApi {
    private val logger = KotlinLogging.logger("SponsorBlockApi")
    private const val END_POINT = "bsbsb.top"

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    private val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            defaultRequest {
                url {
                    host = END_POINT
                    protocol = URLProtocol.HTTPS
                }
                header("origin", "bv-android")
                header("x-ext-version", "1.0.0")
            }
        }
    }

    /**
     * Get skip segments for a video
     *
     * @param videoId BV ID of the video (e.g., "BV1xx411c7mD")
     * @param cid Optional CID of the video part
     * @return List of sponsor segments, or empty list if not found
     */
    suspend fun getSkipSegments(
        videoId: String,
        cid: String? = null
    ): List<SponsorBlockSegment> {
        return runCatching {
            logger.debug { "[SponsorBlock] Fetching sponsor segments for video: $videoId, cid: $cid" }
            val response = client.get("/api/skipSegments") {
                parameter("videoID", videoId)
                cid?.let { parameter("cid", it) }
            }

            if (response.status.isSuccess()) {
                val responseText = response.bodyAsText()
                logger.debug { "[SponsorBlock] Response: $responseText" }
                val segments = json.decodeFromString<List<SponsorBlockSegmentResponse>>(responseText)
                logger.info { "[SponsorBlock] Found ${segments.size} sponsor segments for $videoId" }
                segments.map { it.toSponsorBlockSegment() }
            } else {
                // 404 means no segments found, which is normal
                if (response.status.value == 404) {
                    logger.debug { "[SponsorBlock] No sponsor segments found for $videoId" }
                } else {
                    logger.warn { "[SponsorBlock] Failed to fetch segments: ${response.status}" }
                }
                emptyList()
            }
        }.onFailure {
            logger.warn { "[SponsorBlock] Error fetching sponsor segments: ${it.message}" }
        }.getOrDefault(emptyList())
    }
}

/**
 * Internal response class for API deserialization
 */
@Serializable
private data class SponsorBlockSegmentResponse(
    val segment: List<Float>,
    val cid: String = "",
    val UUID: String,
    val category: String,
    val actionType: String,
    val locked: Int = 0,
    val votes: Int = 0,
    val videoDuration: Float = 0f,
    val description: String = ""
) {
    fun toSponsorBlockSegment() = SponsorBlockSegment(
        segment = segment,
        cid = cid,
        UUID = UUID,
        category = category,
        actionType = actionType,
        locked = locked,
        votes = votes,
        videoDuration = videoDuration
    )
}
