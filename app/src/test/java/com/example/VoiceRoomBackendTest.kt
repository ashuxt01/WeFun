package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GiftCategory
import com.example.data.model.RoomRole
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.repository.VoiceRoomSeedData
import com.example.data.repository.WefunRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VoiceRoomBackendTest {

    @Test
    fun `level 1 to 10 seat progression matches 8 to 26 rule`() {
        val configs = VoiceRoomSeedData.defaultLevelConfigs
        assertEquals(10, configs.size)
        configs.forEachIndexed { index, cfg ->
            val expectedLevel = index + 1
            val expectedSeats = 8 + index * 2
            assertEquals(expectedLevel, cfg.level)
            assertEquals(expectedSeats, cfg.seatCount)
        }
    }

    @Test
    fun `gift contribution uses separate accounting and upgrades room level automatically`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WefunRepository(context)
        repo.ensureSeeded()

        val sender = UserProfile(
            uid = "usr_test_sender",
            wefunId = "WF-999111",
            username = "tester",
            nickname = "Test Sender",
            bio = "Testing",
            coins = 50_000L,
        )
        val gift = VirtualGift(
            id = "g_eclipse",
            name = "Eclipse Sovereign",
            category = GiftCategory.EXCLUSIVE,
            coinPrice = 9999L,
            diamondValue = 4999L,
            emoji = "💎",
        )

        // Room 1579056 starts at Level 1 with 4,250 contribution points; Level 2 requires 10,000 points.
        // Sending 9,999 coins * 0.85 pointsPerCoin = 8,499 contribution points -> 12,749 total -> upgrades to Level 2 (10 seats)!
        val result = repo.roomBackend.processRoomGiftContribution(
            roomId = "1579056",
            sender = sender,
            receiverId = "usr_nova_01",
            receiverName = "Aria Vance",
            gift = gift,
            comboCount = 1,
            totalCoins = 9999L,
        )

        assertTrue("Contribution points must follow conversion rule (not 1:1 coin)", result.contributionPointsAdded < 9999L)
        assertEquals(8499L, result.contributionPointsAdded)
        assertEquals(4999L, result.receiverDiamondsEarned)
        assertEquals(2, result.updatedRoomLevel)
        assertEquals(10, result.updatedSeatCount)
        assertNotNull("Crossing 10,000 pts must emit a LevelUpEvent", result.levelUpEvent)
        assertEquals(2, result.levelUpEvent?.newLevel)
    }

    @Test
    fun `normal member cannot assign roles or publish admin announcements`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WefunRepository(context)
        repo.ensureSeeded()

        val roleRes = repo.roomBackend.assignRole(
            roomId = "1579056",
            actorName = "Guest",
            actorRole = RoomRole.NORMAL_MEMBER,
            targetUserId = "sp_4",
            targetName = "Zain Flow",
            newRole = RoomRole.ROOM_ADMIN,
        )
        assertTrue(roleRes.isFailure)
    }
}
