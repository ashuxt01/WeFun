package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class MainTab(val route: String, val label: String) {
    WEFUN("wefun", "WEFUN"),
    VOICE_ROOM("voice_room", "VOICE ROOM"),
    CHATS("chats", "CHATS"),
    DISCOVER("discover", "DISCOVER"),
    ME("me", "ME"),
}

enum class AuthMethod(val label: String) {
    PHONE_OTP("Phone + OTP"),
    GOOGLE("Google"),
    EMAIL("Email"),
}

enum class RoomCategory(val label: String) {
    ALL("All"),
    PUBLIC("Public"),
    PRIVATE("Private"),
    PASSWORD("Locked"),
    FAMILY("Family"),
    MUSIC("Music"),
    VIDEO("Video"),
    DATING("Social"),
    GAME("Games"),
    OFFICIAL("Official"),
    PK("PK Battle"),
}

enum class RoomThemeStyle(val label: String, val primaryHex: Long, val secondaryHex: Long) {
    GALAXY("Cosmic Lounge", 0xFF8B5CF6, 0xFF38BDF8),
    ROYAL_GOLD("Royal Palace", 0xFFFBBF24, 0xFF9333EA),
    CYBER_NEON("Cyber Club", 0xFF06B6D4, 0xFFF43F5E),
    SAKURA_NIGHT("Starlight Garden", 0xFFEC4899, 0xFF6366F1),
}

enum class VoiceRoomBackdropTheme(
    val label: String,
    val topHex: Long,
    val midHex: Long,
    val bottomHex: Long,
    val isDarkStage: Boolean,
) {
    ROYAL_BLUE("Starlight Blue", 0xFF0A2463, 0xFF0E3484, 0xFF13409E, true),
    MIDNIGHT_CHARCOAL("Midnight Night", 0xFF17151F, 0xFF121212, 0xFF0E0D12, true),
    AURORA_VIOLET("Aurora Violet", 0xFF2B1055, 0xFF43187A, 0xFF1D0D3B, true),
    ROSE_VELVET("Rose Velvet", 0xFF3E1032, 0xFF5D1847, 0xFF26091E, true),
    PEARL_LIGHT("Pearl Daylight", 0xFFF3EEFF, 0xFFFFFFFF, 0xFFF7F7FB, false),
}

enum class GiftCategory(val label: String) {
    LOVE("Love"),
    ROMANTIC("Romantic"),
    ROYAL("Royal"),
    LUXURY("Luxury"),
    ANIMALS("Animals"),
    SUPERCARS("Supercars"),
    PRIVATE_JETS("Private Jets"),
    GOLDEN_LIONS("Golden Lions"),
    DRAGONS("Dragons"),
    FANTASY("Fantasy"),
    FESTIVAL("Festival"),
    PREMIUM("Premium"),
    EXCLUSIVE("Limited"),
}

enum class TransactionType(val label: String) {
    RECHARGE("Coin Recharge"),
    GIFT_SENT("Gift Sent"),
    GIFT_EARNED("Gift Earnings"),
    DAILY_CHECKIN("Daily Check-In"),
    GAME_REWARD("Game Reward"),
    VIP_PURCHASE("VIP Upgrade"),
    REFUND("Refund"),
}

enum class CurrencyKind(val label: String) {
    COINS("Coins"),
    DIAMONDS("Diamonds"),
    BONUS("Bonus"),
    XP("XP"),
}

enum class AdminRole(val title: String, val permissionsSummary: String) {
    SUPER_ADMIN("Super Admin", "Full platform config, finances, bans, roles"),
    ADMINISTRATOR("Administrator", "Users, rooms, banners, gifts, system logs"),
    MODERATOR("Moderator", "Reports queue, mutes, suspensions, content review"),
    FINANCE_MANAGER("Finance Manager", "Transactions, commissions, settlements, refunds"),
    EVENT_MANAGER("Event Manager", "Tournaments, schedules, winners, rewards"),
    AGENCY_MANAGER("Agency Manager", "Host verification, agency onboarding, quotas"),
}

data class AvatarConfig(
    val faceStyle: String = "Radiant",
    val hairStyle: String = "Neon Braids",
    val outfitStyle: String = "Cyber Jacket",
    val accessory: String = "Holo Headphones",
    val backgroundAura: String = "Cosmic Violet",
    val frameName: String = "Default Ring",
)

