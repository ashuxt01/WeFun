package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DailyTaskItem
import com.example.data.model.EventItem
import com.example.data.model.GameId
import com.example.data.model.GameMatchRecord
import com.example.data.model.PartyGame
import com.example.data.model.RoomCategory
import com.example.data.model.UserProfile
import com.example.data.model.VoiceRoom
import com.example.data.repository.WefunSeedData
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.WefunBrandLogo
import com.example.ui.components.WefunGradientButton
import com.example.ui.components.formatCompactCount
import com.example.ui.games.PlayableGameDialog
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepNavyHeading
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LightLavenderTop
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TurquoiseActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WefunHomeGamesScreen(
    user: UserProfile,
    rooms: List<VoiceRoom>,
    events: List<EventItem>,
    matches: List<GameMatchRecord>,
    dailyTasks: List<DailyTaskItem>,
    followedUserIds: Set<String>,
    initialGameToLaunch: GameId? = null,
    onClearInitialGame: () -> Unit = {},
    onOpenWallet: () -> Unit,
    onOpenEventsRankings: () -> Unit,
    onOpenRoom: (VoiceRoom) -> Unit,
    onOpenGameRooms: () -> Unit,
    onOpenProfileTab: () -> Unit,
    onOpenChatWithPeer: (peerId: String, peerName: String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onClaimDailyTask: (String) -> Unit,
    onRecordMatchResult: (gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) -> Unit,
    onInviteFriendsToast: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wefunColors = LocalWefunColors.current
    var selectedSectionTab by remember { mutableIntStateOf(0) } // 0 = Games, 1 = History, 2 = Rankings
    var selectedCategory by remember { mutableStateOf("All") }
    var lobbyGame by remember(initialGameToLaunch) {
        mutableStateOf(initialGameToLaunch?.let { id -> WefunSeedData.partyGames.firstOrNull { it.id == id } })
    }
    var activeGame by remember { mutableStateOf<PartyGame?>(null) }
    var showTasksSheet by remember { mutableStateOf(false) }
    var showFriendsSheet by remember { mutableStateOf(false) }

    val categories = remember {
        listOf("All", "Social Deduction", "Card & Board", "Simulation", "Arcade Party", "Trivia Speed", "Music Stage")
    }
    val filteredGames = remember(selectedCategory) {
        if (selectedCategory == "All") WefunSeedData.partyGames
        else WefunSeedData.partyGames.filter { it.category == selectedCategory }
    }
    val featuredBanners = remember(filteredGames) {
        filteredGames.filter { it.isFeaturedBanner }
    }
    val gridGames = remember(filteredGames) {
        filteredGames.filterNot { it.isFeaturedBanner }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header + Soft Lavender Sky Backdrop (Reference Screenshot 1)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            if (wefunColors.isDark) {
                                listOf(Color(0xFF1F1B30), MaterialTheme.colorScheme.background)
                            } else {
                                listOf(LightLavenderTop, MaterialTheme.colorScheme.background)
                            }
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WefunBrandLogo(
                            size = 36.dp,
                            cornerRadius = 10.dp,
                            showGoldBorder = true,
                            modifier = Modifier.testTag("home_brand_logo")
                        )
                        Text(
                            text = "WEFUN",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PurplePrimary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "PARTY HUB",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurplePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                // Header Row: User Avatar + Coin Balance Pill (+ button) + Jungle King + Events
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(2.dp, TurquoiseActive, CircleShape)
                                .clickable(onClick = onOpenProfileTab)
                                .testTag("home_avatar_button")
                        ) {
                            if (user.photoUri != null) {
                                AsyncImage(
                                    model = user.photoUri,
                                    contentDescription = "User avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                    contentDescription = "User avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Coin Balance Pill with Add Coins (+) Button
                        Surface(
                            onClick = onOpenWallet,
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, GoldAmber.copy(alpha = 0.45f)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.testTag("home_coin_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 8.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(GoldAmber),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.MonetizationOn, null, tint = Color.White, modifier = Modifier.size(15.dp))
                                }
                                Text(
                                    text = formatCompactCount(user.coins),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(GoldAmber.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = "Add Coins", tint = GoldAmber, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Jungle King / Featured Event Shortcut
                        Surface(
                            onClick = {
                                val target = rooms.firstOrNull { it.category == RoomCategory.OFFICIAL } ?: rooms.firstOrNull()
                                if (target != null) onOpenRoom(target) else onOpenEventsRankings()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.25f)),
                            modifier = Modifier.testTag("jungle_king_shortcut")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Jungle", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
                                Text("King", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = GoldAmber, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        // Events Shortcut
                        Surface(
                            onClick = onOpenEventsRankings,
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, TurquoiseActive.copy(alpha = 0.35f)),
                            modifier = Modifier.testTag("home_events_shortcut")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = "Events", tint = TurquoiseActive, modifier = Modifier.size(17.dp))
                                Text("Events", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                // Quick Action Section: Ranking, Tasks, Friends, Game Room
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionBadge(
                        label = "Ranking",
                        icon = Icons.Rounded.EmojiEvents,
                        gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                        badgeTint = GoldAccent,
                        onClick = onOpenEventsRankings,
                        testTag = "quick_action_ranking"
                    )
                    QuickActionBadge(
                        label = "Tasks",
                        icon = Icons.Rounded.TaskAlt,
                        gradientColors = listOf(Color(0xFF00C2B8), Color(0xFF0284C7)),
                        badgeTint = Color.White,
                        badgeCount = dailyTasks.count { !it.isClaimed },
                        onClick = { showTasksSheet = true },
                        testTag = "quick_action_tasks"
                    )
                    QuickActionBadge(
                        label = "Friends",
                        icon = Icons.Rounded.People,
                        gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                        badgeTint = Color.White,
                        onClick = { showFriendsSheet = true },
                        testTag = "quick_action_friends"
                    )
                    QuickActionBadge(
                        label = "Game Room",
                        icon = Icons.Rounded.MeetingRoom,
                        gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                        badgeTint = Color.White,
                        onClick = onOpenGameRooms,
                        testTag = "quick_action_gameroom"
                    )
                }
            }
        }

        // Main Games Heading + Game Room Shortcut + Sub-Tabs (Games / History / Rankings)
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Games",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = TurquoiseActive.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "${WefunSeedData.partyGames.size} Live",
                                style = MaterialTheme.typography.labelSmall,
                                color = TurquoiseActive,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onOpenGameRooms,
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, wefunColors.glassBorder),
                        modifier = Modifier.testTag("game_room_shortcut")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.MeetingRoom, null, tint = TurquoiseActive, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Game Room", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = wefunColors.mutedText, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // View Switcher: Games / History / Rankings
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Games", "Game History (${matches.size})", "Rankings").forEachIndexed { idx, label ->
                        FilterChip(
                            selected = selectedSectionTab == idx,
                            onClick = { selectedSectionTab = idx },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        when (selectedSectionTab) {
            0 -> {
                // Game Categories Filter
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }

                // Full-width Horizontal Illustrated Banners (Space Werewolf, Jackaroo, Petrol Pump Simulator, Who's the Spy)
                items(featuredBanners, key = { it.id.name }) { game ->
                    FeaturedHorizontalGameBanner(
                        game = game,
                        onClick = { lobbyGame = game },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Two-Column Illustrated Game Cards (Paint to Hide, Trickster's Cafe, Hide & Seek, Oh My Card, Ludo, Quiz Battle, etc.)
                val chunked = gridGames.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { game ->
                            TwoColumnGameCard(
                                game = game,
                                onClick = { lobbyGame = game },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Events Banner Card Preview
                if (events.isNotEmpty()) {
                    val topEvent = events.first()
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            borderColor = PurplePrimary.copy(alpha = 0.25f),
                            onClick = onOpenEventsRankings
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("TOURNAMENT SPOTLIGHT", style = MaterialTheme.typography.labelSmall, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(topEvent.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${topEvent.schedule} • ${topEvent.rewardSummary}", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = wefunColors.mutedText)
                            }
                        }
                    }
                }
            }

            1 -> {
                if (matches.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Rounded.History, null, tint = TurquoiseActive, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No matches recorded yet", style = MaterialTheme.typography.titleMedium)
                                Text("Pick any game banner above to start a match!", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                            }
                        }
                    }
                } else {
                    items(matches, key = { it.id }) { m ->
                        GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(m.gameTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("${m.outcome} • Score ${m.score}", style = MaterialTheme.typography.bodySmall, color = TurquoiseActive)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("+${m.coinsWon} Coins", style = MaterialTheme.typography.labelLarge, color = GoldAmber, fontWeight = FontWeight.Bold)
                                    Text("+${m.xpEarned} XP", style = MaterialTheme.typography.labelSmall, color = EmeraldMint)
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                val board = listOf(
                    Triple(1, "Milo Taktik • 94 Wins", 18420L),
                    Triple(2, "Aria Vance • 78 Wins", 15900L),
                    Triple(3, "Kai Storm • 71 Wins", 14210L),
                    Triple(4, "DJ Rhea • 67 Wins", 13450L),
                    Triple(5, "Sanya Star • 64 Wins", 12800L),
                )
                items(board) { (rank, title, pts) ->
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.EmojiEvents,
                                    null,
                                    tint = if (rank == 1) GoldAmber else TurquoiseActive
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("#$rank  $title", style = MaterialTheme.typography.titleSmall)
                            }
                            Text("$pts pts", style = MaterialTheme.typography.labelLarge, color = GoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Daily Tasks Modal Sheet
    if (showTasksSheet) {
        ModalBottomSheet(onDismissRequest = { showTasksSheet = false }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Daily Missions & Rewards", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Complete social & game tasks to earn bonus Coins and XP.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                }
                items(dailyTasks, key = { it.id }) { task ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(task.description, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                Text("+${task.rewardCoins} Coins • +${task.rewardXp} XP", style = MaterialTheme.typography.labelSmall, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (task.isClaimed) {
                                Surface(shape = RoundedCornerShape(50), color = EmeraldMint.copy(alpha = 0.16f)) {
                                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CheckCircle, null, tint = EmeraldMint, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Claimed", style = MaterialTheme.typography.labelSmall, color = EmeraldMint)
                                    }
                                }
                            } else {
                                Surface(
                                    onClick = { onClaimDailyTask(task.id) },
                                    shape = RoundedCornerShape(50),
                                    color = TurquoiseActive
                                ) {
                                    Text(
                                        text = if (task.isCompleted) "Claim" else "Complete",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Online Friends Modal Sheet
    if (showFriendsSheet) {
        val people = remember {
            listOf(
                Triple("host_rhea", "DJ Rhea", 5 to 0xFF8B5CF6),
                Triple("host_kai", "Kai Storm", 6 to 0xFFF43F5E),
                Triple("host_zara", "Zara Moon", 4 to 0xFFEC4899),
                Triple("host_milo", "Milo Taktik", 3 to 0xFF00C2B8),
                Triple("host_leo", "Leo Beats", 6 to 0xFFF59E0B),
            )
        }
        ModalBottomSheet(onDismissRequest = { showFriendsSheet = false }) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Online Friends", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Follow, invite to games, or jump into a direct chat.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                }
                items(people, key = { it.first }) { (uid, name, meta) ->
                    val isFollowing = uid in followedUserIds
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                AvatarCircle(
                                    initials = name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                    accentColor = Color(meta.second),
                                    size = 44.dp,
                                    vipLevel = meta.first,
                                    isSpeaking = true
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("VIP ${meta.first} • Online", style = MaterialTheme.typography.labelSmall, color = EmeraldMint)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(onClick = { onToggleFollow(uid) }) {
                                    Text(if (isFollowing) "Following" else "Follow", style = MaterialTheme.typography.labelSmall)
                                }
                                Surface(
                                    onClick = {
                                        showFriendsSheet = false
                                        onOpenChatWithPeer(uid, name)
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = TurquoiseActive
                                ) {
                                    Text("Chat", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Game Detail & Multiplayer Lobby Modal
    if (lobbyGame != null) {
        val g = lobbyGame!!
        AlertDialog(
            onDismissRequest = {
                lobbyGame = null
                onClearInitialGame()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(g.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (g.isNew) {
                        Surface(shape = RoundedCornerShape(50), color = RoseNeon) {
                            Text("NEW", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(g.rules, style = MaterialTheme.typography.bodyMedium)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Category: ${g.category} (${g.maxPlayers}-player room)", style = MaterialTheme.typography.bodySmall)
                            Text("Players Active: ${g.playersOnline} online", style = MaterialTheme.typography.bodySmall, color = TurquoiseActive)
                            Text("Entry: ${if (g.entryCoins == 0L) "Free" else "${g.entryCoins} Coins"}", style = MaterialTheme.typography.bodySmall)
                            Text("Winner Reward: +${g.rewardCoins} Coins & +120 XP", style = MaterialTheme.typography.bodySmall, color = GoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onInviteFriendsToast("Sent ${g.title} lobby invite to online friends!") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.GroupAdd, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Invite", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = {
                                lobbyGame = null
                                onClearInitialGame()
                                onOpenGameRooms()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.MeetingRoom, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Voice Room", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            },
            confirmButton = {
                WefunGradientButton(
                    text = "Start Match",
                    testTag = "start_game_match_button",
                    colors = listOf(TurquoiseActive, PurplePrimary),
                    onClick = {
                        activeGame = g
                        lobbyGame = null
                        onClearInitialGame()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    lobbyGame = null
                    onClearInitialGame()
                }) { Text("Close") }
            }
        )
    }

    if (activeGame != null) {
        PlayableGameDialog(
            game = activeGame!!,
            onDismiss = { activeGame = null },
            onComplete = { outcome, score, coins, xp ->
                val title = activeGame!!.title
                activeGame = null
                onRecordMatchResult(title, outcome, score, coins, xp)
            }
        )
    }
}

@Composable
private fun QuickActionBadge(
    label: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    badgeTint: Color,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(gradientColors))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = badgeTint, modifier = Modifier.size(24.dp))
            }
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(RoseNeon),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$badgeCount", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun FeaturedHorizontalGameBanner(
    game: PartyGame,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val startColor = Color(game.accentHex)
    val endColor = Color(game.secondaryAccentHex)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(startColor, endColor)))
            .border(1.5.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("game_card_${game.id.name}")
    ) {
        // Subtle decorative inner border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (game.isNew) {
                        Surface(shape = RoundedCornerShape(50), color = GoldAccent) {
                            Text(
                                text = "NEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = DeepNavyHeading,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${game.playersOnline} playing • Win +${game.rewardCoins} Coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }

            // Custom Original Game Artwork on Right
            GameArtworkIllustration(
                gameId = game.id,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

@Composable
private fun TwoColumnGameCard(
    game: PartyGame,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val startColor = Color(game.accentHex)
    val endColor = Color(game.secondaryAccentHex)
    Box(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(startColor, endColor)))
            .border(1.5.dp, Color.White.copy(alpha = 0.42f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("game_card_${game.id.name}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.5.dp)
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (game.isNew) {
                        Surface(shape = RoundedCornerShape(50), color = GoldAccent) {
                            Text(
                                text = "NEW",
                                fontSize = 9.sp,
                                color = DeepNavyHeading,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${game.playersOnline} live",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            GameArtworkIllustration(
                gameId = game.id,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun GameArtworkIllustration(
    gameId: GameId,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Soft glowing halo
        drawCircle(
            color = Color.White.copy(alpha = 0.16f),
            radius = w * 0.46f,
            center = Offset(cx, cy)
        )

        when (gameId) {
            GameId.SPACE_WEREWOLF -> {
                // Crescent moon + starship helmet silhouette
                drawCircle(color = Color.White.copy(alpha = 0.9f), radius = w * 0.28f, center = Offset(cx, cy))
                drawRoundRect(
                    color = Color(0xFF1E1B4B),
                    topLeft = Offset(cx - w * 0.18f, cy - h * 0.12f),
                    size = Size(w * 0.36f, h * 0.18f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawCircle(color = GoldAccent, radius = w * 0.06f, center = Offset(w * 0.18f, h * 0.22f))
                drawCircle(color = CyanPulse, radius = w * 0.04f, center = Offset(w * 0.84f, h * 0.78f))
            }
            GameId.JACKAROO, GameId.OH_MY_CARD -> {
                // Layered playing cards + diamond suit
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.75f),
                    topLeft = Offset(cx - w * 0.28f, cy - h * 0.22f),
                    size = Size(w * 0.36f, h * 0.48f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cx - w * 0.08f, cy - h * 0.28f),
                    size = Size(w * 0.38f, h * 0.52f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                val diamond = Path().apply {
                    moveTo(cx + w * 0.11f, cy - h * 0.12f)
                    lineTo(cx + w * 0.20f, cy)
                    lineTo(cx + w * 0.11f, cy + h * 0.12f)
                    lineTo(cx + w * 0.02f, cy)
                    close()
                }
                drawPath(diamond, color = RoseNeon)
            }
            GameId.PETROL_PUMP_TYCOON -> {
                // Stylized fuel dispenser + gold coin
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cx - w * 0.22f, cy - h * 0.28f),
                    size = Size(w * 0.42f, h * 0.58f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color(0xFF0077B6),
                    topLeft = Offset(cx - w * 0.15f, cy - h * 0.20f),
                    size = Size(w * 0.28f, h * 0.18f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawCircle(color = GoldAccent, radius = w * 0.11f, center = Offset(cx + w * 0.20f, cy + h * 0.18f))
            }
            GameId.WHOS_THE_SPY -> {
                // Magnifying lens + clue reticle
                drawCircle(
                    color = Color.White,
                    radius = w * 0.24f,
                    center = Offset(cx - w * 0.06f, cy - h * 0.06f),
                    style = Stroke(width = w * 0.07f)
                )
                drawLine(
                    color = GoldAccent,
                    start = Offset(cx + w * 0.12f, cy + h * 0.12f),
                    end = Offset(cx + w * 0.32f, cy + h * 0.32f),
                    strokeWidth = w * 0.08f
                )
            }
            GameId.LUDO -> {
                // Crisp die face
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cx - w * 0.26f, cy - h * 0.26f),
                    size = Size(w * 0.52f, h * 0.52f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                listOf(
                    Offset(cx - w * 0.12f, cy - h * 0.12f),
                    Offset(cx + w * 0.12f, cy - h * 0.12f),
                    Offset(cx, cy),
                    Offset(cx - w * 0.12f, cy + h * 0.12f),
                    Offset(cx + w * 0.12f, cy + h * 0.12f),
                ).forEach { pt ->
                    drawCircle(color = PurplePrimary, radius = w * 0.055f, center = pt)
                }
            }
            else -> {
                // Vibrant geometric emblem
                drawCircle(color = Color.White.copy(alpha = 0.9f), radius = w * 0.24f, center = Offset(cx, cy))
                drawCircle(color = GoldAccent, radius = w * 0.11f, center = Offset(cx, cy))
            }
        }
    }
}
