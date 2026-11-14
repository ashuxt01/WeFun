package com.example.ui.screens.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.SocialPost
import com.example.data.repository.WefunSeedData
import com.example.ui.discover.DiscoverSocialScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun DiscoverScreen(
    posts: List<SocialPost> = emptyList(),
    families: List<FamilyInfo> = WefunSeedData.families,
    events: List<EventItem> = WefunSeedData.events,
    followedUserIds: Set<String> = emptySet(),
    onOpenFamilies: () -> Unit = {},
    onOpenEventsRankings: () -> Unit = {},
    onToggleFollow: (String) -> Unit = {},
    onChatWithUser: (peerId: String, peerName: String) -> Unit = { _, _ -> },
    onCreatePost: (content: String, hashtag: String, mediaType: String, imageUri: String?) -> Unit = { _, _, _, _ -> },
    onToggleLikePost: (SocialPost) -> Unit = {},
    onCommentPost: (SocialPost) -> Unit = {},
    onReportPost: (SocialPost) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        DiscoverSocialScreen(
            posts = posts,
            families = families,
            events = events,
            followedUserIds = followedUserIds,
            onOpenFamilies = onOpenFamilies,
            onOpenEventsRankings = onOpenEventsRankings,
            onToggleFollow = onToggleFollow,
            onChatWithUser = onChatWithUser,
            onCreatePost = onCreatePost,
            onToggleLikePost = onToggleLikePost,
            onCommentPost = onCommentPost,
            onReportPost = onReportPost
        )
    }
}
