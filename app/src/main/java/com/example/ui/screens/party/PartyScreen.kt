package com.example.ui.screens.party

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.VoiceRoom
import com.example.ui.rooms.VoiceRoomsLobbyScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun PartyScreen(
    rooms: List<VoiceRoom> = emptyList(),
    onJoinRoom: (VoiceRoom) -> Unit = {},
    onCreateRoom: (
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
    ) -> Unit = { _, _, _, _, _, _ -> },
    onOpenEventsRankings: () -> Unit = {},
    initialCategoryFilter: RoomCategory = RoomCategory.ALL,
    showCreateDialogInitially: Boolean = false,
    onDismissInitialCreate: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        VoiceRoomsLobbyScreen(
            rooms = rooms,
            onJoinRoom = onJoinRoom,
            onCreateRoom = onCreateRoom,
            onOpenEventsRankings = onOpenEventsRankings,
            initialCategoryFilter = initialCategoryFilter,
            showCreateDialogInitially = showCreateDialogInitially,
            onDismissInitialCreate = onDismissInitialCreate
        )
    }
}
