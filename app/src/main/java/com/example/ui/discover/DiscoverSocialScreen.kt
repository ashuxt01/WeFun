package com.example.ui.discover

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.SocialPost
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.VipBadge
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TurquoiseActive

@Composable
fun DiscoverSocialScreen(
    posts: List<SocialPost>,
    families: List<FamilyInfo>,
    events: List<EventItem>,
    followedUserIds: Set<String>,
    onOpenFamilies: () -> Unit,
    onOpenEventsRankings: () -> Unit,
    onToggleFollow: (String) -> Unit,
    onChatWithUser: (peerId: String, peerName: String) -> Unit,
    onCreatePost: (content: String, hashtag: String, mediaType: String, imageUri: String?) -> Unit,
    onToggleLikePost: (SocialPost) -> Unit,
    onCommentPost: (SocialPost) -> Unit,
    onReportPost: (SocialPost) -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    val context = LocalContext.current
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedHashtag by remember { mutableStateOf("All") }
    var activeDiscoveryMode by remember { mutableStateOf("All") } // All, Nearby, Community
    var previewStoryAuthor by remember { mutableStateOf<Pair<String, String>?>(null) }

    val visiblePosts = remember(posts, selectedHashtag) {
        if (selectedHashtag == "All") posts
        else posts.filter { it.hashtag.equals(selectedHashtag, ignoreCase = true) }
    }

    val popularUsers = remember {
        listOf(
            Triple("host_rhea", "DJ Rhea • Singing", 5 to 0xFF8B5CF6),
            Triple("host_kai", "Kai Storm • PK Pro", 6 to 0xFFF43F5E),
            Triple("host_zara", "Zara Moon • Games", 4 to 0xFFEC4899),
            Triple("host_milo", "Milo Taktik • Nearby", 3 to 0xFF00C2B8),
            Triple("host_leo", "Leo Beats • DJ Stage", 6 to 0xFFF59E0B),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen 4 Header: Discover Title + Post Moment Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Surface(
                    onClick = { showCreatePostDialog = true },
                    shape = RoundedCornerShape(50),
                    color = TurquoiseActive,
                    modifier = Modifier.testTag("create_post_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Moment", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Featured Moments Banner Row (Reference Screenshot 4)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("discover_moments_banner"),
                onClick = { showCreatePostDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Colorful Moments Ring Emblem
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    listOf(TurquoiseActive, PurplePrimary, RoseNeon, GoldAccent, TurquoiseActive)
                                ),
                                style = Stroke(width = 7f)
                            )
                            drawCircle(color = TurquoiseActive, radius = 5f)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Moments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("New stories, photos & short clips", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(RoseNeon)
                        )
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Open Moments", tint = wefunColors.mutedText)
                    }
                }
            }
        }

        // 3 Discovery Category Cards: Family, Nearby, Community (Reference Screenshot 4 Grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DiscoveryCategoryCrestCard(
                    title = "Family",
                    subtitle = "${families.size} Houses",
                    icon = Icons.Rounded.Groups,
                    accentColor = GoldAmber,
                    bgTint = Color(0xFFFFF6E5),
                    selected = false,
                    onClick = onOpenFamilies,
                    modifier = Modifier.weight(1f).testTag("discover_family_card")
                )
                DiscoveryCategoryCrestCard(
                    title = "Nearby",
                    subtitle = "18 Active",
                    icon = Icons.Rounded.LocationOn,
                    accentColor = TurquoiseActive,
                    bgTint = Color(0xFFE6FAF7),
                    selected = activeDiscoveryMode == "Nearby",
                    onClick = {
                        activeDiscoveryMode = if (activeDiscoveryMode == "Nearby") "All" else "Nearby"
                    },
                    modifier = Modifier.weight(1f).testTag("discover_nearby_card")
                )
                DiscoveryCategoryCrestCard(
                    title = "Community",
                    subtitle = "Hub & News",
                    icon = Icons.Rounded.Forum,
                    accentColor = PurplePrimary,
                    bgTint = Color(0xFFF0ECFF),
                    selected = activeDiscoveryMode == "Community",
                    onClick = {
                        activeDiscoveryMode = if (activeDiscoveryMode == "Community") "All" else "Community"
                    },
                    modifier = Modifier.weight(1f).testTag("discover_community_card")
                )
            }
        }

        // User Stories Strip
        item {
            SectionHeader(
                title = "Live Stories",
                subtitle = "Tap any creator story or share your own"
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { showCreatePostDialog = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(TurquoiseActive.copy(alpha = 0.14f))
                                .border(2.dp, TurquoiseActive, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Add, "Add story", tint = TurquoiseActive, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Your Story", style = MaterialTheme.typography.labelSmall)
                    }
                }
                items(popularUsers, key = { "story_${it.first}" }) { (uid, label, meta) ->
                    val shortName = label.substringBefore(" •")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            previewStoryAuthor = shortName to "Live backstage clip from $shortName's WEFUN voice room!"
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, Brush.linearGradient(listOf(TurquoiseActive, PurplePrimary, GoldAccent)), CircleShape)
                                .padding(3.dp)
                        ) {
                            AvatarCircle(
                                initials = shortName.take(2),
                                accentColor = Color(meta.second),
                                size = 52.dp,
                                vipLevel = meta.first
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(shortName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        }

        // Popular Users & Recommended Friends
        item {
            SectionHeader(
                title = if (activeDiscoveryMode == "Nearby") "Nearby Friends & Creators" else "Recommended Friends",
                subtitle = "Follow or start a 1-on-1 chat"
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(popularUsers, key = { it.first }) { (uid, label, meta) ->
                    val name = label.substringBefore(" •")
                    val tag = label.substringAfter(" •", "Host")
                    val isFollowing = uid in followedUserIds
                    GlassCard(modifier = Modifier.width(142.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AvatarCircle(
                                initials = name.take(2),
                                accentColor = Color(meta.second),
                                size = 48.dp,
                                vipLevel = meta.first,
                                isSpeaking = true,
                                isVerified = true
                            )
                            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(tag, style = MaterialTheme.typography.labelSmall, color = TurquoiseActive)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    onClick = { onToggleFollow(uid) },
                                    shape = RoundedCornerShape(50),
                                    color = if (isFollowing) PurplePrimary.copy(alpha = 0.14f) else TurquoiseActive
                                ) {
                                    Text(
                                        text = if (isFollowing) "Followed" else "+ Follow",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isFollowing) PurplePrimary else Color.White,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    onClick = { onChatWithUser(uid, name) },
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "Hi",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trending Families & Official Announcements
        item {
            SectionHeader(
                title = "Trending Families & Activities",
                subtitle = "Join guilds and official WEFUN events",
                actionLabel = "See All",
                onAction = onOpenEventsRankings
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(events, key = { it.id }) { ev ->
                    GlassCard(
                        modifier = Modifier.width(236.dp),
                        borderColor = PurplePrimary.copy(alpha = 0.25f),
                        onClick = onOpenEventsRankings
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Campaign, null, tint = TurquoiseActive, modifier = Modifier.size(16.dp))
                                Text("OFFICIAL • ${ev.category.uppercase()}", style = MaterialTheme.typography.labelSmall, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                            }
                            Text(ev.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(ev.schedule, style = MaterialTheme.typography.bodySmall, color = GoldAmber, fontWeight = FontWeight.SemiBold)
                            Text("${ev.participantCount} joined", style = MaterialTheme.typography.labelSmall, color = wefunColors.mutedText)
                        }
                    }
                }
                items(families, key = { it.id }) { fam ->
                    GlassCard(
                        modifier = Modifier.width(196.dp),
                        onClick = onOpenFamilies
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("FAMILY RANK #${fam.rank}", style = MaterialTheme.typography.labelSmall, color = GoldAmber, fontWeight = FontWeight.Bold)
                            Text(fam.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text("Lv.${fam.level} • ${fam.memberCount} members", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        }
                    }
                }
            }
        }

        // Moments Feed Header + Hashtag Filter
        item {
            SectionHeader(
                title = "Trending Moments Feed",
                subtitle = "Photos, short videos & party highlights"
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    listOf(
                        "All" to "All Moments",
                        "#MidnightNeon" to "#MidnightNeon",
                        "#PKBattle" to "#PKBattle",
                        "#SpaceWerewolf" to "#SpaceWerewolf",
                    )
                ) { (tag, label) ->
                    FilterChip(
                        selected = selectedHashtag == tag,
                        onClick = { selectedHashtag = tag },
                        label = { Text(label) }
                    )
                }
            }
        }

        items(visiblePosts, key = { it.id }) { post ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(
                                initials = post.authorName.take(2),
                                accentColor = TurquoiseActive,
                                size = 40.dp,
                                vipLevel = post.authorVip
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(post.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    VipBadge(vipLevel = post.authorVip)
                                }
                                Text(post.hashtag, style = MaterialTheme.typography.labelSmall, color = TurquoiseActive)
                            }
                        }
                        IconButton(onClick = { onReportPost(post) }) {
                            Icon(Icons.Rounded.Report, "Report post", tint = wefunColors.mutedText, modifier = Modifier.size(18.dp))
                        }
                    }

                    Text(post.content, style = MaterialTheme.typography.bodyMedium)

                    if (post.imageUri != null) {
                        AsyncImage(
                            model = post.imageUri,
                            contentDescription = "Moment media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else if (post.mediaType == "SHORT_VIDEO") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(116.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(PurplePrimary.copy(0.85f), TurquoiseActive.copy(0.85f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.PlayCircle, null, tint = Color.White, modifier = Modifier.size(34.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Short Video Highlight • 0:24", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (post.mediaType == "IMAGE") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFFF43F5E).copy(0.18f), GoldAmber.copy(0.18f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Image, null, tint = RoseNeon, modifier = Modifier.size(26.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PK Finale Snapshot", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onToggleLikePost(post) }
                            ) {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = "Like post",
                                    tint = if (post.isLiked) RoseNeon else wefunColors.mutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.likesCount}", style = MaterialTheme.typography.labelMedium)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onCommentPost(post) }
                            ) {
                                Icon(Icons.Rounded.ChatBubbleOutline, "Comment", tint = wefunColors.mutedText, modifier = Modifier.size(19.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.commentsCount}", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        IconButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${post.authorName} on WEFUN: ${post.content} ${post.hashtag}")
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Moment"))
                            }
                        ) {
                            Icon(Icons.Rounded.Share, "Share moment", tint = TurquoiseActive, modifier = Modifier.size(19.dp))
                        }
                    }
                }
            }
        }
    }

    if (previewStoryAuthor != null) {
        val (author, storyText) = previewStoryAuthor!!
        AlertDialog(
            onDismissRequest = { previewStoryAuthor = null },
            title = { Text("$author's Story") },
            text = { Text(storyText) },
            confirmButton = {
                TextButton(onClick = { previewStoryAuthor = null }) { Text("Close") }
            }
        )
    }

    if (showCreatePostDialog) {
        CreateDiscoverPostDialog(
            onDismiss = { showCreatePostDialog = false },
            onPublish = { content, tag, type, uri ->
                showCreatePostDialog = false
                onCreatePost(content, tag, type, uri)
            }
        )
    }
}

