package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.SponsorBlockCategory
import dev.aaa1115910.bv.player.entity.SponsorBlockSegment
import dev.aaa1115910.bv.player.entity.SponsorBlockSkipMode
import dev.aaa1115910.bv.util.formatHourMinSec

// TODO 跳转历史记录
@Composable
fun BackToHistoryTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    time: String
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "上次看到 $time 点击确认键跳转"
    )
}

// TODO 跳过片头
@Composable
fun SkipOpTip(
    modifier: Modifier = Modifier,
    show: Boolean
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片头"
    )
}

// TODO 跳过片尾
@Composable
fun SkipEdTip(
    modifier: Modifier = Modifier,
    show: Boolean
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片尾"
    )
}

/**
 * Sponsor Block segment skip tip
 * @param category The segment category
 * @param countdown Countdown seconds for auto-skip mode (null for manual mode)
 */
@Composable
fun SponsorBlockSkipTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    category: SponsorBlockCategory,
    countdown: Int? = null
) {
    val text = when (category.skipMode) {
        SponsorBlockSkipMode.AUTO_SKIP -> {
            if (countdown != null && countdown > 0) {
                "跳过${category.displayName} [${countdown}秒后自动跳过] 按确认键取消"
            } else {
                "跳过${category.displayName}"
            }
        }
        SponsorBlockSkipMode.MANUAL_SKIP -> {
            "跳过${category.displayName} 按确认键跳过"
        }
        SponsorBlockSkipMode.DISABLED -> ""
    }

    if (category.skipMode != SponsorBlockSkipMode.DISABLED) {
        SkipTip(
            modifier = modifier,
            show = show,
            text = text,
            backgroundColor = category.color.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun SkipTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    text: String,
    backgroundColor: Color = Color.Black.copy(alpha = 0.6f)
) {
    AnimatedVisibility(
        visible = show,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Surface(
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp),
                colors = SurfaceDefaults.colors(
                    containerColor = backgroundColor
                ),
                shape = MaterialTheme.shapes.medium.copy(
                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SkipTips(
    modifier: Modifier = Modifier,
    showSkipOp: Boolean = false,
    showSkipEd: Boolean = false,
    currentSponsorSegment: SponsorBlockSegment? = null,
    sponsorSkipCountdown: Int = 0,
) {
    val videoPlayerHistoryData = LocalVideoPlayerHistoryData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current

    Box(modifier = modifier.fillMaxSize()) {
        BackToHistoryTip(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 32.dp),
            show = videoPlayerStateData.showBackToHistory,
            time = videoPlayerHistoryData.lastPlayed.toLong().formatHourMinSec()
        )
        
        // SponsorBlock skip tip
        if (currentSponsorSegment != null) {
            SponsorBlockSkipTip(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp),
                show = true,
                category = currentSponsorSegment.categoryEnum,
                countdown = if (sponsorSkipCountdown > 0) sponsorSkipCountdown else null
            )
        }
    }
}