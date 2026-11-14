package com.example.data.repository

import com.example.data.local.DirectMessageEntity
import com.example.data.local.GameMatchEntity
import com.example.data.local.SafetyReportEntity
import com.example.data.local.SocialPostEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.model.AchievementItem
import com.example.data.model.ChatThread
import com.example.data.model.DailyTaskItem
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameId
import com.example.data.model.GiftCategory
import com.example.data.model.HostStats
import com.example.data.model.LeaderboardEntry
import com.example.data.model.NotificationItem
import com.example.data.model.PartyGame
import com.example.data.model.RechargePackage
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.VipTier
import com.example.data.model.VirtualGift

object WefunSeedData {

    fun defaultUser(
        uid: String = "usr_nova_01",
        nickname: String = "Aria Vance",
        username: String = "ariavance",
        phone: String = "+91 98765 43210",
        email: String = "aria@wefun.live",
        country: String = "India",
        language: String = "English",
    ) = UserProfileEntity(
        uid = uid,
        wefunId = "WF-804921",
        username = username,
        nickname = nickname,
        bio = "Music host, late-night storyteller & Ludo strategist. Come grab a mic!",
        phone = phone,
        email = email,
        dob = "2000-05-14",
        isAgeVerified = true,
        gender = "Female",
        showGender = true,
        showAge = true,
        country = country,
        language = language,
        photoUri = null,
        accentColorHex = 0xFF8B5CF6,
        faceStyle = "Radiant",
        hairStyle = "Neon Braids",
        outfitStyle = "Cyber Jacket",
        accessory = "Holo Headphones",
        backgroundAura = "Cosmic Violet",
        frameName = "Astral Gold",
        vipLevel = 2,
        userLevel = 14,
        xp = 3450,
        nextLevelXp = 5000,
        coins = 2800,
        diamonds = 640,
        bonusPoints = 320,
        followersCount = 1280,
        followingCount = 184,
        friendsCount = 72,
        giftsReceivedCount = 419,
        giftsSentCount = 285,
        visitorsCount = 94,
        familyId = "fam_celestial",
        isHostVerified = true,
        hostLevel = 6,
        agencyName = "Nova Star Agency",
        lastCheckInDay = 2,
        isCheckedInToday = false,
    )