data class UserProfile(
    val uid: String,
    val wefunId: String,
    val username: String,
    val nickname: String,
    val bio: String,
    val phone: String = "",
    val email: String = "",
    val dob: String = "2000-05-14",
    val isAgeVerified: Boolean = true,
    val gender: String = "Prefer not to say",
    val showGender: Boolean = true,
    val showAge: Boolean = true,
    val country: String = "India",
    val language: String = "English",
    val photoUri: String? = null,
    val accentColorHex: Long = 0xFF8B5CF6,
    val avatarConfig: AvatarConfig = AvatarConfig(),
    val vipLevel: Int = 2,
    val userLevel: Int = 14,
    val xp: Long = 3450,
    val nextLevelXp: Long = 5000,
    val coins: Long = 2800,
    val diamonds: Long = 640,
    val bonusPoints: Long = 320,
    val followersCount: Int = 1280,
    val followingCount: Int = 184,
    val friendsCount: Int = 72,
    val giftsReceivedCount: Int = 419,
    val giftsSentCount: Int = 285,
    val visitorsCount: Int = 94,
    val familyId: String? = "fam_celestial",
    val isHostVerified: Boolean = true,
    val hostLevel: Int = 6,
    val agencyName: String = "Nova Star Agency",
    val lastCheckInDay: Int = 2,
    val isCheckedInToday: Boolean = false,
)

data class VoiceRoom(
    val id: String,
    val title: String,
    val description: String,
    val category: RoomCategory,
    val password: String? = null,
    val hostId: String,
    val hostName: String,
    val hostVip: Int = 3,
    val maxSeats: Int = 8,
    val roomLevel: Int = 1,
    val totalContribution: Long = 0L,
    val isFavorited: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis() - 86400000L * 14,
    val decorationStyle: String = "Charcoal Gold Standard",
    val musicAdminsOnly: Boolean = false,
    val themeStyle: RoomThemeStyle = RoomThemeStyle.GALAXY,
    val announcement: String = "Welcome to WEFUN! Keep vibes positive & respectful.",
    val popularity: Long = 18400,
    val onlineCount: Int = 64,
    val language: String = "English",
    val familyName: String? = null,
    val isPkActive: Boolean = false,
    val pkMode: String = "1v1 PK",
    val pkOpponentName: String = "DJ Rhea • Pulse",
    val pkScoreLeft: Long = 1240,
    val pkScoreRight: Long = 980,
    val pkSecondsLeft: Int = 180,
    val isMusicPlaying: Boolean = false,
    val currentTrack: String = "Midnight Neon (Licensed Loop)",
    val backdropTheme: VoiceRoomBackdropTheme = VoiceRoomBackdropTheme.ROYAL_BLUE,
)

data class RoomSeat(
    val seatIndex: Int,
    val occupantId: String? = null,
    val occupantName: String? = null,
    val occupantInitials: String = "",
    val accentColorHex: Long = 0xFF6200EE,
    val isHost: Boolean = false,
    val isCoHost: Boolean = false,
    val isMuted: Boolean = false,
    val isMicLocked: Boolean = false,
    val isSeatLocked: Boolean = false,
    val isSpeaking: Boolean = false,
    val rmsLevel: Float = 0f,
    val vipLevel: Int = 0,
    val userLevel: Int = 12,
    val role: RoomRole = RoomRole.NORMAL_MEMBER,
    val charmCount: Long = 0,
    val reservedForUserId: String? = null,
    val reservedForName: String? = null,
)

enum class RoomMessageType { TEXT, EMOJI, GIFT, SYSTEM, ENTRY }

data class RoomChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderVip: Int,
    val text: String,
    val type: RoomMessageType = RoomMessageType.TEXT,
    val timestampLabel: String = "now",
)

data class MicRequest(
    val userId: String,
    val userName: String,
    val vipLevel: Int,
    val userLevel: Int,
)

data class VirtualGift(
    val id: String,
    val name: String,
    val category: GiftCategory,
    val coinPrice: Long,
    val diamondValue: Long,
    val durationMs: Long = 2600L,
    val emoji: String,
    val isFullScreen: Boolean = false,
    val isLimited: Boolean = false,
    val usesDragonArt: Boolean = false,
    val accentHex: Long = 0xFFFBBF24,
)

data class ActiveGiftEffect(
    val id: String,
    val gift: VirtualGift,
    val senderName: String,
    val receiverName: String,
    val comboCount: Int,
    val totalCoins: Long,
)

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val currency: CurrencyKind,
    val amount: Long,
    val isCredit: Boolean,
    val description: String,
    val referenceId: String,
    val timestamp: Long,
)

