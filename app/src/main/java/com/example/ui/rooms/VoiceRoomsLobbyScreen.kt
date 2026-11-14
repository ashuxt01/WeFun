package com.example.ui.rooms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddHome
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.VoiceRoom
import com.example.ui.components.AvatarCircle
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveVoiceBars
import com.example.ui.components.WefunGradientButton
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepNavyHeading
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TurquoiseActive

private data class FeaturedCarnivalItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val colors: List<Color>,
    val categoryMatch: RoomCategory,
)

@Composable
fun VoiceRoomsLobbyScreen(
    rooms: List<VoiceRoom>,
    onJoinRoom: (VoiceRoom) -> Unit,
    onCreateRoom: (
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
    ) -> Unit,
    onOpenEventsRankings: () -> Unit = {},
    initialCategoryFilter: RoomCategory = RoomCategory.ALL,
    showCreateDialogInitially: Boolean = false,
    onDismissInitialCreate: () -> Unit = {},
) {
    val wefunColors = LocalWefunColors.current
    var headerScopeTab by remember { mutableIntStateOf(1) } // 0 = Related, 1 = All, 2 = Popular
    var selectedCategory by remember(initialCategoryFilter) { mutableStateOf(initialCategoryFilter) }
    var selectedSort by remember { mutableStateOf("Recommended") }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember(showCreateDialogInitially) { mutableStateOf(showCreateDialogInitially) }
    var passwordPromptRoom by remember { mutableStateOf<VoiceRoom?>(null) }
    var enteredPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val featuredCarousels = remember {
        listOf(
            FeaturedCarnivalItem(
                title = "WEFUN PK Carnival",
                subtitle = "1v1 & Team PK • Double Contribution Spotlight",
                badge = "LIVE PK",
                colors = listOf(PurplePrimary, PurpleDeep),
                categoryMatch = RoomCategory.PK
            ),
            FeaturedCarnivalItem(
                title = "Beast Battle",
                subtitle = "Mythic Gift Bosses & Champion Frames",
                badge = "HOT EVENT",
                colors = listOf(Color(0xFFE11D48), Color(0xFF9F1239)),
                categoryMatch = RoomCategory.OFFICIAL
            ),
            FeaturedCarnivalItem(
                title = "Family Championship",
                subtitle = "Guild Ranks • Weekly Trophy War",
                badge = "FAMILIES",
                colors = listOf(TurquoiseActive, Color(0xFF0284C7)),
                categoryMatch = RoomCategory.FAMILY
            ),
            FeaturedCarnivalItem(
                title = "Special Events",
                subtitle = "Starlight Acoustic & Singing Grand Prix",
                badge = "FEATURED",
                colors = listOf(GoldAmber, Color(0xFFB45309)),
                categoryMatch = RoomCategory.MUSIC
            ),
        )
    }

    val categoryTabs = remember {
        listOf(
            RoomCategory.ALL to "All",
            RoomCategory.DATING to "Friends",
            RoomCategory.PK to "PK",
            RoomCategory.MUSIC to "Music",
            RoomCategory.VIDEO to "Video",
            RoomCategory.GAME to "Games",
        )
    }

    val sortOptions = remember {
        listOf("Recommended", "Most Popular", "Friends", "New Rooms")
    }

    val visibleRooms = remember(rooms, headerScopeTab, selectedCategory, selectedSort, searchQuery) {
        val base = rooms.filter { rm ->
            val matchesScope = when (headerScopeTab) {
                0 -> rm.isFavorited || rm.category == RoomCategory.FAMILY || rm.hostId == "usr_nova_01"
                2 -> rm.popularity >= 25000L || rm.onlineCount >= 100
                else -> true
            }
            val matchesCat = when (selectedCategory) {
                RoomCategory.ALL -> true
                RoomCategory.DATING -> rm.category == RoomCategory.DATING || rm.category == RoomCategory.PUBLIC
                else -> rm.category == selectedCategory
            }
            val matchesQuery = searchQuery.isBlank() ||
                rm.title.contains(searchQuery, ignoreCase = true) ||
                rm.hostName.contains(searchQuery, ignoreCase = true) ||
                rm.id.contains(searchQuery, ignoreCase = true)
            matchesScope && matchesCat && matchesQuery
        }

        when (selectedSort) {
            "Most Popular" -> base.sortedByDescending { it.popularity }
            "Friends" -> base.sortedByDescending { it.isFavorited }
            "New Rooms" -> base.sortedByDescending { it.createdAtMillis }
            else -> base
        }
    }

    val handleOpenRoom: (VoiceRoom) -> Unit = { room ->
        if (!room.password.isNullOrBlank()) {
            passwordPromptRoom = room
            enteredPassword = ""
            passwordError = null
        } else {
            onJoinRoom(room)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen 2 Header: Related / All / Popular + Search & Create Room (+)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Voice Room",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Related", "All", "Popular").forEachIndexed { idx, label ->
                            val selected = headerScopeTab == idx
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { headerScopeTab = idx }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = if (selected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.onSurface else wefunColors.mutedText
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (selected) TurquoiseActive else Color.Transparent)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier.testTag("fab_create_room")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddHome,
                                contentDescription = "Create Voice Room",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier.testTag("voice_rooms_search_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search Voice Rooms",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (showSearchBar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search room name, host or Room ID...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            HorizontalDivider(color = wefunColors.glassBorder)
        }

        // Featured Promotional Carousel Banner (WEFUN PK Carnival, Beast Battle, Family Championship, Special Events)
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredCarousels) { banner ->
                    Box(
                        modifier = Modifier
                            .width(304.dp)
                            .height(104.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(banner.colors))
                            .clickable {
                                val matching = rooms.firstOrNull { it.category == banner.categoryMatch }
                                if (matching != null) handleOpenRoom(matching) else onOpenEventsRankings()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.22f)) {
                                    Text(
                                        text = banner.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = banner.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = banner.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Overlapping Host Avatar Stack
                            Box(modifier = Modifier.width(68.dp).height(34.dp)) {
                                listOf("DR" to 0xFFF59E0B, "KS" to 0xFF38BDF8, "AV" to 0xFFF43F5E).forEachIndexed { idx, (init, col) ->
                                    Box(
                                        modifier = Modifier
                                            .offset(x = (idx * 16).dp)
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(col))
                                            .border(2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(init, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Pills: All, Friends, PK, Music, Video, Games
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryTabs) { (cat, label) ->
                    val selected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(50),
                        color = if (selected) TurquoiseActive else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selected) TurquoiseActive else wefunColors.glassBorder)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Sorting Bar: Recommended, Most Popular, Friends, New Rooms
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortOptions) { sort ->
                    FilterChip(
                        selected = selectedSort == sort,
                        onClick = { selectedSort = sort },
                        label = { Text(sort, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        if (visibleRooms.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.GraphicEq, null, tint = TurquoiseActive, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No matching voice rooms", style = MaterialTheme.typography.titleMedium)
                        Text("Try switching category tabs or create a new room.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                    }
                }
            }
        } else {
            items(visibleRooms, key = { it.id }) { room ->
                VoiceRoomReferenceListCard(
                    room = room,
                    onClick = { handleOpenRoom(room) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (passwordPromptRoom != null) {
        val target = passwordPromptRoom!!
        AlertDialog(
            onDismissRequest = { passwordPromptRoom = null },
            icon = { Icon(Icons.Rounded.Lock, null, tint = TurquoiseActive) },
            title = { Text("Enter Room Passcode") },
            text = {
                Column {
                    Text("‘${target.title}’ is password-protected (Demo code: ${target.password}).")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = enteredPassword,
                        onValueChange = { enteredPassword = it },
                        label = { Text("Passcode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (enteredPassword == target.password) {
                            passwordPromptRoom = null
                            onJoinRoom(target)
                        } else {
                            passwordError = "Incorrect passcode."
                        }
                    }
                ) { Text("Unlock & Join") }
            },
            dismissButton = {
                TextButton(onClick = { passwordPromptRoom = null }) { Text("Cancel") }
            }
        )
    }

    if (showCreateDialog) {
        CreateVoiceRoomDialog(
            onDismiss = {
                showCreateDialog = false
                onDismissInitialCreate()
            },
            onCreate = { title, desc, cat, seats, pass, theme ->
                showCreateDialog = false
                onDismissInitialCreate()
                onCreateRoom(title, desc, cat, seats, pass, theme)
            }
        )
    }
}

@Composable
private fun VoiceRoomReferenceListCard(
    room: VoiceRoom,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val wefunColors = LocalWefunColors.current
    val categoryColor = Color(room.themeStyle.primaryHex)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Square Rounded Room Cover Avatar (Reference Screenshot 2)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(room.themeStyle.primaryHex), Color(room.themeStyle.secondaryHex))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(
                    initials = room.hostName.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                    accentColor = categoryColor,
                    size = 44.dp,
                    vipLevel = room.hostVip,
                    isSpeaking = true
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = room.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!room.password.isNullOrBlank()) {
                        Icon(Icons.Rounded.Lock, "Locked room", tint = GoldAmber, modifier = Modifier.size(14.dp))
                    }
                }

                // Category Badge + PK Status + 3 Room Level / Achievement Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TurquoiseActive.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = room.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TurquoiseActive,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (room.isPkActive) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoseNeon.copy(alpha = 0.16f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Bolt, null, tint = RoseNeon, modifier = Modifier.size(11.dp))
                                Text("PK", style = MaterialTheme.typography.labelSmall, color = RoseNeon, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 3 Room Level / Achievement Badges
                    RoomMiniBadge(Icons.Rounded.Star, "Lv.${room.roomLevel}", GoldAmber)
                    RoomMiniBadge(Icons.Rounded.EmojiEvents, "V${room.hostVip}", PurplePrimary)
                    RoomMiniBadge(Icons.Rounded.AutoAwesome, formatCompactCount(room.popularity), TurquoiseActive)
                }

                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = wefunColors.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LiveVoiceBars(isActive = true, rmsLevel = 0.68f)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Rounded.People, contentDescription = "Online members", tint = wefunColors.mutedText, modifier = Modifier.size(14.dp))
                    Text(
                        text = "${room.onlineCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomMiniBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = tint.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(10.dp))
            Text(label, fontSize = 10.sp, color = tint, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateVoiceRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
    ) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(RoomCategory.PUBLIC) }
    var password by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf(RoomThemeStyle.GALAXY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Voice Room") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Room Name") },
                    placeholder = { Text("e.g., Late Night Acoustic Party") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("create_room_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Room Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        RoomCategory.PUBLIC,
                        RoomCategory.PRIVATE,
                        RoomCategory.PASSWORD,
                        RoomCategory.MUSIC,
                        RoomCategory.VIDEO,
                        RoomCategory.DATING,
                        RoomCategory.GAME,
                        RoomCategory.FAMILY,
                        RoomCategory.PK
                    ).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
                if (category == RoomCategory.PASSWORD || category == RoomCategory.PRIVATE) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Optional Passcode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    "Room Level 1 Standard: 8 Regular Mic Seats + Owner & Partner (Levels 2–10 unlock up to 26 seats via gifts)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TurquoiseActive
                )
                Text("Room Theme", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoomThemeStyle.entries.forEach { st ->
                        FilterChip(
                            selected = theme == st,
                            onClick = { theme = st },
                            label = { Text(st.label, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            WefunGradientButton(
                text = "Go Live",
                testTag = "confirm_create_room_button",
                onClick = {
                    onCreate(
                        title,
                        description,
                        category,
                        8,
                        password.takeIf { it.isNotBlank() },
                        theme
                    )
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