    val initialRooms = listOf(
        VoiceRoomEntity(
            id = "1579056",
            title = "💎❤️✨ Midnight Neon Music",
            description = "Level 1 standard 8-seat music room. Grab a golden seat & request tracks!",
            categoryName = RoomCategory.MUSIC.name,
            password = null,
            hostId = "usr_nova_01",
            hostName = "Aria Vance",
            hostVip = 5,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 4_250L,
            isFavorited = true,
            decorationStyle = "Charcoal Gold Standard",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Welcome to WEFUN! Keep vibes golden ✨ Request songs or hop on one of the 8 mic seats 🙏",
            popularity = 42800,
            onlineCount = 148,
            language = "English",
            familyName = "Celestial House",
        ),
        VoiceRoomEntity(
            id = "rm_102",
            title = "Royal PK Arena: Titans vs Phoenix",
            description = "Level 3 (12 seats) PK Arena — 75,000 / 100,000 toward Level 4!",
            categoryName = RoomCategory.PK.name,
            password = null,
            hostId = "host_kai",
            hostName = "Kai Storm",
            hostVip = 6,
            maxSeats = 12,
            roomLevel = 3,
            totalContribution = 75_000L,
            isFavorited = false,
            decorationStyle = "Gold Ribbon Crest",
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Double PK spotlight active! 25,000 points to unlock Level 4 (14 Seats)!",
            popularity = 68900,
            onlineCount = 312,
            language = "English",
            familyName = "Golden Crown",
        ),
        VoiceRoomEntity(
            id = "rm_101",
            title = "DJ Rhea Acoustic & Chill",
            description = "Level 2 cozy lounge with 10 microphone seats & acoustic requests.",
            categoryName = RoomCategory.MUSIC.name,
            password = null,
            hostId = "host_rhea",
            hostName = "DJ Rhea",
            hostVip = 5,
            maxSeats = 10,
            roomLevel = 2,
            totalContribution = 18_400L,
            decorationStyle = "Purple Pulse Ring",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Request your favorite track in the Music Queue! Respect all speakers.",
            popularity = 39200,
            onlineCount = 118,
            language = "English",
            familyName = "Velvet Pulse",
        ),
        VoiceRoomEntity(
            id = "rm_103",
            title = "Space Werewolf & Spy Pro Lobby",
            description = "12-seat voice deduction lobby. Mic required for voting rounds!",
            categoryName = RoomCategory.GAME.name,
            password = null,
            hostId = "host_milo",
            hostName = "Milo Taktik",
            hostVip = 3,
            maxSeats = 12,
            roomLevel = 3,
            totalContribution = 44_200L,
            decorationStyle = "Gold Ribbon Crest",
            themeName = RoomThemeStyle.CYBER_NEON.name,
            announcement = "No spoilers in text chat during night phase. Have fun!",
            popularity = 31500,
            onlineCount = 96,
            language = "Hindi",
            familyName = "Shadow Syndicate",
        ),
        VoiceRoomEntity(
            id = "rm_104",
            title = "Starlight Social & Icebreakers",
            description = "Level 1 friendly social room with 8 open golden mic seats.",
            categoryName = RoomCategory.DATING.name,
            password = null,
            hostId = "host_zara",
            hostName = "Zara Moon",
            hostVip = 4,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 6_800L,
            themeName = RoomThemeStyle.SAKURA_NIGHT.name,
            announcement = "Respectful vibes only. Zero tolerance for harassment.",
            popularity = 27400,
            onlineCount = 114,
            language = "English",
            familyName = "Velvet Pulse",
        ),
        VoiceRoomEntity(
            id = "rm_105",
            title = "Celestial Family Weekly Townhall",
            description = "Family war strategy, contribution badges & member rewards.",
            categoryName = RoomCategory.FAMILY.name,
            password = null,
            hostId = "usr_nova_01",
            hostName = "Aria Vance",
            hostVip = 2,
            maxSeats = 16,
            roomLevel = 5,
            totalContribution = 248_000L,
            decorationStyle = "Astral Halo Frame",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Family PK tournament starts Friday 9 PM IST!",
            popularity = 19200,
            onlineCount = 54,
            language = "English",
            familyName = "Celestial House",
        ),
        VoiceRoomEntity(
            id = "rm_106",
            title = "VIP Obsidian Sanctuary (Pass: 1234)",
            description = "Private acoustic jam & close friends circle.",
            categoryName = RoomCategory.PASSWORD.name,
            password = "1234",
            hostId = "host_leo",
            hostName = "Leo Beats",
            hostVip = 6,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 2_400L,
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Private room — ask host for passcode (Demo pass: 1234).",
            popularity = 12400,
            onlineCount = 18,
            language = "English",
            familyName = null,
        ),
        VoiceRoomEntity(
            id = "rm_107",
            title = "WEFUN Official Starlight Carnival",
            description = "Level 5 official platform showcase with verified hosts & coin drops!",
            categoryName = RoomCategory.OFFICIAL.name,
            password = null,
            hostId = "host_official",
            hostName = "WEFUN Official",
            hostVip = 6,
            maxSeats = 16,
            roomLevel = 5,
            totalContribution = 312_000L,
            decorationStyle = "Astral Halo Frame",
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Official Carnival Finale tonight! Win exclusive SVIP frames.",
            popularity = 91500,
            onlineCount = 540,
            language = "English",
            familyName = "WEFUN Creators",
        ),
        VoiceRoomEntity(
            id = "rm_108",
            title = "Neon Stage Live Video & DJ Mix",
            description = "Interactive stage visualizers, live DJ sets & video hangout!",
            categoryName = RoomCategory.VIDEO.name,
            password = null,
            hostId = "host_leo",
            hostName = "Leo Beats",
            hostVip = 6,
            maxSeats = 10,
            roomLevel = 2,
            totalContribution = 16_200L,
            decorationStyle = "Purple Pulse Ring",
            themeName = RoomThemeStyle.CYBER_NEON.name,
            announcement = "Turn up the bass! Video stage spotlight open for VIP performers.",
            popularity = 44100,
            onlineCount = 204,
            language = "English",
            familyName = "Velvet Pulse",
        ),
    )

