package dev.aaa1115910.bv.tv.component.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.bv.R
import java.util.Locale

@Composable
fun VideoCommentSortBar(
    modifier: Modifier = Modifier,
    title: String,
    selectedSort: CommentSort,
    onSortChange: (CommentSort) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VideoCommentSortChip(
                text = stringResource(R.string.video_comment_sort_hot),
                selected = selectedSort == CommentSort.Hot,
                onClick = { onSortChange(CommentSort.Hot) }
            )
            VideoCommentSortChip(
                text = stringResource(R.string.video_comment_sort_time),
                selected = selectedSort == CommentSort.Time,
                onClick = { onSortChange(CommentSort.Time) }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VideoCommentSortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.inverseSurface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
            },
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = Color.White,
            focusedContentColor = Color.White,
            pressedContentColor = Color.White
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCommentCard(
    modifier: Modifier = Modifier,
    comment: Comment,
    showReplyPreview: Boolean = true,
    onOpenReplies: (() -> Unit)? = null
) {
    val displayReplyCount = maxOf(comment.repliesCount, comment.replies.size)
    val previewReplies = comment.replies.take(2)
    val hasReplies = displayReplyCount > 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f),
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.92f),
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.92f),
            contentColor = Color.White,
            focusedContentColor = Color.White,
            pressedContentColor = Color.White
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = { onOpenReplies?.invoke() }
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    model = comment.member.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = comment.member.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = comment.content.joinToString(separator = ""),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        overflow = TextOverflow.Clip
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VideoCommentMetaText(text = comment.timeDesc)
                        VideoCommentMetaText(
                            text = stringResource(
                                R.string.video_comment_like_count,
                                formatCommentCount(comment.likeCount)
                            )
                        )
                        if (hasReplies) {
                            VideoCommentMetaText(
                                text = stringResource(
                                    R.string.video_comment_reply_count,
                                    formatCommentCount(displayReplyCount)
                                )
                            )
                        }
                    }
                }
            }

            if (showReplyPreview && hasReplies) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Black.copy(alpha = 0.16f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    previewReplies.forEach { reply ->
                        Text(
                            text = buildAnnotatedString {
                                pushStyle(
                                    SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                append(reply.member.name)
                                pop()
                                append("：")
                                append(reply.content.joinToString(separator = ""))
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.video_comment_view_all_replies,
                                formatCommentCount(displayReplyCount)
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoCommentMetaText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.72f)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoCommentLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@Composable
fun VideoCommentEmpty(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.video_comment_empty),
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

private fun formatCommentCount(count: Int): String {
    return when {
        count >= 100_000_000 -> compactNumber(count / 100_000_000.0, "亿")
        count >= 10_000 -> compactNumber(count / 10_000.0, "万")
        else -> count.toString()
    }
}

private fun compactNumber(value: Double, suffix: String): String {
    val formatted = String.format(Locale.US, "%.1f", value)
    val trimmed = formatted.removeSuffix(".0")
    return "$trimmed$suffix"
}
