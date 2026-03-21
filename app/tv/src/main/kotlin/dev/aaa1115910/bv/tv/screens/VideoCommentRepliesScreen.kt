package dev.aaa1115910.bv.tv.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.component.reply.VideoCommentCard
import dev.aaa1115910.bv.tv.component.reply.VideoCommentEmpty
import dev.aaa1115910.bv.tv.component.reply.VideoCommentLoading
import dev.aaa1115910.bv.tv.component.reply.VideoCommentSortBar
import dev.aaa1115910.bv.util.OnBottomReached
import dev.aaa1115910.bv.viewmodel.CommentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun VideoCommentRepliesScreen(
    modifier: Modifier = Modifier,
    commentViewModel: CommentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val aid = intent.getLongExtra("aid", 0L)
    val rpid = intent.getLongExtra("rpid", 0L)
    val repliesCount = intent.getIntExtra("replies_count", 0)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(aid, rpid) {
        if (aid == 0L || rpid == 0L) return@LaunchedEffect
        commentViewModel.commentId = aid
        commentViewModel.commentType = 1
        commentViewModel.rpid = rpid
        commentViewModel.rpCount = repliesCount
        withContext(Dispatchers.IO) {
            commentViewModel.refreshReplies()
        }
    }

    if (commentViewModel.replies.isNotEmpty()) {
        listState.OnBottomReached(loading = commentViewModel.updatingReplies) {
            scope.launch(Dispatchers.IO) {
                commentViewModel.loadMoreReplies()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                VideoCommentSortBar(
                    modifier = Modifier.padding(horizontal = 50.dp),
                    title = if (repliesCount > 0) {
                        stringResource(
                            R.string.video_comment_replies_subtitle,
                            repliesCount.toString()
                        )
                    } else {
                        stringResource(R.string.video_comment_replies_title)
                    },
                    selectedSort = commentViewModel.replySort,
                    onSortChange = { sort ->
                        if (sort == commentViewModel.replySort) return@VideoCommentSortBar
                        scope.launch(Dispatchers.IO) {
                            commentViewModel.switchReplySort(sort)
                        }
                    }
                )
            }

            commentViewModel.replyRootComment?.let { rootComment ->
                item {
                    VideoCommentCard(
                        modifier = Modifier.padding(horizontal = 50.dp),
                        comment = rootComment,
                        showReplyPreview = false
                    )
                }
            }

            items(
                items = commentViewModel.replies,
                key = { it.rpid }
            ) { reply ->
                VideoCommentCard(
                    modifier = Modifier.padding(horizontal = 50.dp),
                    comment = reply,
                    showReplyPreview = false
                )
            }

            if (commentViewModel.replies.isEmpty() && !(commentViewModel.refreshingReplies || commentViewModel.updatingReplies)) {
                item {
                    VideoCommentEmpty(modifier = Modifier.padding(horizontal = 50.dp))
                }
            }

            if (commentViewModel.refreshingReplies || commentViewModel.updatingReplies) {
                item {
                    VideoCommentLoading(modifier = Modifier.padding(horizontal = 50.dp))
                }
            }
        }
    }
}