    val virtualGifts = listOf(
        VirtualGift("g_heart", "Neon Heart", GiftCategory.LOVE, 10, 5, 1600, "💜", false, false, false, 0xFF8B5CF6),
        VirtualGift("g_rose", "Crystal Rose", GiftCategory.ROMANTIC, 29, 14, 1800, "🌹", false, false, false, 0xFFF43F5E),
        VirtualGift("g_kiss", "Starlight Kiss", GiftCategory.ROMANTIC, 50, 25, 2000, "💋", false, false, false, 0xFFEC4899),
        VirtualGift("g_teddy", "Cosmic Bear", GiftCategory.ANIMALS, 99, 49, 2200, "🧸", false, false, false, 0xFF38BDF8),
        VirtualGift("g_crown", "Royal Crown", GiftCategory.ROYAL, 199, 99, 2600, "👑", true, false, false, 0xFFFBBF24),
        VirtualGift("g_watch", "Tourbillon", GiftCategory.LUXURY, 299, 149, 2600, "⌚", true, false, false, 0xFF38BDF8),
        VirtualGift("g_lantern", "Festival Lantern", GiftCategory.FESTIVAL, 188, 94, 2400, "🏮", true, false, false, 0xFFF59E0B),
        VirtualGift("g_supercar", "Hyper Roadster", GiftCategory.SUPERCARS, 599, 299, 3200, "🏎️", true, false, false, 0xFFF43F5E),
        VirtualGift("g_lion", "Golden Lion", GiftCategory.GOLDEN_LIONS, 888, 444, 3400, "🦁", true, false, false, 0xFFFBBF24),
        VirtualGift("g_phoenix", "Astral Phoenix", GiftCategory.FANTASY, 1200, 600, 3600, "🔥", true, false, false, 0xFFA855F7),
        VirtualGift("g_jet", "Nebula Jet", GiftCategory.PRIVATE_JETS, 1888, 944, 3800, "🛩️", true, false, false, 0xFF38BDF8),
        VirtualGift("g_dragon", "Cosmic Dragon", GiftCategory.DRAGONS, 2500, 1250, 4200, "🐉", true, true, true, 0xFFFBBF24),
        VirtualGift("g_galaxy", "Galaxy Throne", GiftCategory.PREMIUM, 5200, 2600, 4600, "🌌", true, true, true, 0xFF8B5CF6),
        VirtualGift("g_eclipse", "Eclipse Sovereign", GiftCategory.EXCLUSIVE, 9999, 4999, 5000, "💎", true, true, true, 0xFF38BDF8),
    )

    val rechargePackages = listOf(
        RechargePackage("pkg_100", 500, 50, "₹49", "Starter"),
        RechargePackage("pkg_300", 1500, 200, "₹149", "Popular"),
        RechargePackage("pkg_600", 3500, 600, "₹299", "+17% Bonus"),
        RechargePackage("pkg_1500", 10000, 2000, "₹799", "Best Value"),
        RechargePackage("pkg_4000", 30000, 7500, "₹1,999", "VIP Choice"),
    )

    val vipTiers = listOf(
        VipTier(1, "VIP 1 • Spark", 300, 0xFF38BDF8, "Cyan Glow Ring", "Starlight Ripple", "Electric Mist Bubble",
            listOf("Cyan profile ring", "VIP 1 badge", "+10% daily check-in bonus", "Custom bio badge")),
        VipTier(2, "VIP 2 • Pulse", 600, 0xFF8B5CF6, "Violet Aura Ring", "Neon Beam Entry", "Violet Glass Bubble",
            listOf("Neon Beam room entry", "Priority mic queue", "2 exclusive romantic gifts", "Colorful nickname")),
        VipTier(3, "VIP 3 • Vanguard", 1200, 0xFFA855F7, "Prism Crown Frame", "Hologram Arrival", "Prism Gradient Bubble",
            listOf("Anti-kick protection from non-hosts", "Hide visitor footprint", "Exclusive profile cover", "3D accessory unlock")),
        VipTier(4, "VIP 4 • Noble", 2400, 0xFFF59E0B, "Amber Sovereign", "Golden Carpet Entry", "Gold Foil Chat Bubble",
            listOf("Golden Lion gift access", "Room spotlight ping", "+25% XP boost", "Private lounge theme")),
        VipTier(5, "VIP 5 • Monarch", 4500, 0xFFFBBF24, "Royal Halo Frame", "Royal Lion Arrival", "Monarch Flame Bubble",
            listOf("Full-screen entry announcement", "VIP customer concierge", "Custom seat aura", "Host fee discount")),
        VipTier(6, "SVIP • Celestial", 8000, 0xFFF43F5E, "Eclipse Dragon Frame", "Cosmic Dragon Entrance", "Celestial Aurora Bubble",
            listOf("Global room entry banner", "All limited-edition gifts", "SVIP Dragon avatar aura", "Priority event seeding")),
    )

