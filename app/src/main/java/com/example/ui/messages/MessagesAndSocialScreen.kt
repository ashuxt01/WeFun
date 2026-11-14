package com.example.ui.messages

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatThread
import com.example.data.model.DirectMessage
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.repository.WefunSeedData
import com.example.ui.components.AvatarCircle
import com.example.ui.components.VipBadge
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TurquoiseActive

@Composable
fun MessagesAndSocialScreen(
    currentUser: UserProfile,
    threads: List<ChatThread>,
    activeThreadId: String?,
    activeMessages: List<DirectMessage>,
    blockedUserIds: Set<String>,
    onOpenThread: (String?) -> Unit,
    onStartOrOpenChat: (peerId: String, peerName: String, isGroup: Boolean) -> Unit,
    onSendDirectMessage: (threadId: String, text: String, gift: VirtualGift?, voiceSeconds: Int?) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onReactMessage: (DirectMessage, String) -> Unit,
    onToggleMuteThread: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onBlockUser: (String, String) -> Unit,
    onReportTarget: (String, String, String) -> Unit,
) {
    val activeThread = threads.firstOrNull { it.id == activeThreadId }
    if (activeThread != null) {
        BackHandler { onOpenThread(null) }
        ChatDetailPane(
            currentUser = currentUser,
            thread = activeThread,
            messages = activeMessages,
            onBack = { onOpenThread(null) },
            onSend = { txt, gift, voice -> onSendDirectMessage(activeThread.id, txt, gift, voice) },
            onDeleteMessage = onDeleteMessage,
            onReactMessage = onReactMessage,
            onToggleMute = { onToggleMuteThread(activeThread.id) },
            onBlock = { onBlockUser(activeThread.peerId, activeThread.peerName) },
            onReport = { onReportTarget("User", activeThread.peerName, "Reported from private chat") }
        )
        return
    }

    val wefunColors = LocalWefunColors.current
    var searchQuery by remember { mutableStateOf("") }
    var filterTab by remember { mutableIntStateOf(0) } // 0=All, 1=Unread, 2=Groups, 3=Requests
    var showManageMenu by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showFriendsModal by remember { mutableStateOf(false) }

    val unblockedThreads = remember(threads, blockedUserIds) {
        threads.filterNot { it.peerId in blockedUserIds }
    }
    val requestCount = unblockedThreads.count { it.isRequest }

    val visibleThreads = remember(unblockedThreads, filterTab, searchQuery) {
        unblockedThreads.filter { th ->
            val matchesFilter = when (filterTab) {
                1 -> !th.isRequest && th.unreadCount > 0
                2 -> !th.isRequest && th.isGroup
                3 -> th.isRequest
                else -> !th.isRequest
            }
            val matchesQuery = searchQuery.isBlank() ||
                th.peerName.contains(searchQuery, ignoreCase = true) ||
                th.lastMessagePreview.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Screen 3 Header: Chats + Contacts + New Chat (+) + Message Management
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showFriendsModal = true },
                        modifier = Modifier.testTag("chats_friends_button")
                    ) {
                        Icon(Icons.Rounded.People, contentDescription = "Manage Friends & Requests", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(
                        onClick = { showNewChatDialog = true },
                        modifier = Modifier.testTag("chats_new_chat_button")
                    ) {
                        Icon(Icons.Rounded.AddComment, contentDescription = "Start New Chat", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Box {
                        IconButton(
                            onClick = { showManageMenu = true },
                            modifier = Modifier.testTag("chats_manage_button")
                        ) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Message management", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        DropdownMenu(
                            expanded = showManageMenu,
                            onDismissRequest = { showManageMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mark All Read") },
                                onClick = {
                                    showManageMenu = false
                                    onMarkAllRead()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Friend Requests ($requestCount)") },
                                onClick = {
                                    showManageMenu = false
                                    filterTab = 3
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Create Group Chat") },
                                onClick = {
                                    showManageMenu = false
                                    showNewChatDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Rounded Light Search Bar (Reference Screenshot 3)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages & friends...", color = wefunColors.mutedText) },
                leadingIcon = { Icon(Icons.Rounded.Search, null, tint = wefunColors.mutedText) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("chats_search_input")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = filterTab == 0,
                        onClick = { filterTab = 0 },
                        label = { Text("All (${unblockedThreads.count { !it.isRequest }})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterTab == 1,
                        onClick = { filterTab = 1 },
                        label = { Text("Unread (${unblockedThreads.count { !it.isRequest && it.unreadCount > 0 }})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterTab == 2,
                        onClick = { filterTab = 2 },
                        label = { Text("Groups (${unblockedThreads.count { it.isGroup }})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filterTab == 3,
                        onClick = { filterTab = 3 },
                        label = { Text("Requests ($requestCount)") }
                    )
                }
            }
        }

        HorizontalDivider(color = wefunColors.glassBorder)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            if (visibleThreads.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = TurquoiseActive, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No conversations match", style = MaterialTheme.typography.titleMedium)
                        Text("Start a new chat with + or clear filters.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                    }
                }
            } else {
                items(visibleThreads, key = { it.id }) { thread ->
                    ChatListRow(
                        thread = thread,
                        onClick = { onOpenThread(thread.id) },
                        onToggleMute = { onToggleMuteThread(thread.id) },
                        onAccept = { onAcceptRequest(thread.id) },
                        onDecline = { onDeclineRequest(thread.id) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 78.dp, end = 16.dp),
                        color = wefunColors.glassBorder
                    )
                }
            }
        }
    }

    if (showNewChatDialog) {
        val candidates = listOf(
            "host_rhea" to "DJ Rhea",
            "host_kai" to "Kai Storm",
            "host_zara" to "Zara Moon",
            "host_milo" to "Milo Taktik",
            "host_leo" to "Leo Beats",
        )
        var groupName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("New Chat or Group") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Create Group Name (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (groupName.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                showNewChatDialog = false
                                onStartOrOpenChat("grp_${groupName.lowercase()}", groupName, true)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Group: $groupName")
                        }
                    }
                    Text("Quick 1-to-1 Message", style = MaterialTheme.typography.labelMedium, color = TurquoiseActive)
                    candidates.forEach { (uid, name) ->
                        OutlinedButton(
                            onClick = {
                                showNewChatDialog = false
                                onStartOrOpenChat(uid, name, false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(name) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showNewChatDialog = false }) { Text("Close") } }
        )
    }

    if (showFriendsModal) {
        val requests = unblockedThreads.filter { it.isRequest }
        AlertDialog(
            onDismissRequest = { showFriendsModal = false },
            title = { Text("Friends & Requests") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (requests.isEmpty()) {
                        Text("No pending friend requests right now.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                    } else {
                        requests.forEach { req ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(req.peerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(req.lastMessagePreview, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onAcceptRequest(req.id) }) { Text("Accept") }
                                    TextButton(onClick = { onDeclineRequest(req.id) }) { Text("Decline", color = RoseNeon) }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFriendsModal = false }) { Text("Done") }
            }
        )
    }
}

@Composable
private fun ChatListRow(
    thread: ChatThread,
    onClick: () -> Unit,
    onToggleMute: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("chat_thread_${thread.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AvatarCircle(
                initials = thread.peerInitials,
                accentColor = Color(thread.accentHex),
                size = 52.dp,
                vipLevel = thread.vipLevel,
                isSpeaking = false
            )
            if (thread.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(EmeraldMint)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = thread.peerName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (thread.isOfficial) {
                    Surface(shape = RoundedCornerShape(50), color = TurquoiseActive.copy(alpha = 0.16f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Verified, null, tint = TurquoiseActive, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("OFFICIAL", fontSize = 9.sp, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (thread.vipLevel > 0) {
                    VipBadge(vipLevel = thread.vipLevel)
                }
                if (thread.isGroup) {
                    Surface(shape = RoundedCornerShape(50), color = GoldAmber.copy(alpha = 0.2f)) {
                        Text("GROUP", fontSize = 9.sp, color = GoldAmber, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                    }
                }
            }

            Text(
                text = thread.lastMessagePreview,
                style = MaterialTheme.typography.bodySmall,
                color = wefunColors.mutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (thread.isRequest) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Surface(onClick = onAccept, shape = RoundedCornerShape(50), color = TurquoiseActive) {
                        Text("Accept", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                    Surface(onClick = onDecline, shape = RoundedCornerShape(50), color = RoseNeon.copy(alpha = 0.15f)) {
                        Text("Decline", color = RoseNeon, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = thread.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = wefunColors.mutedText
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (thread.isMuted) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsOff,
                        contentDescription = "Muted conversation",
                        tint = wefunColors.mutedText,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onToggleMute)
                    )
                }
                if (thread.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (thread.isMuted) wefunColors.mutedText else TurquoiseActive)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${thread.unreadCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatDetailPane(
    currentUser: UserProfile,
    thread: ChatThread,
    messages: List<DirectMessage>,
    onBack: () -> Unit,
    onSend: (String, VirtualGift?, Int?) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onReactMessage: (DirectMessage, String) -> Unit,
    onToggleMute: () -> Unit,
    onBlock: () -> Unit,
    onReport: () -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    var input by remember { mutableStateOf("") }
    var searchInChat by remember { mutableStateOf("") }
    var showSearchInChat by remember { mutableStateOf(false) }
    var showGiftPicker by remember { mutableStateOf(false) }
    var showStickers by remember { mutableStateOf(false) }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            attachedImageUri = uri
            onSend("📷 Shared a photo attachment", null, null)
        }
    }

    val visibleMessages = remember(messages, searchInChat) {
        if (searchInChat.isBlank()) messages
        else messages.filter { it.text.contains(searchInChat, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 84.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to inbox")
                        }
                        Column {
                            Text(thread.peerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (thread.isMuted) "${thread.lastSeenLabel} • Muted" else thread.lastSeenLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = TurquoiseActive
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = { showSearchInChat = !showSearchInChat }) {
                            Icon(Icons.Rounded.Search, "Search messages")
                        }
                        IconButton(onClick = onToggleMute) {
                            Icon(
                                Icons.Rounded.NotificationsOff,
                                "Mute conversation",
                                tint = if (thread.isMuted) TurquoiseActive else wefunColors.mutedText
                            )
                        }
                        IconButton(onClick = onBlock) { Icon(Icons.Rounded.Block, "Block user", tint = RoseNeon) }
                        IconButton(onClick = onReport) { Icon(Icons.Rounded.Report, "Report user", tint = RoseNeon) }
                    }
                }
                if (showSearchInChat) {
                    OutlinedTextField(
                        value = searchInChat,
                        onValueChange = { searchInChat = it },
                        placeholder = { Text("Filter messages...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(visibleMessages, key = { it.id }) { msg ->
                val isMine = msg.senderId == currentUser.uid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isMine) PurplePrimary else MaterialTheme.colorScheme.surface,
                        border = if (isMine) null else BorderStroke(1.dp, wefunColors.glassBorder),
                        modifier = Modifier.clickable { onReactMessage(msg, "💜") }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            if (!isMine && thread.isGroup) {
                                Text(msg.senderName, style = MaterialTheme.typography.labelSmall, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                            if (msg.giftName != null) {
                                Text("Gift: ${msg.giftName}", style = MaterialTheme.typography.labelMedium, color = RoyalGold)
                            }
                            if (msg.voiceSeconds != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.GraphicEq, null, tint = if (isMine) Color.White else TurquoiseActive, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Voice Message • ${msg.voiceSeconds}s", color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface)
                                }
                            } else {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (msg.reaction != null) {
                                    Text(msg.reaction, style = MaterialTheme.typography.labelSmall)
                                }
                                if (isMine) {
                                    Icon(
                                        Icons.Rounded.DoneAll,
                                        contentDescription = "Read",
                                        tint = if (msg.isRead) CyanPulse else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Icon(
                                        Icons.Rounded.Delete,
                                        contentDescription = "Delete message",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { onDeleteMessage(msg.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showStickers) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("🎉 GG!", "🔥 Let's PK!", "🎤 Sing with us!", "💜 Good vibes", "👑 VIP Salute")) { sticker ->
                    Surface(
                        onClick = {
                            onSend(sticker, null, null)
                            showStickers = false
                        },
                        shape = RoundedCornerShape(50),
                        color = TurquoiseActive.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, TurquoiseActive.copy(alpha = 0.4f))
                    ) {
                        Text(sticker, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }

        Surface(color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = { showGiftPicker = true },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(RoyalGold.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Rounded.CardGiftcard, "Send gift in chat", tint = GoldAmber, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricViolet.copy(alpha = 0.14f))
                ) {
                    Icon(Icons.Rounded.Image, "Share photo", tint = PurplePrimary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { showStickers = !showStickers },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(TurquoiseActive.copy(alpha = 0.14f))
                ) {
                    Icon(Icons.Rounded.EmojiEmotions, "Stickers & emoji", tint = TurquoiseActive, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { onSend("Voice note (6s)", null, 6) },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(TurquoiseActive.copy(alpha = 0.14f))
                ) {
                    Icon(Icons.Rounded.Mic, "Send voice note", tint = TurquoiseActive, modifier = Modifier.size(20.dp))
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Message...") },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).testTag("dm_input")
                )
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            onSend(input, null, null)
                            input = ""
                        }
                    },
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(TurquoiseActive).testTag("dm_send_button")
                ) {
                    Icon(Icons.Rounded.Send, "Send message", tint = Color.White)
                }
            }
        }
    }

    if (showGiftPicker) {
        val quickGifts = WefunSeedData.virtualGifts.take(4)
        AlertDialog(
            onDismissRequest = { showGiftPicker = false },
            title = { Text("Send Gift to ${thread.peerName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickGifts.forEach { gift ->
                        OutlinedButton(
                            onClick = {
                                showGiftPicker = false
                                onSend("Sent ${gift.emoji} ${gift.name}!", gift, null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${gift.emoji} ${gift.name} • ${gift.coinPrice} Coins")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showGiftPicker = false }) { Text("Close") } }
        )
    }
}
