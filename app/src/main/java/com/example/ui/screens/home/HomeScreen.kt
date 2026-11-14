package com.example.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.DailyTaskItem
import com.example.data.model.EventItem
import com.example.data.model.GameId
import com.example.data.model.GameMatchRecord
import com.example.data.model.UserProfile
import com.example.data.model.VoiceRoom
import com.example.data.repository.WefunSeedData
import com.example.ui.home.WefunHomeGamesScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun HomeScreen(
    user: UserProfile = defaultPreviewUser(),
    rooms: List<VoiceRoom> = emptyList(),
    events: List<EventItem> = WefunSeedData.events,
    matches: List<GameMatchRecord> = emptyList(),
    dailyTasks: List<DailyTaskItem> = WefunSeedData.dailyTasks,
    followedUserIds: Set<String> = emptySet(),
    initialGameToLaunch: GameId? = null,
    onClearInitialGame: () -> Unit = {},
    onOpenWallet: () -> Unit = {},
    onOpenEventsRankings: () -> Unit = {},
    onOpenRoom: (VoiceRoom) -> Unit = {},
    onOpenGameRooms: () -> Unit = {},
    onOpenProfileTab: () -> Unit = {},
    onOpenChatWithPeer: (peerId: String, peerName: String) -> Unit = { _, _ -> },
    onToggleFollow: (String) -> Unit = {},
    onClaimDailyTask: (String) -> Unit = {},
    onRecordMatchResult: (gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) -> Unit = { _, _, _, _, _ -> },
    onInviteFriendsToast: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        WefunHomeGamesScreen(
            user = user,
            rooms = rooms,
            events = events,
            matches = matches,
            dailyTasks = dailyTasks,
            followedUserIds = followedUserIds,
            initialGameToLaunch = initialGameToLaunch,
            onClearInitialGame = onClearInitialGame,
            onOpenWallet = onOpenWallet,
            onOpenEventsRankings = onOpenEventsRankings,
            onOpenRoom = onOpenRoom,
            onOpenGameRooms = onOpenGameRooms,
            onOpenProfileTab = onOpenProfileTab,
            onOpenChatWithPeer = onOpenChatWithPeer,
            onToggleFollow = onToggleFollow,
            onClaimDailyTask = onClaimDailyTask,
            onRecordMatchResult = onRecordMatchResult,
            onInviteFriendsToast = onInviteFriendsToast,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun defaultPreviewUser() = UserProfile(
    uid = "usr_nova_01",
    wefunId = "WF-804921",
    username = "ariavance",
    nickname = "Aria Vance",
    bio = "Music host, late-night storyteller & Ludo strategist."
)