    val partyGames = listOf(
        PartyGame(GameId.SPACE_WEREWOLF, "Space Werewolf", "Social Deduction", 1490, 50, 220, 10,
            "Crewmates complete starship tasks while impostors sabotage in the dark. Debate and eject suspects!", "🛸", 0xFF6200EE, 0xFF3B1D8A, isNew = true, isFeaturedBanner = true),
        PartyGame(GameId.JACKAROO, "Jackaroo", "Card & Board", 1180, 100, 350, 4,
            "Team-based tactical card & marble board race. Coordinate swaps and block rivals!", "🃏", 0xFFF59E0B, 0xFFD97706, isNew = false, isFeaturedBanner = true),
        PartyGame(GameId.PETROL_PUMP_TYCOON, "Petrol Pump Simulator", "Simulation", 1045, 0, 180, 6,
            "Manage fuel pumps, upgrade forecourt lanes, VIP car washes & rush-hour queues to build your station empire!", "⛽", 0xFF00B4D8, 0xFF0077B6, isNew = true, isFeaturedBanner = true),
        PartyGame(GameId.WHOS_THE_SPY, "Who's the Spy", "Social Deduction", 940, 20, 100, 8,
            "Each player gets a secret word—one spy has a similar word. Describe cleverly and vote out the spy!", "🕵️", 0xFFF43F5E, 0xFFBE123C, isNew = false, isFeaturedBanner = true),
        PartyGame(GameId.PAINT_TO_HIDE, "Paint to Hide", "Arcade Party", 890, 25, 130, 8,
            "Blend into painted walls matching your avatar tint before the Hunter's spotlight sweeps the hall!", "🎨", 0xFF10B981, 0xFF047857, isNew = true, isFeaturedBanner = false),
        PartyGame(GameId.TRICKSTERS_CAFE, "Trickster's Cafe", "Social Deduction", 820, 30, 150, 6,
            "Serve secret orders in a bustling midnight cafe while bluffing rivals and catching card tricksters!", "☕", 0xFF8B5CF6, 0xFF5B21B6, isNew = true, isFeaturedBanner = false),
        PartyGame(GameId.HIDE_AND_SEEK, "Hide and Seek", "Arcade Party", 730, 20, 100, 8,
            "Pick one of 6 cosmic pods to hide before the Seeker scans the station!", "👀", 0xFF06B6D4, 0xFF0E7490, isNew = false, isFeaturedBanner = false),
        PartyGame(GameId.OH_MY_CARD, "Oh My Card", "Card & Board", 910, 40, 160, 4,
            "Fast-paced color & action card clash! Drop +2, Reverse, and Wild cards to empty your hand first!", "🎴", 0xFFEC4899, 0xFF9D174D, isNew = true, isFeaturedBanner = false),
        PartyGame(GameId.LUDO, "Ludo", "Card & Board", 1420, 50, 180, 4,
            "Roll the dice, move your 4 cosmic tokens around the circuit, and race to the home star!", "🎲", 0xFF3B82F6, 0xFF1D4ED8, isNew = false, isFeaturedBanner = false),
        PartyGame(GameId.QUIZ_BATTLE, "Quiz Battle", "Trivia Speed", 760, 30, 120, 6,
            "Answer rapid-fire pop culture, music, science, and gaming trivia faster than opponents!", "⚡", 0xFFF59E0B, 0xFFB45309, isNew = false, isFeaturedBanner = false),
        PartyGame(GameId.DRAW_AND_GUESS, "Draw & Guess", "Creative Party", 830, 20, 90, 8,
            "Sketch the secret prompt on the live canvas before time runs out while friends race to guess!", "🖌️", 0xFF14B8A6, 0xFF0F766E),
        PartyGame(GameId.TRUTH_OR_DARE, "Truth or Dare", "Icebreaker", 1050, 0, 50, 12,
            "Spin the cosmic bottle in a live voice room! Pick Truth or Dare with community-safe prompts.", "🔥", 0xFFF43F5E, 0xFF9F1239),
        PartyGame(GameId.WORD_CHALLENGE, "Word Challenge", "Wordplay", 420, 25, 110, 6,
            "Unscramble cosmic word chains under pressure and keep your combo streak alive!", "🔤", 0xFF06B6D4, 0xFF155E75),
        PartyGame(GameId.MEMORY_CHALLENGE, "Memory Challenge", "Brain Reflex", 390, 20, 95, 4,
            "Repeat the glowing neon sequence as it grows longer every round!", "🧠", 0xFF7C3AED, 0xFF4C1D95),
        PartyGame(GameId.NUMBER_GUESS, "Number Guess", "Logic Duel", 510, 20, 85, 4,
            "Crack the hidden 1-100 vault code in fewest guesses using Higher/Lower telemetry!", "🔢", 0xFF0EA5E9, 0xFF0369A1),
        PartyGame(GameId.FAST_TYPING, "Fast Typing Battle", "Speed Arena", 480, 30, 130, 6,
            "Type high-energy phrases at peak WPM with zero typos to surge ahead!", "⌨️", 0xFF10B981, 0xFF065F46),
        PartyGame(GameId.SINGING_COMP, "Singing Competition", "Music Stage", 640, 50, 300, 10,
            "Take the stage mic for 60 seconds; audience & judges vote with applause and gifts!", "🎤", 0xFFE11D48, 0xFF881337),
        PartyGame(GameId.MIC_GRAB_KARAOKE, "Mic Grab / Karaoke", "Music Stage", 710, 25, 140, 8,
            "Beat the buzzer when the beat drops to claim the spotlight mic and finish the chorus!", "🎧", 0xFF9333EA, 0xFF581C87),
        PartyGame(GameId.TEAM_STRATEGY, "Team Strategy", "Guild Clash", 360, 100, 420, 8,
            "Capture energy nodes across a tactical hex grid with your squad!", "🛡️", 0xFFD97706, 0xFF92400E),
    )

