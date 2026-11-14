package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.audio.AudioEngineState
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomOwnerInfo
import com.example.data.model.RoomPartnerInfo
import com.example.data.model.RoomRole
import com.example.data.model.RoomSeat
import com.example.data.model.UserProfile
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.repository.VoiceRoomSeedData
import com.example.ui.auth.WefunSplashScreen
import com.example.ui.navigation.AppNavigation
import com.example.ui.navigation.BottomNavigationBar
import com.example.ui.navigation.WefunDestination
import com.example.ui.navigation.navigateToWefunDestination
import com.example.ui.rooms.LiveVoiceRoomScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.LiveRoomSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunNavigationAndThemeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `wefun theme defaults to exact premium light palette across screens`() {
        var observedBackground = SecondaryBackground
        var observedSurface = PrimaryBackground
        var observedCard = SecondaryBackground
        var observedPrimary = PinkAccent
        var observedPink = PrimaryPurple
        var observedGold = PrimaryPurple
        var observedText = SecondaryText
        var observedSecondaryText = PrimaryText
        var observedBorder = PrimaryPurple
        var observedIsDark = true

        composeRule.setContent {
            WEFUNTheme {
                observedBackground = MaterialTheme.colorScheme.background
                observedSurface = MaterialTheme.colorScheme.surface
                observedCard = MaterialTheme.colorScheme.surfaceVariant
                observedPrimary = MaterialTheme.colorScheme.primary
                observedPink = MaterialTheme.colorScheme.secondary
                observedGold = MaterialTheme.colorScheme.tertiary
                observedText = MaterialTheme.colorScheme.onBackground
                observedSecondaryText = MaterialTheme.colorScheme.onSurfaceVariant
                observedBorder = MaterialTheme.colorScheme.outline
                observedIsDark = LocalWefunColors.current.isDark
            }
        }
        composeRule.waitForIdle()

        assertEquals(PrimaryBackground, observedBackground)
        assertEquals(SecondaryBackground, observedSurface)
        assertEquals(CardBackground, observedCard)
        assertEquals(PrimaryPurple, observedPrimary)
        assertEquals(PinkAccent, observedPink)
        assertEquals(PremiumGold, observedGold)
        assertEquals(PrimaryText, observedText)
        assertEquals(SecondaryText, observedSecondaryText)
        assertEquals(BorderColor, observedBorder)
        assertFalse(observedIsDark)
    }

    @Test
    fun `splash screen renders official wefun logo and tagline on light theme`() {
        composeRule.setContent {
            WEFUNTheme {
                WefunSplashScreen(onFinished = {})
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("wefun_splash_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("splash_brand_logo").assertIsDisplayed()
        composeRule.onNodeWithText("WEFUN").assertIsDisplayed()
        composeRule.onNodeWithText("Connect. Play. Express. Belong.").assertIsDisplayed()
    }

    @Test
    fun `bottom navigation switches all five tabs updates selected state and supports back navigation`() {
        lateinit var currentRouteRef: () -> String?
        lateinit var backStackCountRef: () -> Int

        composeRule.setContent {
            WEFUNTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: WefunDestination.Home.route
                currentRouteRef = { navController.currentBackStackEntry?.destination?.route }
                backStackCountRef = { navController.currentBackStack.value.size }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onSelectDestination = { destination ->
                                navController.navigateToWefunDestination(destination)
                            },
                            unreadChatsCount = 3,
                            showVipGoldIndicator = true,
                        )
                    }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // 1. Initial route must be Home (WEFUN)
        assertEquals(WefunDestination.Home.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_home").assertIsSelected()
        composeRule.onNodeWithTag("nav_tab_party").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_chats").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_discover").assertIsNotSelected()
        composeRule.onNodeWithTag("nav_tab_profile").assertIsNotSelected()
        composeRule.onNodeWithTag("home_avatar_button").assertIsDisplayed()
        composeRule.onNodeWithTag("home_brand_logo").assertIsDisplayed()

        // 2. Switch to Party (Voice Room)
        composeRule.onNodeWithTag("nav_tab_party").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Party.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_party").assertIsSelected()
        composeRule.onNodeWithTag("nav_tab_home").assertIsNotSelected()
        composeRule.onNodeWithTag("fab_create_room").assertIsDisplayed()

        // 3. Switch to Chats
        composeRule.onNodeWithTag("nav_tab_chats").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Chats.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_chats").assertIsSelected()
        composeRule.onNodeWithTag("chats_new_chat_button").assertIsDisplayed()

        // 4. Switch to Discover
        composeRule.onNodeWithTag("nav_tab_discover").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Discover.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_discover").assertIsSelected()
        composeRule.onNodeWithTag("create_post_button").assertIsDisplayed()

        // 5. Switch to Profile (Me)
        composeRule.onNodeWithTag("nav_tab_profile").performClick()
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Profile.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_profile").assertIsSelected()
        composeRule.onNodeWithText("Aria Vance").assertIsDisplayed()

        // 6. Tapping Profile again must NOT create duplicate entries on back stack
        val stackSizeBeforeDuplicateTap = backStackCountRef()
        composeRule.onNodeWithTag("nav_tab_profile").performClick()
        composeRule.waitForIdle()
        assertEquals(stackSizeBeforeDuplicateTap, backStackCountRef())

        // 7. Android back navigation from Profile returns to Home (WEFUN)
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
        assertEquals(WefunDestination.Home.route, currentRouteRef())
        composeRule.onNodeWithTag("nav_tab_home").assertIsSelected()
    }

    @Test
    fun `phase 2 voice room renders top nav, owner and partner thrones, and 8 seats at level 1`() {
        val sampleUser = UserProfile(
            uid = "user_aria",
            wefunId = "WF-804921",
            username = "ariavance",
            nickname = "Aria Vance",
            bio = "Host",
            vipLevel = 3,
            userLevel = 18
        )
        val sampleRoom = VoiceRoom(
            id = "320252",
            title = "DOMINATORS",
            description = "Voice party",
            category = RoomCategory.DATING,
            hostId = "user_aria",
            hostName = "Aria Vance",
            maxSeats = 8,
            roomLevel = 1,
            popularity = 24800,
            onlineCount = 42,
            backdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE
        )
        val session = LiveRoomSessionState(
            room = sampleRoom,
            ownerInfo = RoomOwnerInfo(
                roomId = sampleRoom.id,
                userId = "user_aria",
                displayName = "Aria Vance",
                initials = "AV",
                avatarColorHex = 0xFF7B2CFF,
                vipLevel = 3,
                userLevel = 40,
                charmCount = 1850
            ),
            partnerInfo = RoomPartnerInfo(
                roomId = sampleRoom.id,
                userId = "sp_partner",
                displayName = "Sanya Star",
                initials = "SS",
                vipLevel = 4,
                userLevel = 40,
                relationshipDays = 14,
                partnerSinceLabel = "14d Together"
            ),
            memberRoles = listOf(
                RoomMemberRoleEntry(sampleRoom.id, "user_aria", "Aria Vance", "AV", RoomRole.OWNER, 3, 40),
                RoomMemberRoleEntry(sampleRoom.id, "sp_partner", "Sanya Star", "SS", RoomRole.PARTNER, 4, 40),
                RoomMemberRoleEntry(sampleRoom.id, "sp_2", "Jishan Bhati", "JB", RoomRole.ROOM_ADMIN, 3, 21),
            ),
            currentUserRole = RoomRole.OWNER,
            seats = (0 until 8).map { idx ->
                if (idx == 4) {
                    RoomSeat(
                        seatIndex = idx,
                        occupantId = "sp_2",
                        occupantName = "Jishan Bhati",
                        occupantInitials = "JB",
                        role = RoomRole.ROOM_ADMIN,
                        userLevel = 21,
                        vipLevel = 3,
                        charmCount = 620
                    )
                } else {
                    RoomSeat(seatIndex = idx)
                }
            }
        )

        composeRule.setContent {
            WEFUNTheme {
                LiveVoiceRoomScreen(
                    session = session,
                    currentUser = sampleUser,
                    audioState = AudioEngineState(),
                    levelConfigs = VoiceRoomSeedData.defaultLevelConfigs,
                    contributionSummary = null,
                    contributors = emptyList(),
                    giftLedger = emptyList(),
                    activityLogs = emptyList(),
                    onRefreshMicPermission = {},
                    onMinimize = {},
                    onLeave = {},
                    onToggleFavorite = {},
                    onToggleMute = {},
                    onTapSeat = {},
                    onSendChat = { _, _ -> },
                    onSendGift = { _, _, _, _ -> },
                    onAcceptMicRequest = {},
                    onRejectMicRequest = {},
                    onToggleSeatLock = {},
                    onToggleMicLock = {},
                    onToggleOccupantMute = {},
                    onReserveSeat = { _, _ -> },
                    onAssignUserToSeat = { _, _, _, _ -> },
                    onToggleCoHost = {},
                    onRemoveFromSeat = {},
                    onBanUser = { _, _ -> },
                    onStartMoveSeat = {},
                    onTransferHost = {},
                    onAssignMemberRole = { _, _, _ -> },
                    onAssignPartner = { _, _ -> },
                    onRemovePartner = {},
                    onChangeBackdropTheme = {},
                    onPublishAnnouncement = { _, _, _ -> },
                    onUpdateAppearance = { _, _, _, _ -> },
                    onUpdateAdminLevelConfig = { _, _, _ -> },
                    onStartPk = { _, _ -> },
                    onEndPk = {},
                    onToggleMusic = {},
                    onSelectOrRequestTrack = {},
                    onApproveSongRequest = {},
                    onReportRoomOrUser = { _, _, _ -> },
                    onBlockUser = { _, _ -> },
                    onOpenWallet = {},
                    onInviteFriendsToast = {},
                    onDismissLevelUp = {},
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("room_top_level_badge").assertIsDisplayed()
        composeRule.onNodeWithTag("room_top_copy_id").assertIsDisplayed()
        composeRule.onNodeWithTag("top_nav_owner_profile").assertIsDisplayed()
        composeRule.onNodeWithTag("top_nav_coowner_profile").assertIsDisplayed()
        composeRule.onNodeWithTag("room_online_count_pill").assertIsDisplayed()
        composeRule.onNodeWithTag("room_owner_slot").assertIsDisplayed()
        composeRule.onNodeWithTag("room_partner_slot").assertIsDisplayed()
        composeRule.onNodeWithTag("room_music_pill").assertIsDisplayed()
        composeRule.onNodeWithTag("mic_seat_0").assertIsDisplayed()
        composeRule.onNodeWithTag("mic_seat_4").assertIsDisplayed()
        composeRule.onNodeWithTag("mic_seat_7").assertIsDisplayed()
        composeRule.onNodeWithText("No Songs").assertIsDisplayed()

        // Tap Partner throne to verify Remove/Change Partner UI opens cleanly
        composeRule.onNodeWithTag("room_partner_slot").performClick()
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(600)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("remove_partner_button").assertIsDisplayed()
        composeRule.onNodeWithText("Partner • 14d Together").assertIsDisplayed()
    }
}
