package dev.aaa1115910.bv.tv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.tv.screens.VideoCommentRepliesScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class VideoCommentRepliesActivity : ComponentActivity() {
    companion object {
        fun actionStart(
            context: Context,
            aid: Long,
            rpid: Long,
            repliesCount: Int
        ) {
            context.startActivity(
                Intent(context, VideoCommentRepliesActivity::class.java).apply {
                    putExtra("aid", aid)
                    putExtra("rpid", rpid)
                    putExtra("replies_count", repliesCount)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme(forceDark = true) {
                VideoCommentRepliesScreen()
            }
        }
    }
}