    val dailyTasks = listOf(
        DailyTaskItem("tsk_1", "Daily Check-In Streak", "Check in once today to keep your 7-day coin streak", 150, 100, isCompleted = true, isClaimed = false),
        DailyTaskItem("tsk_2", "Join a Voice Party", "Spend 5 minutes listening or speaking in any Voice Room", 200, 150, isCompleted = true, isClaimed = false),
        DailyTaskItem("tsk_3", "Play 1 Multiplayer Game", "Complete a match in Ludo, Jackaroo, or Space Werewolf", 250, 200, isCompleted = false, isClaimed = false),
        DailyTaskItem("tsk_4", "Send a Virtual Gift", "Cheer a host with any virtual gift in a live room", 300, 250, isCompleted = false, isClaimed = false),
    )

    val initialTransactions = listOf(
        WalletTransactionEntity("tx_1", "RECHARGE", "COINS", 1500, true, "Starter Welcome Bundle", "ref_seed_001", System.currentTimeMillis() - 86400000L * 3),
        WalletTransactionEntity("tx_2", "GIFT_SENT", "COINS", 199, false, "Sent Royal Crown to DJ Rhea", "ref_seed_002", System.currentTimeMillis() - 86400000L * 2),
        WalletTransactionEntity("tx_3", "GIFT_EARNED", "DIAMONDS", 320, true, "Received Astral Phoenix in Room #101", "ref_seed_003", System.currentTimeMillis() - 86400000L),
        WalletTransactionEntity("tx_4", "GAME_REWARD", "COINS", 180, true, "Ludo Express 1st Place", "ref_seed_004", System.currentTimeMillis() - 3600000L * 5),
    )

