package dev.aaa1115910.bv.player.entity

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Skip mode for sponsor segments
 */
enum class SponsorBlockSkipMode {
    /** Automatically skip segment with countdown */
    AUTO_SKIP,
    /** Show skip button, user must press to skip */
    MANUAL_SKIP,
    /** Don't show any skip UI */
    DISABLED
}

/**
 * Represents a sponsor segment from BilibiliSponsorBlock API
 * API documentation: https://github.com/hanydd/BilibiliSponsorBlock/wiki/API
 */
data class SponsorBlockSegment(
    val segment: List<Float>,
    val cid: String,
    val UUID: String,
    val category: String,
    val actionType: String,
    val locked: Int,
    val votes: Int,
    val videoDuration: Float
) {
    val startTime: Float get() = segment.getOrElse(0) { 0f }
    val endTime: Float get() = segment.getOrElse(1) { 0f }

    /** Start time in milliseconds */
    val startTimeMs: Long get() = (startTime * 1000).toLong()
    /** End time in milliseconds */
    val endTimeMs: Long get() = (endTime * 1000).toLong()

    val categoryEnum: SponsorBlockCategory
        get() = SponsorBlockCategory.fromString(category)
}

/**
 * SponsorBlock segment categories with associated colors and skip modes
 * Colors are based on the official SponsorBlock color scheme
 */
enum class SponsorBlockCategory(
    val value: String,
    val color: Color,
    val displayName: String,
    val skipMode: SponsorBlockSkipMode
) {
    SPONSOR("sponsor", Color(0xFF00D400), "赞助广告", SponsorBlockSkipMode.AUTO_SKIP),
    SELFPROMO("selfpromo", Color(0xFFFFFF00), "自我推广", SponsorBlockSkipMode.MANUAL_SKIP),
    INTERACTION("interaction", Color(0xFFCC00FF), "互动提醒", SponsorBlockSkipMode.MANUAL_SKIP),
    INTRO("intro", Color(0xFF00FFFF), "片头", SponsorBlockSkipMode.MANUAL_SKIP),
    OUTRO("outro", Color(0xFF0202ED), "片尾", SponsorBlockSkipMode.MANUAL_SKIP),
    PREVIEW("preview", Color(0xFF008FD6), "预告/回顾", SponsorBlockSkipMode.MANUAL_SKIP),
    FILLER("filler", Color(0xFF7300FF), "填充/过渡", SponsorBlockSkipMode.MANUAL_SKIP),
    MUSIC_OFFTOPIC("music_offtopic", Color(0xFFFF9900), "非音乐部分", SponsorBlockSkipMode.MANUAL_SKIP),
    POI_HIGHLIGHT("poi_highlight", Color(0xFFFF1684), "高光时刻", SponsorBlockSkipMode.DISABLED),
    EXCLUSIVE_ACCESS("exclusive_access", Color(0xFF008A5C), "独家访问", SponsorBlockSkipMode.DISABLED),
    CHAPTER("chapter", Color(0xFF7F7F7F), "章节", SponsorBlockSkipMode.DISABLED),
    UNKNOWN("unknown", Color(0xFF808080), "未知", SponsorBlockSkipMode.DISABLED);

    companion object {
        fun fromString(value: String): SponsorBlockCategory {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}

/**
 * CompositionLocal for sponsor block segments data
 */
data class VideoPlayerSponsorBlockData(
    val segments: List<SponsorBlockSegment> = emptyList()
)

val LocalVideoPlayerSponsorBlockData = compositionLocalOf { VideoPlayerSponsorBlockData() }
