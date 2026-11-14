package com.example.data.repository

import android.content.Context
import com.example.data.local.DirectMessageEntity
import com.example.data.local.GameMatchEntity
import com.example.data.local.SafetyReportEntity
import com.example.data.local.SocialPostEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.local.WefunAppSettings
import com.example.data.local.WefunDatabase
import com.example.data.local.WefunPreferences
import com.example.data.model.AvatarConfig
import com.example.data.model.ChatThread
import com.example.data.model.CurrencyKind
import com.example.data.model.DailyTaskItem
import com.example.data.model.DirectMessage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameMatchRecord
import com.example.data.model.NotificationItem
import com.example.data.model.RechargePackage
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SafetyReport
import com.example.data.model.SocialPost
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class WefunRepository(context: Context) {

    private val db = WefunDatabase.getInstance(context)
    private val prefs = WefunPreferences(context)

    private val userDao = db.userDao()
    private val roomDao = db.voiceRoomDao()
    private val txDao = db.transactionDao()
    private val postDao = db.socialPostDao()
    private val msgDao = db.messageDao()
    private val matchDao = db.gameMatchDao()
    private val reportDao = db.reportDao()
    private val sysDao = db.voiceRoomSystemDao()

    val roomBackend = VoiceRoomBackendEngine(roomDao, sysDao)

    val settingsFlow: Flow<WefunAppSettings> = prefs.settingsFlow

    val currentUserFlow: Flow<UserProfile?> = userDao.observeCurrentUser().map { it?.toDomain() }

    val roomsFlow: Flow<List<VoiceRoom>> = roomDao.observeRooms().map { list ->
        list.map { it.toDomain() }
    }

    val transactionsFlow: Flow<List<WalletTransaction>> = txDao.observeTransactions().map { list ->
        list.map { it.toDomain() }
    }

    val postsFlow: Flow<List<SocialPost>> = postDao.observePosts().map { list ->
        list.map { it.toDomain() }
    }

    val gameMatchesFlow: Flow<List<GameMatchRecord>> = matchDao.observeMatches().map { list ->
        list.map { it.toDomain() }
    }

    val reportsFlow: Flow<List<SafetyReport>> = reportDao.observeReports().map { list ->
        list.map { it.toDomain() }
    }

    private val _threads = MutableStateFlow(WefunSeedData.initialThreads)
    val threadsFlow = _threads.asStateFlow()

    private val _families = MutableStateFlow(WefunSeedData.families)
    val familiesFlow = _families.asStateFlow()

    private val _events = MutableStateFlow(WefunSeedData.events)
    val eventsFlow = _events.asStateFlow()

    private val _notifications = MutableStateFlow(WefunSeedData.notifications)
    val notificationsFlow = _notifications.asStateFlow()

    private val _followedUserIds = MutableStateFlow(setOf("host_rhea", "host_kai"))
    val followedUserIdsFlow = _followedUserIds.asStateFlow()

    private val _dailyTasks = MutableStateFlow(WefunSeedData.dailyTasks)
    val dailyTasksFlow = _dailyTasks.asStateFlow()

    private val _auditLogs = MutableStateFlow(
        listOf(
            "Admin initialized local verified ledger & safety rules",
            "Commission tier set to 15% for Nova Star Agency",
            "Festival Banner #4 published to Home Discovery",
        )
    )
    val auditLogsFlow = _auditLogs.asStateFlow()

    suspend fun ensureSeeded() {
        if (userDao.getCurrentUser() == null) {
            userDao.upsertUser(WefunSeedData.defaultUser())
        }
        if (roomDao.countRooms() == 0) {
            roomDao.upsertRooms(WefunSeedData.initialRooms)
        }
        roomBackend.ensureRoomSystemSeeded(WefunSeedData.initialRooms)
        if (txDao.countTransactions() == 0) {
            WefunSeedData.initialTransactions.forEach { txDao.insertTransaction(it) }
        }
        if (postDao.countPosts() == 0) {
            postDao.upsertPosts(WefunSeedData.initialPosts)
        }
        if (msgDao.countMessages() == 0) {
            msgDao.upsertMessages(WefunSeedData.initialMessages)
        }
        if (matchDao.countMatches() == 0) {
            WefunSeedData.initialMatches.forEach { matchDao.insertMatch(it) }
        }
        if (reportDao.countReports() == 0) {
            WefunSeedData.initialReports.forEach { reportDao.upsertReport(it) }
        }
    }

    suspend fun completeSignIn(
        nickname: String,
        username: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        country: String,
        language: String,
        photoUri: String?,
    ) {
        ensureSeeded()
        val existing = userDao.getCurrentUser() ?: WefunSeedData.defaultUser()
        userDao.upsertUser(
            existing.copy(
                nickname = nickname.ifBlank { existing.nickname },
                username = username.ifBlank { existing.username },
                phone = phone.ifBlank { existing.phone },
                email = email.ifBlank { existing.email },
                dob = dob.ifBlank { existing.dob },
                gender = gender.ifBlank { existing.gender },
                country = country.ifBlank { existing.country },
                language = language.ifBlank { existing.language },
                photoUri = photoUri ?: existing.photoUri,
            )
        )
        prefs.setLoggedIn(loggedIn = true, onboardingDone = true)
    }

    suspend fun signOut() {
        prefs.setLoggedIn(loggedIn = false, onboardingDone = true)
    }

    suspend fun deleteAccountAndReset() {
        userDao.clearUsers()
        prefs.clearAll()
        ensureSeeded()
    }

    suspend fun setDarkMode(enabled: Boolean) {
        prefs.setDarkMode(enabled)
    }

    suspend fun setParentalControl(enabled: Boolean) {
        prefs.setParentalControl(enabled)
        appendAuditLog("Parental Control ${if (enabled) "enabled" else "disabled"}")
    }

    suspend fun updateLanguage(language: String) {
        val user = userDao.getCurrentUser() ?: return
        userDao.upsertUser(user.copy(language = language))
    }

    suspend fun updateNotificationPrefs(gifts: Boolean, rooms: Boolean, pk: Boolean, messages: Boolean) {
        prefs.updateNotificationPrefs(gifts, rooms, pk, messages)
    }

    suspend fun toggleBlockUser(userId: String, displayName: String) {
        prefs.toggleBlockUser(userId)
        appendAuditLog("User block status toggled for $displayName ($userId)")
    }

    suspend fun claimDailyCheckIn(): Result<Long> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        if (user.isCheckedInToday) {
            return Result.failure(IllegalStateException("Already checked in today!"))
        }
        val nextDay = (user.lastCheckInDay % 7) + 1
        val rewardCoins = (100L * nextDay) + if (user.vipLevel > 0) user.vipLevel * 25L else 0L
        val xpGain = 150L
        val updated = applyXpGain(
            user.copy(
                coins = user.coins + rewardCoins,
                bonusPoints = user.bonusPoints + 40,
                lastCheckInDay = nextDay,
                isCheckedInToday = true
            ),
            xpGain
        )
        userDao.upsertUser(updated)
        recordTransaction(
            type = TransactionType.DAILY_CHECKIN,
            currency = CurrencyKind.COINS,
            amount = rewardCoins,
            isCredit = true,
            description = "Day $nextDay Check-In Reward (+${xpGain} XP)",
            referenceId = "checkin_${System.currentTimeMillis()}"
        )
        return Result.success(rewardCoins)
    }

    suspend fun rechargeCoins(pkg: RechargePackage, referenceId: String): Result<Long> {
        if (txDao.existsByReference(referenceId)) {
            return Result.failure(IllegalStateException("Duplicate transaction blocked"))
        }
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val total = pkg.coins + pkg.bonusCoins
        userDao.upsertUser(user.copy(coins = user.coins + total))
        recordTransaction(
            type = TransactionType.RECHARGE,
            currency = CurrencyKind.COINS,
            amount = total,
            isCredit = true,
            description = "Sandbox Recharge: ${pkg.coins} + ${pkg.bonusCoins} Bonus (${pkg.priceDisplay})",
            referenceId = referenceId
        )
        appendAuditLog("Verified coin recharge +$total Coins (${pkg.id})")
        return Result.success(total)
    }

    suspend fun spendCoinsForGift(
        gift: VirtualGift,
        comboCount: Int,
        receiverCount: Int,
        receiverLabel: String,
    ): Result<Long> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val totalCost = gift.coinPrice * comboCount * receiverCount
        if (totalCost <= 0) return Result.failure(IllegalArgumentException("Invalid gift amount"))
        if (user.coins < totalCost) {
            return Result.failure(IllegalStateException("Not enough Coins! Need $totalCost Coins."))
        }
        val xpEarned = (totalCost / 5L).coerceAtLeast(10L)
        val updated = applyXpGain(
            user.copy(
                coins = user.coins - totalCost,
                giftsSentCount = user.giftsSentCount + comboCount * receiverCount,
                bonusPoints = user.bonusPoints + (comboCount * 2L),
            ),
            xpEarned
        )
        userDao.upsertUser(updated)
        recordTransaction(
            type = TransactionType.GIFT_SENT,
            currency = CurrencyKind.COINS,
            amount = totalCost,
            isCredit = false,
            description = "Sent ${gift.name} x${comboCount * receiverCount} to $receiverLabel",
            referenceId = "gift_${UUID.randomUUID()}"
        )
        return Result.success(totalCost)
    }

    suspend fun purchaseVipTier(level: Int, costCoins: Long, tierName: String): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        if (user.coins < costCoins) {
            return Result.failure(IllegalStateException("Insufficient Coins for $tierName ($costCoins required)"))
        }
        val updated = applyXpGain(
            user.copy(
                coins = user.coins - costCoins,
                vipLevel = maxOf(user.vipLevel, level),
                frameName = WefunSeedData.vipTiers.firstOrNull { it.level == level }?.frameTitle ?: user.frameName
            ),
            300L
        )
        userDao.upsertUser(updated)
        recordTransaction(
            type = TransactionType.VIP_PURCHASE,
            currency = CurrencyKind.COINS,
            amount = costCoins,
            isCredit = false,
            description = "Activated $tierName Membership (30 days)",
            referenceId = "vip_${UUID.randomUUID()}"
        )
        return Result.success(Unit)
    }

    suspend fun updateProfileAndAvatar(
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        avatarConfig: AvatarConfig,
    ) {
        val user = userDao.getCurrentUser() ?: return
        userDao.upsertUser(
            user.copy(
                nickname = nickname.ifBlank { user.nickname },
                bio = bio,
                country = country,
                language = language,
                showGender = showGender,
                showAge = showAge,
                photoUri = photoUri ?: user.photoUri,
                faceStyle = avatarConfig.faceStyle,
                hairStyle = avatarConfig.hairStyle,
                outfitStyle = avatarConfig.outfitStyle,
                accessory = avatarConfig.accessory,
                backgroundAura = avatarConfig.backgroundAura,
                frameName = avatarConfig.frameName,
            )
        )
    }

    suspend fun createVoiceRoom(
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
    ): VoiceRoom {
        val user = userDao.getCurrentUser() ?: WefunSeedData.defaultUser()
        val id = "${(1000000..9999999).random()}"
        val entity = VoiceRoomEntity(
            id = id,
            title = title.ifBlank { "${user.nickname}'s Voice Party" },
            description = description.ifBlank { "Live WEFUN voice hangout & party games!" },
            categoryName = category.name,
            password = password?.takeIf { it.isNotBlank() },
            hostId = user.uid,
            hostName = user.nickname,
            hostVip = user.vipLevel,
            maxSeats = maxSeats.coerceAtLeast(8),
            roomLevel = 1,
            totalContribution = 0L,
            isFavorited = false,
            createdAtMillis = System.currentTimeMillis(),
            decorationStyle = "Charcoal Gold Standard",
            musicAdminsOnly = false,
            themeName = theme.name,
            announcement = "Welcome! Tap an open golden mic seat to join the conversation.",
            popularity = 5000,
            onlineCount = 1,
            language = user.language,
            familyName = "Celestial House",
        )
        roomDao.upsertRoom(entity)
        roomBackend.ensureSingleRoomSeeded(entity)
        appendAuditLog("Created Level 1 room '${entity.title}' (ID: $id, 8 seats)")
        return entity.toDomain()
    }

    suspend fun createSocialPost(content: String, hashtag: String, mediaType: String, imageUri: String?) {
        val user = userDao.getCurrentUser() ?: return
        val post = SocialPostEntity(
            id = "post_${UUID.randomUUID()}",
            authorId = user.uid,
            authorName = user.nickname,
            authorVip = user.vipLevel,
            content = content,
            hashtag = if (hashtag.startsWith("#")) hashtag else "#${hashtag.ifBlank { "WEFUN" }}",
            mediaType = mediaType,
            imageUri = imageUri,
            likesCount = 1,
            commentsCount = 0,
            isLiked = true,
            timestamp = System.currentTimeMillis(),
        )
        postDao.upsertPost(post)
        val updated = applyXpGain(user, 45L)
        userDao.upsertUser(updated)
    }

    suspend fun toggleLikePost(post: SocialPost) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        postDao.upsertPost(
            SocialPostEntity(
                id = post.id,
                authorId = post.authorId,
                authorName = post.authorName,
                authorVip = post.authorVip,
                content = post.content,
                hashtag = post.hashtag,
                mediaType = post.mediaType,
                imageUri = post.imageUri,
                likesCount = newCount,
                commentsCount = post.commentsCount,
                isLiked = newLiked,
                timestamp = post.timestamp,
            )
        )
    }

    suspend fun addCommentToPost(post: SocialPost) {
        postDao.upsertPost(
            SocialPostEntity(
                id = post.id,
                authorId = post.authorId,
                authorName = post.authorName,
                authorVip = post.authorVip,
                content = post.content,
                hashtag = post.hashtag,
                mediaType = post.mediaType,
                imageUri = post.imageUri,
                likesCount = post.likesCount,
                commentsCount = post.commentsCount + 1,
                isLiked = post.isLiked,
                timestamp = post.timestamp,
            )
        )
    }

    fun observeThreadMessages(threadId: String): Flow<List<DirectMessage>> =
        msgDao.observeMessagesForThread(threadId).map { list -> list.map { it.toDomain() } }

    suspend fun sendDirectMessage(
        threadId: String,
        text: String,
        giftName: String? = null,
        voiceSeconds: Int? = null,
    ) {
        val user = userDao.getCurrentUser() ?: return
        val msg = DirectMessageEntity(
            id = "dm_${UUID.randomUUID()}",
            threadId = threadId,
            senderId = user.uid,
            senderName = user.nickname,
            text = text,
            giftName = giftName,
            voiceSeconds = voiceSeconds,
            reaction = null,
            isRead = true,
            timestamp = System.currentTimeMillis(),
        )
        msgDao.insertMessage(msg)
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(lastMessagePreview = text, unreadCount = 0, isRequest = false)
            else th
        }
    }

    suspend fun deleteDirectMessage(id: String) {
        msgDao.deleteMessage(id)
    }

    suspend fun reactToDirectMessage(message: DirectMessage, emoji: String) {
        msgDao.insertMessage(
            DirectMessageEntity(
                id = message.id,
                threadId = message.threadId,
                senderId = message.senderId,
                senderName = message.senderName,
                text = message.text,
                giftName = message.giftName,
                voiceSeconds = message.voiceSeconds,
                reaction = emoji,
                isRead = message.isRead,
                timestamp = message.timestamp,
            )
        )
    }

    fun toggleMuteThread(threadId: String) {
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(isMuted = !th.isMuted) else th
        }
    }

    fun markAllThreadsRead() {
        _threads.value = _threads.value.map { it.copy(unreadCount = 0) }
    }

    fun acceptFriendRequest(threadId: String) {
        _threads.value = _threads.value.map { th ->
            if (th.id == threadId) th.copy(isRequest = false, unreadCount = 0) else th
        }
    }

    fun declineFriendRequest(threadId: String) {
        _threads.value = _threads.value.filterNot { it.id == threadId }
    }

    fun startOrOpenChat(peerId: String, peerName: String, isGroup: Boolean = false): String {
        val existing = _threads.value.firstOrNull { it.peerId == peerId || it.peerName.equals(peerName, ignoreCase = true) }
        if (existing != null) return existing.id
        val newId = "th_${UUID.randomUUID().toString().take(6)}"
        val initials = peerName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").ifBlank { "WF" }
        val newThread = ChatThread(
            id = newId,
            peerId = peerId,
            peerName = peerName,
            peerInitials = initials,
            accentHex = 0xFF00C2B8,
            isGroup = isGroup,
            isRequest = false,
            isOnline = true,
            isMuted = false,
            vipLevel = 2,
            timeLabel = "Just now",
            lastSeenLabel = "Online",
            unreadCount = 0,
            lastMessagePreview = "Say hello on WEFUN!"
        )
        _threads.value = listOf(newThread) + _threads.value
        return newId
    }

    suspend fun claimDailyTask(taskId: String): Result<DailyTaskItem> {
        val task = _dailyTasks.value.firstOrNull { it.id == taskId }
            ?: return Result.failure(IllegalArgumentException("Task not found"))
        if (task.isClaimed) return Result.failure(IllegalStateException("Task already claimed"))
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        val updatedUser = applyXpGain(user.copy(coins = user.coins + task.rewardCoins), task.rewardXp)
        userDao.upsertUser(updatedUser)
        recordTransaction(
            type = TransactionType.GAME_REWARD,
            currency = CurrencyKind.COINS,
            amount = task.rewardCoins,
            isCredit = true,
            description = "Task Complete: ${task.title}",
            referenceId = "task_${task.id}_${System.currentTimeMillis()}"
        )
        _dailyTasks.value = _dailyTasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = true, isClaimed = true) else it
        }
        return Result.success(task)
    }

    suspend fun recordGameResult(gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) {
        val match = GameMatchEntity(
            id = "gm_${UUID.randomUUID()}",
            gameTitle = gameTitle,
            outcome = outcome,
            score = score,
            coinsWon = coinsWon,
            xpEarned = xpEarned,
            timestamp = System.currentTimeMillis(),
        )
        matchDao.insertMatch(match)
        val user = userDao.getCurrentUser() ?: return
        val updated = applyXpGain(user.copy(coins = user.coins + coinsWon), xpEarned)
        userDao.upsertUser(updated)
        if (coinsWon > 0) {
            recordTransaction(
                type = TransactionType.GAME_REWARD,
                currency = CurrencyKind.COINS,
                amount = coinsWon,
                isCredit = true,
                description = "$gameTitle • $outcome (+$xpEarned XP)",
                referenceId = "game_${UUID.randomUUID()}"
            )
        }
    }

    suspend fun submitSafetyReport(targetType: String, targetName: String, reason: String) {
        val report = SafetyReportEntity(
            id = "rep_${UUID.randomUUID()}",
            targetType = targetType,
            targetName = targetName,
            reason = reason,
            status = "Pending",
            timestamp = System.currentTimeMillis(),
        )
        reportDao.upsertReport(report)
        appendAuditLog("Safety report filed: [$targetType] $targetName — $reason")
    }

    suspend fun resolveReport(report: SafetyReport, newStatus: String) {
        reportDao.upsertReport(
            SafetyReportEntity(
                id = report.id,
                targetType = report.targetType,
                targetName = report.targetName,
                reason = report.reason,
                status = newStatus,
                timestamp = report.timestamp,
            )
        )
        appendAuditLog("Moderator updated report ${report.id} -> $newStatus")
    }

    fun toggleFamilyMembership(familyId: String) {
        _families.value = _families.value.map { fam ->
            if (fam.id == familyId) {
                val joined = !fam.isJoined
                fam.copy(
                    isJoined = joined,
                    memberCount = if (joined) fam.memberCount + 1 else (fam.memberCount - 1).coerceAtLeast(1)
                )
            } else fam
        }
    }

    fun createFamily(name: String, intro: String, badgeEmoji: String) {
        val newFam = FamilyInfo(
            id = "fam_${UUID.randomUUID().toString().take(6)}",
            name = name.ifBlank { "Nova Legends" },
            badgeEmoji = badgeEmoji.ifBlank { "⚡" },
            intro = intro.ifBlank { "New WEFUN community family!" },
            leaderName = "You",
            memberCount = 1,
            level = 1,
            contributionPoints = 500,
            rank = _families.value.size + 1,
            isJoined = true,
        )
        _families.value = listOf(newFam) + _families.value
    }

    fun toggleEventRegistration(eventId: String) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                val reg = !ev.isRegistered
                ev.copy(
                    isRegistered = reg,
                    participantCount = if (reg) ev.participantCount + 1 else (ev.participantCount - 1).coerceAtLeast(0)
                )
            } else ev
        }
    }

    fun toggleFollowUser(userId: String) {
        val current = _followedUserIds.value
        _followedUserIds.value = if (userId in current) current - userId else current + userId
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun pushNotification(category: String, title: String, body: String) {
        val item = NotificationItem(
            id = "n_${UUID.randomUUID()}",
            category = category,
            title = title,
            body = body,
            timeLabel = "Just now",
            isRead = false
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    suspend fun adminGrantCoins(amount: Long, reason: String): Result<Unit> {
        val user = userDao.getCurrentUser() ?: return Result.failure(IllegalStateException("No user"))
        userDao.upsertUser(user.copy(coins = (user.coins + amount).coerceAtLeast(0)))
        recordTransaction(
            type = TransactionType.REFUND,
            currency = CurrencyKind.COINS,
            amount = kotlin.math.abs(amount),
            isCredit = amount >= 0,
            description = "Admin Adjustment: $reason",
            referenceId = "admin_${UUID.randomUUID()}"
        )
        appendAuditLog("Admin adjusted balance by $amount Coins ($reason)")
        return Result.success(Unit)
    }

    fun appendAuditLog(entry: String) {
        _auditLogs.value = (listOf(entry) + _auditLogs.value).take(25)
    }

    private suspend fun recordTransaction(
        type: TransactionType,
        currency: CurrencyKind,
        amount: Long,
        isCredit: Boolean,
        description: String,
        referenceId: String,
    ) {
        txDao.insertTransaction(
            WalletTransactionEntity(
                id = "tx_${UUID.randomUUID()}",
                typeName = type.name,
                currencyName = currency.name,
                amount = amount,
                isCredit = isCredit,
                description = description,
                referenceId = referenceId,
                timestamp = System.currentTimeMillis(),
            )
        )
    }

    private fun applyXpGain(user: UserProfileEntity, xpGain: Long): UserProfileEntity {
        var newXp = user.xp + xpGain
        var level = user.userLevel
        var threshold = user.nextLevelXp
        while (newXp >= threshold) {
            newXp -= threshold
            level += 1
            threshold = (threshold * 1.25).toLong()
        }
        return user.copy(xp = newXp, userLevel = level, nextLevelXp = threshold)
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        uid = uid,
        wefunId = wefunId,
        username = username,
        nickname = nickname,
        bio = bio,
        phone = phone,
        email = email,
        dob = dob,
        isAgeVerified = isAgeVerified,
        gender = gender,
        showGender = showGender,
        showAge = showAge,
        country = country,
        language = language,
        photoUri = photoUri,
        accentColorHex = accentColorHex,
        avatarConfig = AvatarConfig(faceStyle, hairStyle, outfitStyle, accessory, backgroundAura, frameName),
        vipLevel = vipLevel,
        userLevel = userLevel,
        xp = xp,
        nextLevelXp = nextLevelXp,
        coins = coins,
        diamonds = diamonds,
        bonusPoints = bonusPoints,
        followersCount = followersCount,
        followingCount = followingCount,
        friendsCount = friendsCount,
        giftsReceivedCount = giftsReceivedCount,
        giftsSentCount = giftsSentCount,
        visitorsCount = visitorsCount,
        familyId = familyId,
        isHostVerified = isHostVerified,
        hostLevel = hostLevel,
        agencyName = agencyName,
        lastCheckInDay = lastCheckInDay,
        isCheckedInToday = isCheckedInToday,
    )

    private fun VoiceRoomEntity.toDomain() = VoiceRoom(
        id = id,
        title = title,
        description = description,
        category = runCatching { RoomCategory.valueOf(categoryName) }.getOrDefault(RoomCategory.PUBLIC),
        password = password,
        hostId = hostId,
        hostName = hostName,
        hostVip = hostVip,
        maxSeats = maxSeats,
        roomLevel = roomLevel,
        totalContribution = totalContribution,
        isFavorited = isFavorited,
        createdAtMillis = createdAtMillis,
        decorationStyle = decorationStyle,
        musicAdminsOnly = musicAdminsOnly,
        themeStyle = runCatching { RoomThemeStyle.valueOf(themeName) }.getOrDefault(RoomThemeStyle.GALAXY),
        announcement = announcement,
        popularity = popularity,
        onlineCount = onlineCount,
        language = language,
        familyName = familyName,
        isPkActive = categoryName == RoomCategory.PK.name,
    )

    private fun WalletTransactionEntity.toDomain() = WalletTransaction(
        id = id,
        type = runCatching { TransactionType.valueOf(typeName) }.getOrDefault(TransactionType.RECHARGE),
        currency = runCatching { CurrencyKind.valueOf(currencyName) }.getOrDefault(CurrencyKind.COINS),
        amount = amount,
        isCredit = isCredit,
        description = description,
        referenceId = referenceId,
        timestamp = timestamp,
    )

    private fun SocialPostEntity.toDomain() = SocialPost(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorVip = authorVip,
        content = content,
        hashtag = hashtag,
        mediaType = mediaType,
        imageUri = imageUri,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isLiked = isLiked,
        timestamp = timestamp,
    )

    private fun DirectMessageEntity.toDomain() = DirectMessage(
        id = id,
        threadId = threadId,
        senderId = senderId,
        senderName = senderName,
        text = text,
        giftName = giftName,
        voiceSeconds = voiceSeconds,
        reaction = reaction,
        isRead = isRead,
        timestamp = timestamp,
    )

    private fun GameMatchEntity.toDomain() = GameMatchRecord(
        id = id,
        gameTitle = gameTitle,
        outcome = outcome,
        score = score,
        coinsWon = coinsWon,
        xpEarned = xpEarned,
        timestamp = timestamp,
    )

    private fun SafetyReportEntity.toDomain() = SafetyReport(
        id = id,
        targetType = targetType,
        targetName = targetName,
        reason = reason,
        status = status,
        timestamp = timestamp,
    )
}