    val initialPosts = listOf(
        SocialPostEntity(
            id = "post_1",
            authorId = "host_rhea",
            authorName = "DJ Rhea",
            authorVip = 5,
            content = "Just wrapped a 3-hour Midnight Neon acoustic set with 150+ listeners! Who's joining tonight's Mic Grab?",
            hashtag = "#MidnightNeon",
            mediaType = "SHORT_VIDEO",
            imageUri = null,
            likesCount = 342,
            commentsCount = 48,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 3600000L * 2,
        ),
        SocialPostEntity(
            id = "post_2",
            authorId = "host_kai",
            authorName = "Kai Storm",
            authorVip = 6,
            content = "Royal PK Arena hit 68K popularity! Shoutout to Celestial House for dropping two Cosmic Dragons in the final 10 seconds!",
            hashtag = "#PKBattle",
            mediaType = "IMAGE",
            imageUri = null,
            likesCount = 518,
            commentsCount = 91,
            isLiked = true,
            timestamp = System.currentTimeMillis() - 3600000L * 6,
        ),
        SocialPostEntity(
            id = "post_3",
            authorId = "host_zara",
            authorName = "Zara Moon",
            authorVip = 4,
            content = "Hosting a fresh Space Werewolf + Truth or Dare marathon at 9 PM. Drop your Wefun ID to get an invite!",
            hashtag = "#SpaceWerewolf",
            mediaType = "TEXT",
            imageUri = null,
            likesCount = 194,
            commentsCount = 37,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 3600000L * 11,
        ),
    )

    val initialThreads = listOf(
        ChatThread("th_official", "wefun_official", "WEFUN Official", "WF", 0xFF00C2B8, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 6, isOfficial = true, timeLabel = "11:30 am", lastSeenLabel = "Official System", unreadCount = 1, lastMessagePreview = "Welcome to WEFUN Carnival Week! Claim bonus tasks."),
        ChatThread("th_rhea", "host_rhea", "DJ Rhea", "DR", 0xFF8B5CF6, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 5, isOfficial = false, timeLabel = "11:23 am", lastSeenLabel = "Online", unreadCount = 2, lastMessagePreview = "Ready for our duet PK tonight?"),
        ChatThread("th_family", "fam_celestial", "Celestial House Group", "CH", 0xFFFBBF24, isGroup = true, isRequest = false, isOnline = true, isMuted = false, vipLevel = 4, isOfficial = false, timeLabel = "10:48 am", lastSeenLabel = "14 online", unreadCount = 5, lastMessagePreview = "Aria: Family tournament starts at 9 PM!"),
        ChatThread("th_kai", "host_kai", "Kai Storm", "KS", 0xFF38BDF8, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 6, isOfficial = false, timeLabel = "Yesterday", lastSeenLabel = "In PK Room", unreadCount = 0, lastMessagePreview = "GG! That last-second dragon was wild."),
        ChatThread("th_zara", "host_zara", "Zara Moon", "ZM", 0xFFEC4899, isGroup = false, isRequest = false, isOnline = false, isMuted = true, vipLevel = 4, isOfficial = false, timeLabel = "Yesterday", lastSeenLabel = "Last seen 2h ago", unreadCount = 0, lastMessagePreview = "Space Werewolf table opens at 9 PM!"),
        ChatThread("th_req1", "usr_kiran", "Kiran Beats", "KB", 0xFF10B981, isGroup = false, isRequest = true, isOnline = false, isMuted = false, vipLevel = 1, isOfficial = false, timeLabel = "18m ago", lastSeenLabel = "Last seen 18m ago", unreadCount = 1, lastMessagePreview = "Hey! Loved your singing in Midnight Lounge, can we collab?"),
    )

    val initialMessages = listOf(
        DirectMessageEntity("m_1", "th_rhea", "host_rhea", "DJ Rhea", "Hey Aria! Loved your acoustic cover earlier.", null, null, "🔥", true, System.currentTimeMillis() - 7200000L),
        DirectMessageEntity("m_2", "th_rhea", "usr_nova_01", "Aria Vance", "Thank you Rhea! Your room mix sounded super crisp.", null, null, "💜", true, System.currentTimeMillis() - 7000000L),
        DirectMessageEntity("m_3", "th_rhea", "host_rhea", "DJ Rhea", "Ready for our duet PK tonight?", null, null, null, false, System.currentTimeMillis() - 1200000L),
        DirectMessageEntity("m_4", "th_family", "host_milo", "Milo Taktik", "Who is representing us in Jackaroo tonight?", null, null, null, true, System.currentTimeMillis() - 3600000L),
        DirectMessageEntity("m_5", "th_family", "usr_nova_01", "Aria Vance", "Family tournament starts at 9 PM!", null, null, "⚡", true, System.currentTimeMillis() - 1800000L),
        DirectMessageEntity("m_6", "th_kai", "host_kai", "Kai Storm", "GG! That last-second dragon was wild.", "Cosmic Dragon", null, "🐉", true, System.currentTimeMillis() - 5400000L),
        DirectMessageEntity("m_7", "th_req1", "usr_kiran", "Kiran Beats", "Hey! Loved your singing in Midnight Lounge, can we collab?", null, null, null, false, System.currentTimeMillis() - 1080000L),
    )

