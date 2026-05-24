package dev.aaa1115910.bv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dev.aaa1115910.bv.player.impl.exo.ExoMediaPlayer

@OptIn(UnstableApi::class)
@Composable
fun BvVideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,
    playerListener: VideoPlayerListener,
) {
    val currentPlayerListener = rememberUpdatedState(playerListener)
    val playerEventListener = remember(videoPlayer) {
        object : VideoPlayerListener {
            override fun onError(error: Exception) {
                currentPlayerListener.value.onError(error)
            }

            override fun onReady() {
                currentPlayerListener.value.onReady()
            }

            override fun onPlay() {
                currentPlayerListener.value.onPlay()
            }

            override fun onPause() {
                currentPlayerListener.value.onPause()
            }

            override fun onBuffering() {
                currentPlayerListener.value.onBuffering()
            }

            override fun onEnd() {
                currentPlayerListener.value.onEnd()
            }

            override fun onIdle() {
                currentPlayerListener.value.onIdle()
            }

            override fun onSeekBack(seekBackIncrementMs: Long) {
                currentPlayerListener.value.onSeekBack(seekBackIncrementMs)
            }

            override fun onSeekForward(seekForwardIncrementMs: Long) {
                currentPlayerListener.value.onSeekForward(seekForwardIncrementMs)
            }
        }
    }

    DisposableEffect(videoPlayer) {
        videoPlayer.setPlayerEventListener(playerEventListener)

        onDispose {
            videoPlayer.setPlayerEventListener(null)
            videoPlayer.release()
        }
    }

    when (videoPlayer) {
        is ExoMediaPlayer -> {
            var videoPlayerView: PlayerView? by remember { mutableStateOf(null) }
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { ctx ->
                    videoPlayerView = PlayerView(ctx).apply {
                        player = videoPlayer.mPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        useController = false
                    }
                    videoPlayerView!!
                }
            )
        }
    }
}
