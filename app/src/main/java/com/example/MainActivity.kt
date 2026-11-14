package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.GameId
import com.example.data.model.RoomCategory
import com.example.ui.auth.OnboardingAuthScreen
import com.example.ui.auth.WefunAppLoadingScreen
import com.example.ui.auth.WefunSplashScreen
import com.example.ui.components.LiveVoiceBars
import com.example.ui.navigation.AppNavigation
import com.example.ui.navigation.BottomNavigationBar
import com.example.ui.navigation.WefunDestination
import com.example.ui.navigation.WefunNavigationRail
import com.example.ui.navigation.navigateToWefunDestination
import com.example.ui.profile.AdminAndSafetySheet
import com.example.ui.profile.AvatarAndProfileEditSheet
import com.example.ui.profile.EventsAndRankingsSheet
import com.example.ui.profile.FamiliesSheet
import com.example.ui.profile.NotificationsSheet
import com.example.ui.profile.VipMembershipSheet
import com.example.ui.profile.WalletSheet
import com.example.ui.rooms.LiveVoiceRoomScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.WefunViewModel

private enum class ActiveSheet {
    NONE, WALLET, VIP, AVATAR_EDIT, FAMILIES, EVENTS_RANKINGS, NOTIFICATIONS, ADMIN_SAFETY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WefunViewModel = viewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            WEFUNTheme(darkTheme = settings.isDarkMode) {
                WefunAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WefunAppRoot(viewModel: WefunViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val rooms by viewModel.rooms.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val activeThreadId by viewModel.activeThreadId.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val gameMatches by viewModel.gameMatches.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val families by viewModel.families.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val followedUserIds by viewModel.followedUserIds.collectAsStateWithLifecycle()
    val dailyTasks by viewModel.dailyTasks.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val liveRoomState by viewModel.liveRoomState.collectAsStateWithLifecycle()
    val roomLevelConfigs by viewModel.roomLevelConfigs.collectAsStateWithLifecycle()
    val activeRoomContribution by viewModel.activeRoomContribution.collectAsStateWithLifecycle()
    val activeRoomContributors by viewModel.activeRoomContributors.collectAsStateWithLifecycle()
    val activeRoomGiftLedger by viewModel.activeRoomGiftLedger.collectAsStateWithLifecycle()
    val activeRoomActivityLogs by viewModel.activeRoomActivityLogs.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }
    if (showSplash) {
        WefunSplashScreen(onFinished = { showSplash = false })
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            snackbarHostState.showSnackbar(toastMessage!!)
            viewModel.consumeToast()
        }
    }

    if (!settings.isLoggedIn) {
        OnboardingAuthScreen(
            onAuthCompleted = { nickname, username, phone, email, dob, gender, country, language, photoUri ->
                viewModel.completeSignIn(
                    nickname, username, phone, email, dob, gender, country, language, photoUri
                )
            }
        )
        return
    }

    val user = currentUser
    if (user == null) {
        WefunAppLoadingScreen()
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: WefunDestination.Home.route

    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }
    var openCreateRoomOnVoiceTab by remember { mutableStateOf(false) }
    var initialVoiceRoomCategory by remember { mutableStateOf(RoomCategory.ALL) }
    var pendingGameId by remember { mutableStateOf<GameId?>(null) }