    val initialMatches = listOf(
        GameMatchEntity("gm_1", "Ludo", "1st Place Victory", 420, 180, 120, System.currentTimeMillis() - 7200000L),
        GameMatchEntity("gm_2", "Quiz Battle", "2nd Place", 290, 60, 80, System.currentTimeMillis() - 86400000L),
    )

    val initialReports = listOf(
        SafetyReportEntity("rep_1", "Room", "Spam Promo Lobby #99", "Unsolicited external links in room announcement", "Pending", System.currentTimeMillis() - 3600000L * 4),
        SafetyReportEntity("rep_2", "User", "Guest_4920", "Repeated mic spam during Space Werewolf", "Reviewed", System.currentTimeMillis() - 3600000L * 18),
    )

    val families = listOf(
        FamilyInfo("fam_celestial", "Celestial House", "🌌", "Creators, singers & late-night party hosts uniting under starlight.", "Aria Vance", 128, 12, 485200, 1, true),
        FamilyInfo("fam_golden", "Golden Crown", "👑", "Elite PK warriors & top gift legends.", "Kai Storm", 96, 11, 412900, 2, false),
        FamilyInfo("fam_velvet", "Velvet Pulse", "🎧", "DJs, acoustic musicians & indie lyricists.", "DJ Rhea", 84, 9, 319400, 3, false),
        FamilyInfo("fam_shadow", "Shadow Syndicate", "🛸", "Competitive Space Werewolf, Ludo & Jackaroo champions.", "Milo Taktik", 110, 10, 298700, 4, false),
    )

    val events = listOf(
        EventItem("ev_1", "Starlight Singing Grand Prix", "Singing Competition", "Tonight • 9:00 PM IST", "rm_101", "50,000 Coins + SVIP Frame + Official Host Badge", "3 rounds of 60s live vocals. Audience votes + gift weight.", 184, true),
        EventItem("ev_2", "Titan PK Carnival Finals", "PK Carnival", "Sat • 8:30 PM IST", "rm_102", "100,000 Diamonds Pool + Golden Lion Trophy", "Best-of-3 5-minute PK battles between top 8 families.", 96, false),
        EventItem("ev_3", "Ludo & Jackaroo Weekend Cup", "Official Tournament", "Sun • 6:00 PM IST", "rm_103", "25,000 Coins + Gaming Master Title", "Single elimination 4-player tables. Anti-cheat verified.", 240, false),
        EventItem("ev_4", "Diwali & Cosmic Lantern Gala", "Seasonal Festival", "Nov 15 • All Day", "rm_107", "Limited Lantern Gift + 2x XP Boost", "Send or receive 10 Festival Lanterns to unlock badge.", 512, true),
    )

    val achievements = listOf(
        AchievementItem("ach_1", "Newcomer", "Create your WEFUN identity & enter a live voice room", "🌟", true, "Completed"),
        AchievementItem("ach_2", "Rising Star", "Reach User Level 10 and host 5 voice sessions", "🚀", true, "Completed"),
        AchievementItem("ach_3", "Super Host", "Maintain 4.8+ rating across 20 live hours", "🎙️", true, "24 / 20 hrs"),
        AchievementItem("ach_4", "PK Champion", "Win 10 timed PK battles with your room", "⚔️", false, "7 / 10 wins"),
        AchievementItem("ach_5", "Social Star", "Reach 1,000 followers & publish 10 moments", "💫", true, "1,280 followers"),
        AchievementItem("ach_6", "Gift Master", "Send 50,000 Coins worth of animated gifts", "🎁", false, "18,400 / 50,000"),
        AchievementItem("ach_7", "Gaming Master", "Win matches across 5 different party games", "🏆", false, "3 / 5 games"),
        AchievementItem("ach_8", "Family Leader", "Grow a Family past 100 members & Level 10", "🏰", true, "Lv.12 • 128 members"),
    )