@Composable
private fun DiscoveryCategoryCrestCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    bgTint: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier,
        borderColor = if (selected) accentColor else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(bgTint)
                    .border(1.5.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 12.dp, bottomEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(28.dp))
            }
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = LocalWefunColors.current.mutedText, maxLines = 1)
        }
    }
}

@Composable
private fun CreateDiscoverPostDialog(
    onDismiss: () -> Unit,
    onPublish: (content: String, hashtag: String, mediaType: String, imageUri: String?) -> Unit,
) {
    var content by remember { mutableStateOf("") }
    var hashtag by remember { mutableStateOf("#WEFUN") }
    var mediaType by remember { mutableStateOf("TEXT") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            mediaType = "IMAGE"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Moment or Short Video") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("What's happening in your room?") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hashtag,
                    onValueChange = { hashtag = it },
                    label = { Text("Hashtag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    ) {
                        Icon(Icons.Rounded.Image, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (imageUri != null) "Photo Attached" else "Pick Photo")
                    }
                    FilterChip(
                        selected = mediaType == "SHORT_VIDEO",
                        onClick = { mediaType = if (mediaType == "SHORT_VIDEO") "TEXT" else "SHORT_VIDEO" },
                        label = { Text("Short Video") }
                    )
                }
            }
        },
        confirmButton = {
            WefunGradientButton(
                text = "Publish",
                enabled = content.isNotBlank(),
                onClick = { onPublish(content, hashtag, mediaType, imageUri?.toString()) }
            )
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