    val unreadChats = threads.sumOf { it.unreadCount }
    val unreadNotifs = notifications.count { !it.isRead }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!isWideScreen) {
                    Column {
                        if (liveRoomState.room != null && liveRoomState.isMinimized) {
                            MinimizedRoomBanner(
                                title = liveRoomState.room!!.title,
                                hostName = liveRoomState.room!!.hostName,
                                onRestore = { viewModel.restoreVoiceRoom() },
                                onLeave = { viewModel.leaveVoiceRoom() }
                            )
                        }
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onSelectDestination = { destination ->
                                navController.navigateToWefunDestination(destination)
                            },
                            unreadChatsCount = unreadChats,
                            unreadNotificationsCount = unreadNotifs,
                            showVipGoldIndicator = user.vipLevel > 0,
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    WefunNavigationRail(
                        currentRoute = currentRoute,
                        onSelectDestination = { destination ->
                            navController.navigateToWefunDestination(destination)
                        },
                        unreadChatsCount = unreadChats,
                        unreadNotificationsCount = unreadNotifs,
                        showVipGoldIndicator = user.vipLevel > 0,
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AppNavigation(
                        navController = navController,
                        user = user,
                        settings = settings,
                        rooms = rooms,
                        events = events,
                        gameMatches = gameMatches,
                        dailyTasks = dailyTasks,
                        posts = posts,
                        families = families,
                        threads = threads,
                        activeThreadId = activeThreadId,
                        activeMessages = activeMessages,
                        followedUserIds = followedUserIds,
                        pendingGameId = pendingGameId,
                        initialVoiceRoomCategory = initialVoiceRoomCategory,
                        openCreateRoomOnVoiceTab = openCreateRoomOnVoiceTab,
                        onClearPendingGame = { pendingGameId = null },
                        onOpenWallet = { activeSheet = ActiveSheet.WALLET },
                        onOpenEventsRankings = { activeSheet = ActiveSheet.EVENTS_RANKINGS },
                        onOpenFamilies = { activeSheet = ActiveSheet.FAMILIES },
                        onOpenVip = { activeSheet = ActiveSheet.VIP },
                        onOpenAvatarEditor = { activeSheet = ActiveSheet.AVATAR_EDIT },
                        onOpenNotifications = { activeSheet = ActiveSheet.NOTIFICATIONS },
                        onOpenAdminPanel = { activeSheet = ActiveSheet.ADMIN_SAFETY },
                        onOpenRoom = { room -> viewModel.joinRoom(room) },
                        onCreateRoom = { title, desc, cat, seats, pass, theme ->
                            viewModel.createAndJoinRoom(title, desc, cat, seats, pass, theme)
                        },
                        onDismissInitialCreateRoom = { openCreateRoomOnVoiceTab = false },
                        onOpenGameRooms = {
                            initialVoiceRoomCategory = RoomCategory.GAME
                            navController.navigateToWefunDestination(WefunDestination.Party)
                        },
                        onStartOrOpenChat = { uid, name, isGrp ->
                            viewModel.startOrOpenChat(uid, name, isGrp)
                        },
                        onOpenThread = { id -> viewModel.openThread(id) },
                        onSendDirectMessage = { tid, txt, gift, voice ->
                            viewModel.sendDirectMessage(tid, txt, gift, voice)
                        },
                        onDeleteMessage = { id -> viewModel.deleteDirectMessage(id) },
                        onReactMessage = { msg, emoji -> viewModel.reactToDirectMessage(msg, emoji) },
                        onToggleMuteThread = { tid -> viewModel.toggleMuteThread(tid) },
                        onMarkAllRead = { viewModel.markAllThreadsRead() },
                        onAcceptRequest = { tid -> viewModel.acceptFriendRequest(tid) },
                        onDeclineRequest = { tid -> viewModel.declineFriendRequest(tid) },
                        onBlockUser = { uid, name -> viewModel.toggleBlockUser(uid, name) },
                        onReportTarget = { type, name, reason -> viewModel.submitReport(type, name, reason) },
                        onToggleFollow = { uid -> viewModel.toggleFollow(uid) },
                        onClaimDailyTask = { taskId -> viewModel.claimDailyTask(taskId) },
                        onRecordMatchResult = { title, outcome, score, coins, xp ->
                            viewModel.recordGameMatch(title, outcome, score, coins, xp)
                        },
                        onCreatePost = { content, tag, type, uri ->
                            viewModel.createSocialPost(content, tag, type, uri)
                        },
                        onToggleLikePost = { post -> viewModel.toggleLikePost(post) },
                        onCommentPost = { post -> viewModel.commentOnPost(post) },
                        onToggleDarkMode = { enabled -> viewModel.setDarkMode(enabled) },
                        onToggleParentalControl = { enabled -> viewModel.setParentalControl(enabled) },
                        onSelectLanguage = { lang -> viewModel.updateLanguage(lang) },
                        onPickProfilePhoto = { uri ->
                            viewModel.updateProfileAndAvatar(
                                user.nickname,
                                user.bio,
                                user.country,
                                user.language,
                                user.showGender,
                                user.showAge,
                                uri,
                                user.avatarConfig
                            )
                        },
                        onSignOut = { viewModel.signOut() },
                        onDeleteAccount = { viewModel.deleteAccount() },
                        onShowToast = { msg -> viewModel.showToast(msg) },
                    )

                    if (isWideScreen && liveRoomState.room != null && liveRoomState.isMinimized) {
                        MinimizedRoomBanner(
                            title = liveRoomState.room!!.title,
                            hostName = liveRoomState.room!!.hostName,
                            onRestore = { viewModel.restoreVoiceRoom() },
                            onLeave = { viewModel.leaveVoiceRoom() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // Full-Screen Live Voice Party Room Overlay
        AnimatedVisibility(
            visible = liveRoomState.room != null && !liveRoomState.isMinimized,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            LiveVoiceRoomScreen(
                session = liveRoomState,
                currentUser = user,
                audioState = audioState,
                levelConfigs = roomLevelConfigs,
                contributionSummary = activeRoomContribution,
                contributors = activeRoomContributors,
                giftLedger = activeRoomGiftLedger,
                activityLogs = activeRoomActivityLogs,
                onRefreshMicPermission = { viewModel.refreshMicPermission() },
                onMinimize = { viewModel.minimizeVoiceRoom() },
                onLeave = { viewModel.leaveVoiceRoom() },
                onToggleFavorite = { viewModel.toggleFavoriteRoom() },
                onToggleMute = { viewModel.toggleMicMute() },
                onTapSeat = { idx -> viewModel.tapSeat(idx) },
                onSendChat = { txt, isEmoji -> viewModel.sendRoomChat(txt, isEmoji) },
                onSendGift = { gift, combo, targets, label ->
                    viewModel.sendVirtualGiftInRoom(gift, combo, targets, label)
                },
                onAcceptMicRequest = { req -> viewModel.acceptMicRequest(req) },
                onRejectMicRequest = { uid -> viewModel.rejectMicRequest(uid) },
                onToggleSeatLock = { idx -> viewModel.toggleSeatLock(idx) },
                onToggleMicLock = { idx -> viewModel.toggleSeatMicLock(idx) },
                onToggleOccupantMute = { idx -> viewModel.toggleOccupantMuteByAdmin(idx) },
                onReserveSeat = { idx, name -> viewModel.reserveSeat(idx, name) },
                onAssignUserToSeat = { idx, uid, name, vip -> viewModel.assignUserToSeat(idx, uid, name, vip) },
                onToggleCoHost = { idx -> viewModel.toggleSeatCoHost(idx) },
                onRemoveFromSeat = { idx -> viewModel.removeUserFromSeat(idx) },
                onBanUser = { uid, name -> viewModel.banUserFromRoom(uid, name) },
                onStartMoveSeat = { idx -> viewModel.startMoveSeat(idx) },
                onTransferHost = { idx -> viewModel.transferHostToSeat(idx) },
                onAssignMemberRole = { uid, name, role -> viewModel.assignMemberRole(uid, name, role) },
                onAssignPartner = { uid, name -> viewModel.assignPartner(uid, name) },
                onRemovePartner = { viewModel.removePartner() },
                onChangeBackdropTheme = { theme -> viewModel.updateRoomBackdropTheme(theme) },
                onPublishAnnouncement = { cat, title, content -> viewModel.publishRoomAnnouncement(cat, title, content) },
                onUpdateAppearance = { t, th, dec, musicOnly -> viewModel.updateRoomAppearance(t, th, dec, musicOnly) },
                onUpdateAdminLevelConfig = { seats, pts, diamonds -> viewModel.updateAdminLevelSeatConfig(seats, pts, diamonds) },
                onStartPk = { mode, opp -> viewModel.startPkBattle(mode, opp) },
                onEndPk = { viewModel.endPkBattle() },
                onToggleMusic = { viewModel.toggleRoomMusic() },
                onSelectOrRequestTrack = { trk -> viewModel.selectOrRequestTrack(trk) },
                onApproveSongRequest = { item -> viewModel.approveSongRequest(item) },
                onReportRoomOrUser = { type, name, reason -> viewModel.submitReport(type, name, reason) },
                onBlockUser = { uid, name -> viewModel.toggleBlockUser(uid, name) },
                onOpenWallet = { activeSheet = ActiveSheet.WALLET },
                onInviteFriendsToast = { msg -> viewModel.showToast(msg) },
                onDismissLevelUp = { viewModel.dismissLevelUpBanner() }
            )
        }
    }

    // Ecosystem Modal Sheets
    when (activeSheet) {
        ActiveSheet.WALLET -> WalletSheet(
            user = user,
            transactions = transactions,
            onRecharge = { pkg -> viewModel.rechargeCoins(pkg) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.VIP -> VipMembershipSheet(
            user = user,
            onPurchaseVip = { lvl, cost, name -> viewModel.purchaseVip(lvl, cost, name) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.AVATAR_EDIT -> AvatarAndProfileEditSheet(
            user = user,
            onSave = { nick, bio, country, lang, showG, showA, cfg ->
                viewModel.updateProfileAndAvatar(nick, bio, country, lang, showG, showA, null, cfg)
            },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.FAMILIES -> FamiliesSheet(
            families = families,
            onToggleJoin = { id -> viewModel.toggleFamilyMembership(id) },
            onCreateFamily = { name, intro, emoji -> viewModel.createFamily(name, intro, emoji) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.EVENTS_RANKINGS -> EventsAndRankingsSheet(
            events = events,
            onToggleRegisterEvent = { id -> viewModel.toggleEventRegistration(id) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.NOTIFICATIONS -> NotificationsSheet(
            notifications = notifications,
            settings = settings,
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onUpdatePrefs = { g, r, p, m -> viewModel.updateNotificationPrefs(g, r, p, m) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.ADMIN_SAFETY -> AdminAndSafetySheet(
            user = user,
            roomsCount = rooms.size,
            reports = reports,
            auditLogs = auditLogs,
            onResolveReport = { rep, st -> viewModel.resolveReport(rep, st) },
            onAdminGrantCoins = { amt, reason -> viewModel.adminGrantCoins(amt, reason) },
            onDismiss = { activeSheet = ActiveSheet.NONE }
        )
        ActiveSheet.NONE -> {}
    }
}

@Composable
private fun MinimizedRoomBanner(
    title: String,
    hostName: String,
    onRestore: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onRestore),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    LiveVoiceBars(isActive = true, rmsLevel = 0.7f)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Live with $hostName • Tap to return",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryPurple
                    )
                }
            }
            IconButton(onClick = onLeave) {
                Icon(Icons.Rounded.Close, "Exit voice room", tint = RoseNeon)
            }
        }
    }
}