    val notifications = listOf(
        NotificationItem("n_1", "Gifts", "DJ Rhea thanked you!", "Your Royal Crown boosted Midnight Neon Lounge to #2!", "4m ago", false),
        NotificationItem("n_2", "PK Invites", "PK Challenge from Kai Storm", "Royal PK Arena invited your room to a 5-min Host PK.", "16m ago", false),
        NotificationItem("n_3", "Events", "Starlight Singing Grand Prix starts at 9 PM", "You are registered in Official Room #rm_101.", "1h ago", true),
        NotificationItem("n_4", "VIP", "VIP 2 Pulse Active", "Your Neon Beam room entry effect is active for 24 more days.", "3h ago", true),
        NotificationItem("n_5", "Security", "Age & Profile Verified", "Your WEFUN safety check is complete.", "1d ago", true),
    )

    val hostStats = HostStats(
        totalHoursThisMonth = 38.5f,
        verifiedStatus = "Verified Pro Host",
        nextSchedule = "Tonight • 9:00 PM — Midnight Neon Acoustic",
        monthlyDiamonds = 14820,
        avgListeners = 132,
        agencyName = "Nova Star Agency",
        agencyCommissionPct = 15,
        settlementStatus = "In-App Diamonds Verified • Cycle closes Nov 30",
    )

    fun leaderboardFor(tab: String): List<LeaderboardEntry> = when (tab) {
        "Senders" -> listOf(
            LeaderboardEntry(1, "Aarav Royal", "SVIP • Celestial House", 184500, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Elena Noir", "VIP 5 • Golden Crown", 142200, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Vikram X", "VIP 4 • Shadow Syndicate", 98400, 4, 0xFF38BDF8),
            LeaderboardEntry(4, "Aria Vance", "VIP 2 • Celestial House", 42100, 2, 0xFFF43F5E),
        )
        "Receivers" -> listOf(
            LeaderboardEntry(1, "DJ Rhea", "Verified Host • Pulse", 215000, 5, 0xFF8B5CF6),
            LeaderboardEntry(2, "Kai Storm", "PK Champion • Golden Crown", 198300, 6, 0xFFFBBF24),
            LeaderboardEntry(3, "Zara Moon", "Starlight Host • Velvet", 126900, 4, 0xFFEC4899),
            LeaderboardEntry(4, "Aria Vance", "Music Host • Celestial", 74800, 2, 0xFF38BDF8),
        )
        "Hosts" -> listOf(
            LeaderboardEntry(1, "DJ Rhea", "72.4 hrs • 98% retention", 9850, 5, 0xFF8B5CF6),
            LeaderboardEntry(2, "Kai Storm", "64.0 hrs • 19 PK wins", 9420, 6, 0xFFFBBF24),
            LeaderboardEntry(3, "Aria Vance", "38.5 hrs • 132 avg listeners", 8610, 2, 0xFF38BDF8),
        )
        "Agencies" -> listOf(
            LeaderboardEntry(1, "Nova Star Agency", "48 Verified Hosts • 15% tier", 942000, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Pulse Creative", "35 Verified Hosts • 12% tier", 785000, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Crown Talent Hub", "29 Verified Hosts • 14% tier", 614000, 4, 0xFF38BDF8),
        )
        "PK" -> listOf(
            LeaderboardEntry(1, "Kai Storm", "19 Wins • 312K PK Score", 312000, 6, 0xFFF43F5E),
            LeaderboardEntry(2, "DJ Rhea", "15 Wins • 248K PK Score", 248000, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Leo Beats", "11 Wins • 189K PK Score", 189000, 6, 0xFFFBBF24),
        )
        else -> listOf( // Popularity / Games / Families
            LeaderboardEntry(1, "WEFUN Official Carnival", "540 live • Official", 91500, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Royal PK Arena", "312 live • Kai Storm", 68900, 6, 0xFFF43F5E),
            LeaderboardEntry(3, "Midnight Neon Lounge", "148 live • DJ Rhea", 42800, 5, 0xFF8B5CF6),
            LeaderboardEntry(4, "Space Werewolf Pro", "96 live • Milo Taktik", 31500, 3, 0xFF38BDF8),
        )
    }
}
