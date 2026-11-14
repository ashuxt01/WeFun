package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.local.WefunAppSettings
import com.example.data.model.ChatThread
import com.example.data.model.DailyTaskItem
import com.example.data.model.DirectMessage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameId
import com.example.data.model.GameMatchRecord
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.repository.WefunSeedData
import com.example.ui.screens.chats.ChatsScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.party.PartyScreen
import com.example.ui.screens.profile.ProfileScreen

sealed class WefunDestination(
    val route: String,
    val label: String,
    val title: String,
) {
    data object Home : WefunDestination(route = "home", label = "WeFun", title = "WeFun")
    data object Party : WefunDestination(route = "party", label = "Voice Room", title = "Voice Room")
    data object Chats : WefunDestination(route = "chats", label = "Chats", title = "Chats")
    data object Discover : WefunDestination(route = "discover", label = "Discover", title = "Discover")
    data object Profile : WefunDestination(route = "profile", label = "Me", title = "Me")

    companion object {
        val entries: List<WefunDestination>
            get() = listOf(Home, Party, Chats, Discover, Profile)

        fun fromRoute(route: String?): WefunDestination =
            entries.firstOrNull { it.route == route } ?: Home
    }
}

fun NavHostController.navigateToWefunDestination(destination: WefunDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    user: UserProfile = UserProfile(
        uid = "usr_nova_01",
        wefunId = "WF-804921",
        username = "ariavance",
        nickname = "Aria Vance",
        bio = "Music host, late-night storyteller & Ludo strategist."
    ),
    settings: WefunAppSettings = WefunAppSettings(),
    rooms: List<VoiceRoom> = emptyList(),
    events: List<EventItem> = WefunSeedData.events,
    gameMatches: List<GameMatchRecord> = emptyList(),
    dailyTasks: List<DailyTaskItem> = WefunSeedData.dailyTasks,
    posts: List<SocialPost> = emptyList(),
    families: List<FamilyInfo> = WefunSeedData.families,
    threads: List<ChatThread> = WefunSeedData.initialThreads,
    activeThreadId: String? = null,
    activeMessages: List<DirectMessage> = emptyList(),
    followedUserIds: Set<String> = emptySet(),
    pendingGameId: GameId? = null,
    initialVoiceRoomCategory: RoomCategory = RoomCategory.ALL,
    openCreateRoomOnVoiceTab: Boolean = false,
    onClearPendingGame: () -> Unit = {},
    onOpenWallet: () -> Unit = {},
    onOpenEventsRankings: () -> Unit = {},
    onOpenFamilies: () -> Unit = {},
    onOpenVip: () -> Unit = {},
    onOpenAvatarEditor: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenAdminPanel: () -> Unit = {},
    onOpenRoom: (VoiceRoom) -> Unit = {},
    onCreateRoom: (String, String, RoomCategory, Int, String?, RoomThemeStyle) -> Unit = { _, _, _, _, _, _ -> },
    onDismissInitialCreateRoom: () -> Unit = {},
    onOpenGameRooms: () -> Unit = {},
    onStartOrOpenChat: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onOpenThread: (String?) -> Unit = {},
    onSendDirectMessage: (String, String, VirtualGift?, Int?) -> Unit = { _, _, _, _ -> },
    onDeleteMessage: (String) -> Unit = {},
    onReactMessage: (DirectMessage, String) -> Unit = { _, _ -> },
    onToggleMuteThread: (String) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onDeclineRequest: (String) -> Unit = {},
    onBlockUser: (String, String) -> Unit = { _, _ -> },
    onReportTarget: (String, String, String) -> Unit = { _, _, _ -> },
    onToggleFollow: (String) -> Unit = {},
    onClaimDailyTask: (String) -> Unit = {},
    onRecordMatchResult: (String, String, Int, Long, Long) -> Unit = { _, _, _, _, _ -> },
    onCreatePost: (String, String, String, String?) -> Unit = { _, _, _, _ -> },
    onToggleLikePost: (SocialPost) -> Unit = {},
    onCommentPost: (SocialPost) -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleParentalControl: (Boolean) -> Unit = {},
    onSelectLanguage: (String) -> Unit = {},
    onPickProfilePhoto: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = WefunDestination.Home.route,
        modifier = modifier
    ) {
        composable(WefunDestination.Home.route) {
            HomeScreen(
                user = user,
                rooms = rooms,
                events = events,
                matches = gameMatches,
                dailyTasks = dailyTasks,
                followedUserIds = followedUserIds,
                initialGameToLaunch = pendingGameId,
                onClearInitialGame = onClearPendingGame,
                onOpenWallet = onOpenWallet,
                onOpenEventsRankings = onOpenEventsRankings,
                onOpenRoom = onOpenRoom,
                onOpenGameRooms = onOpenGameRooms,
                onOpenProfileTab = { navController.navigateToWefunDestination(WefunDestination.Profile) },
                onOpenChatWithPeer = { uid, name ->
                    onStartOrOpenChat(uid, name, false)
                    navController.navigateToWefunDestination(WefunDestination.Chats)
                },
                onToggleFollow = onToggleFollow,
                onClaimDailyTask = onClaimDailyTask,
                onRecordMatchResult = onRecordMatchResult,
                onInviteFriendsToast = onShowToast
            )
        }

        composable(WefunDestination.Party.route) {
            PartyScreen(
                rooms = rooms,
                onJoinRoom = onOpenRoom,
                onCreateRoom = onCreateRoom,
                onOpenEventsRankings = onOpenEventsRankings,
                initialCategoryFilter = initialVoiceRoomCategory,
                showCreateDialogInitially = openCreateRoomOnVoiceTab,
                onDismissInitialCreate = onDismissInitialCreateRoom
            )
        }

        composable(WefunDestination.Chats.route) {
            ChatsScreen(
                currentUser = user,
                threads = threads,
                activeThreadId = activeThreadId,
                activeMessages = activeMessages,
                blockedUserIds = settings.blockedUserIds,
                onOpenThread = onOpenThread,
                onStartOrOpenChat = onStartOrOpenChat,
                onSendDirectMessage = onSendDirectMessage,
                onDeleteMessage = onDeleteMessage,
                onReactMessage = onReactMessage,
                onToggleMuteThread = onToggleMuteThread,
                onMarkAllRead = onMarkAllRead,
                onAcceptRequest = onAcceptRequest,
                onDeclineRequest = onDeclineRequest,
                onBlockUser = onBlockUser,
                onReportTarget = onReportTarget
            )
        }

        composable(WefunDestination.Discover.route) {
            DiscoverScreen(
                posts = posts,
                families = families,
                events = events,
                followedUserIds = followedUserIds,
                onOpenFamilies = onOpenFamilies,
                onOpenEventsRankings = onOpenEventsRankings,
                onToggleFollow = onToggleFollow,
                onChatWithUser = { uid, name ->
                    onStartOrOpenChat(uid, name, false)
                    navController.navigateToWefunDestination(WefunDestination.Chats)
                },
                onCreatePost = onCreatePost,
                onToggleLikePost = onToggleLikePost,
                onCommentPost = onCommentPost,
                onReportPost = { post ->
                    onReportTarget("Post", post.authorName, "Reported from Discover")
                }
            )
        }

        composable(WefunDestination.Profile.route) {
            ProfileScreen(
                user = user,
                settings = settings,
                posts = posts,
                followedUserIds = followedUserIds,
                onToggleDarkMode = onToggleDarkMode,
                onToggleParentalControl = onToggleParentalControl,
                onSelectLanguage = onSelectLanguage,
                onToggleFollow = onToggleFollow,
                onPickProfilePhoto = onPickProfilePhoto,
                onOpenAvatarEditor = onOpenAvatarEditor,
                onOpenWallet = onOpenWallet,
                onOpenVip = onOpenVip,
                onOpenNotifications = onOpenNotifications,
                onOpenAdminPanel = onOpenAdminPanel,
                onSignOut = onSignOut,
                onDeleteAccount = onDeleteAccount,
                onShowToast = onShowToast
            )
        }
    }
}