data class RechargePackage(
    val id: String,
    val coins: Long,
    val bonusCoins: Long,
    val priceDisplay: String,
    val badge: String? = null,
)

data class VipTier(
    val level: Int,
    val name: String,
    val monthlyCoins: Long,
    val accentHex: Long,
    val frameTitle: String,
    val entryEffect: String,
    val chatBubble: String,
    val privileges: List<String>,
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorVip: Int,
    val content: String,
    val hashtag: String,
    val mediaType: String = "TEXT", // TEXT, IMAGE, SHORT_VIDEO
    val imageUri: String? = null,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val timestamp: Long,
)

data class ChatThread(
    val id: String,
    val peerId: String,
    val peerName: String,
    val peerInitials: String,
    val accentHex: Long,
    val isGroup: Boolean = false,
    val isRequest: Boolean = false,
    val isOnline: Boolean = true,
    val isMuted: Boolean = false,
    val vipLevel: Int = 0,
    val isOfficial: Boolean = false,
    val timeLabel: String = "11:23 am",
    val lastSeenLabel: String = "Online",
    val unreadCount: Int = 0,
    val lastMessagePreview: String,
)

data class DirectMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val giftName: String? = null,
    val voiceSeconds: Int? = null,
    val reaction: String? = null,
    val isRead: Boolean = true,
    val timestamp: Long,
)

enum class GameId {
    LUDO, JACKAROO, WHOS_THE_SPY, SPACE_WEREWOLF, DRAW_AND_GUESS, QUIZ_BATTLE,
    TRUTH_OR_DARE, WORD_CHALLENGE, MEMORY_CHALLENGE, NUMBER_GUESS, FAST_TYPING,
    SINGING_COMP, MIC_GRAB_KARAOKE, TEAM_STRATEGY, HIDE_AND_SEEK, PETROL_PUMP_TYCOON,
    PAINT_TO_HIDE, TRICKSTERS_CAFE, OH_MY_CARD
}

data class PartyGame(
    val id: GameId,
    val title: String,
    val category: String,
    val playersOnline: Int,
    val entryCoins: Long,
    val rewardCoins: Long,
    val maxPlayers: Int,
    val rules: String,
    val emoji: String,
    val accentHex: Long,
    val secondaryAccentHex: Long = 0xFF3700B3,
    val isNew: Boolean = false,
    val isFeaturedBanner: Boolean = false,
)

data class DailyTaskItem(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Long,
    val rewardXp: Long,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
)

data class GameMatchRecord(
    val id: String,
    val gameTitle: String,
    val outcome: String,
    val score: Int,
    val coinsWon: Long,
    val xpEarned: Long,
    val timestamp: Long,
)

data class FamilyInfo(
    val id: String,
    val name: String,
    val badgeEmoji: String,
    val intro: String,
    val leaderName: String,
    val memberCount: Int,
    val level: Int,
    val contributionPoints: Long,
    val rank: Int,
    val isJoined: Boolean = false,
)

data class EventItem(
    val id: String,
    val title: String,
    val category: String,
    val schedule: String,
    val roomId: String,
    val rewardSummary: String,
    val rules: String,
    val participantCount: Int,
    val isRegistered: Boolean = false,
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val subtitle: String,
    val score: Long,
    val vipLevel: Int,
    val accentHex: Long,
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val progressText: String,
)

data class NotificationItem(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val timeLabel: String,
    val isRead: Boolean = false,
)

data class SafetyReport(
    val id: String,
    val targetType: String, // User, Room, Message, Post
    val targetName: String,
    val reason: String,
    val status: String, // Pending, Reviewed, Resolved
    val timestamp: Long,
)

data class HostStats(
    val totalHoursThisMonth: Float,
    val verifiedStatus: String,
    val nextSchedule: String,
    val monthlyDiamonds: Long,
    val avgListeners: Int,
    val agencyName: String,
    val agencyCommissionPct: Int,
    val settlementStatus: String,
)

data class AdminDashboardSnapshot(
    val activeUsers: Int,
    val liveRooms: Int,
    val dailyCoinsVolume: Long,
    val pendingReports: Int,
    val activeEvents: Int,
    val registeredAgencies: Int,
    val auditLogs: List<String>,
)

fun Color.toHexLong(): Long {
    val a = (alpha * 255).toLong() and 0xFF
    val r = (red * 255).toLong() and 0xFF
    val g = (green * 255).toLong() and 0xFF
    val b = (blue * 255).toLong() and 0xFF
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}
