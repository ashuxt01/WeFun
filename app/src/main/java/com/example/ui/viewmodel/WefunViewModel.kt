package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngineState
import com.example.audio.VoiceAudioEngine
import com.example.data.local.WefunAppSettings
import com.example.data.model.AnnouncementCategory
import com.example.data.model.AvatarConfig
import com.example.data.model.ChatThread
import com.example.data.model.ContributorRankEntry
import com.example.data.model.DailyTaskItem
import com.example.data.model.DirectMessage
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.GameMatchRecord
import com.example.data.model.MicRequest
import com.example.data.model.MusicTrack
import com.example.data.model.NotificationItem
import com.example.data.model.RechargePackage
import com.example.data.model.RoomActivityLogEntry
import com.example.data.model.RoomAnnouncementEntry
import com.example.data.model.RoomCategory
import com.example.data.model.RoomContributionSummary
import com.example.data.model.RoomGiftLedgerEntry
import com.example.data.model.RoomLevelConfig
import com.example.data.model.RoomRole
import com.example.data.model.RoomThemeStyle
import com.example.data.model.SafetyReport
import com.example.data.model.SocialPost
import com.example.data.model.SongRequestItem
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceRoom
import com.example.data.model.VoiceRoomBackdropTheme
import com.example.data.model.WalletTransaction
import com.example.data.repository.VoiceRoomSeedData
import com.example.data.repository.WefunRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WefunViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WefunRepository(application)
    private val audioEngine = VoiceAudioEngine(application)
    private val roomManager = LiveRoomManager(viewModelScope)

    val settings: StateFlow<WefunAppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WefunAppSettings())

    val currentUser: StateFlow<UserProfile?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rooms: StateFlow<List<VoiceRoom>> = repository.roomsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<WalletTransaction>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<SocialPost>> = repository.postsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameMatches: StateFlow<List<GameMatchRecord>> = repository.gameMatchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<SafetyReport>> = repository.reportsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val threads: StateFlow<List<ChatThread>> = repository.threadsFlow
    val families: StateFlow<List<FamilyInfo>> = repository.familiesFlow
    val events: StateFlow<List<EventItem>> = repository.eventsFlow
    val notifications: StateFlow<List<NotificationItem>> = repository.notificationsFlow
    val followedUserIds: StateFlow<Set<String>> = repository.followedUserIdsFlow
    val dailyTasks: StateFlow<List<DailyTaskItem>> = repository.dailyTasksFlow
    val auditLogs: StateFlow<List<String>> = repository.auditLogsFlow

    val audioState: StateFlow<AudioEngineState> = audioEngine.state
    val liveRoomState: StateFlow<LiveRoomSessionState> = roomManager.session

    val roomLevelConfigs: StateFlow<List<RoomLevelConfig>> = repository.roomBackend.observeLevelConfigs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VoiceRoomSeedData.defaultLevelConfigs)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val activeRoomIdFlow = liveRoomState.map { it.room?.id }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomContribution: StateFlow<RoomContributionSummary?> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.roomBackend.observeContributionSummary(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomContributors: StateFlow<List<ContributorRankEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeContributorRankings(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomGiftLedger: StateFlow<List<RoomGiftLedgerEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeRoomGiftTransactions(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomAnnouncements: StateFlow<List<RoomAnnouncementEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeAnnouncements(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoomActivityLogs: StateFlow<List<RoomActivityLogEntry>> = activeRoomIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.roomBackend.observeActivityLogs(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeThreadId = MutableStateFlow<String?>(null)
    val activeThreadId: StateFlow<String?> = _activeThreadId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<DirectMessage>> = _activeThreadId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeThreadMessages(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
        viewModelScope.launch {
            audioEngine.state.collect { audio ->
                roomManager.updateUserMicTelemetry(audio.micRmsLevel, audio.isVoiceDetected)
            }
        }
    }

    fun consumeToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun refreshMicPermission() {
        audioEngine.refreshPermission()
    }

    fun completeSignIn(
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
        viewModelScope.launch {
            repository.completeSignIn(
                nickname = nickname,
                username = username,
                phone = phone,
                email = email,
                dob = dob,
                gender = gender,
                country = country,
                language = language,
                photoUri = photoUri
            )
        }
    }

    fun signOut() {
        leaveVoiceRoom()
        viewModelScope.launch { repository.signOut() }
    }

    fun deleteAccount() {
        leaveVoiceRoom()
        viewModelScope.launch {
            repository.deleteAccountAndReset()
            _toastMessage.value = "Account reset & local data cleared."
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setParentalControl(enabled: Boolean) {
        viewModelScope.launch {
            repository.setParentalControl(enabled)
            _toastMessage.value = if (enabled) "Parental Control & Safe Mode enabled." else "Parental Control disabled."
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguage(language)
            _toastMessage.value = "App language set to $language."
        }
    }

    fun claimDailyTask(taskId: String) {
        viewModelScope.launch {
            repository.claimDailyTask(taskId)
                .onSuccess { task ->
                    _toastMessage.value = "Task claimed: +${task.rewardCoins} Coins & +${task.rewardXp} XP!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun updateNotificationPrefs(gifts: Boolean, rooms: Boolean, pk: Boolean, messages: Boolean) {
        viewModelScope.launch {
            repository.updateNotificationPrefs(gifts, rooms, pk, messages)
            _toastMessage.value = "Notification preferences saved."
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            repository.claimDailyCheckIn()
                .onSuccess { reward ->
                    _toastMessage.value = "Claimed +$reward Coins & +150 XP!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun rechargeCoins(pkg: RechargePackage) {
        viewModelScope.launch {
            val refId = "gp_sim_${pkg.id}_${System.currentTimeMillis()}"
            repository.rechargeCoins(pkg, refId)
                .onSuccess { total ->
                    _toastMessage.value = "Verified +$total Coins added to your Wallet!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun purchaseVip(level: Int, costCoins: Long, tierName: String) {
        viewModelScope.launch {
            repository.purchaseVipTier(level, costCoins, tierName)
                .onSuccess {
                    _toastMessage.value = "$tierName activated! Exclusive frame & effects unlocked."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun updateProfileAndAvatar(
        nickname: String,
        bio: String,
        country: String,
        language: String,
        showGender: Boolean,
        showAge: Boolean,
        photoUri: String?,
        avatarConfig: AvatarConfig,
    ) {
        viewModelScope.launch {
            repository.updateProfileAndAvatar(
                nickname, bio, country, language, showGender, showAge, photoUri, avatarConfig
            )
            _toastMessage.value = "Profile & 3D Avatar saved."
        }
    }

    fun createAndJoinRoom(
        title: String,
        description: String,
        category: RoomCategory,
        maxSeats: Int,
        password: String?,
        theme: RoomThemeStyle,
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val created = repository.createVoiceRoom(title, description, category, maxSeats, password, theme)
            val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(created.id)
            roomManager.enterRoom(created, user, owner, partner, roles)
            audioEngine.startMicCapture()
        }
    }

    fun joinRoom(room: VoiceRoom) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
            roomManager.enterRoom(room, user, owner, partner, roles)
        }
    }

    fun minimizeVoiceRoom() = roomManager.minimizeRoom()
    fun restoreVoiceRoom() = roomManager.restoreRoom()
    fun dismissLevelUpBanner() = roomManager.dismissLevelUpBanner()

    fun leaveVoiceRoom() {
        audioEngine.stopMicCapture()
        audioEngine.toggleBackgroundMusic(false)
        roomManager.leaveRoom()
    }

    fun toggleMicMute() {
        val muted = roomManager.toggleUserMute()
        if (muted) {
            audioEngine.stopMicCapture()
        } else {
            audioEngine.startMicCapture()
        }
    }

    fun tapSeat(seatIndex: Int) {
        val user = currentUser.value ?: return
        val session = liveRoomState.value
        if (session.pendingMoveFromSeat != null) {
            roomManager.completeMoveSeat(seatIndex)
            return
        }
        if (session.currentUserSeatIndex == seatIndex) {
            roomManager.leaveMicSeat()
            audioEngine.stopMicCapture()
            return
        }
        roomManager.takeOrRequestSeat(seatIndex, user)
        if (liveRoomState.value.currentUserSeatIndex == seatIndex) {
            audioEngine.startMicCapture()
        }
    }

    fun toggleFavoriteRoom() {
        val room = liveRoomState.value.room ?: return
        viewModelScope.launch {
            val fav = repository.roomBackend.toggleFavoriteRoom(room.id)
            roomManager.updateFavoriteState(fav)
            _toastMessage.value = if (fav) "Added room to favorites!" else "Removed from favorites."
        }
    }

    fun acceptMicRequest(req: MicRequest) = roomManager.acceptMicRequest(req)
    fun rejectMicRequest(userId: String) = roomManager.rejectMicRequest(userId)
    fun toggleSeatLock(seatIndex: Int) = roomManager.toggleSeatLock(seatIndex)
    fun toggleSeatMicLock(seatIndex: Int) = roomManager.toggleSeatMicLock(seatIndex)
    fun toggleOccupantMuteByAdmin(seatIndex: Int) = roomManager.toggleOccupantMuteByAdmin(seatIndex)
    fun toggleSeatCoHost(seatIndex: Int) = roomManager.toggleSeatCoHost(seatIndex)
    fun removeUserFromSeat(seatIndex: Int) = roomManager.removeUserFromSeat(seatIndex)
    fun startMoveSeat(seatIndex: Int) = roomManager.startMoveSeat(seatIndex)
    fun reserveSeat(seatIndex: Int, name: String?) = roomManager.reserveSeat(seatIndex, name)
    fun assignUserToSeat(seatIndex: Int, userId: String, name: String, vip: Int) =
        roomManager.assignUserToSeat(seatIndex, userId, name, vip)

    fun banUserFromRoom(userId: String, name: String) {
        val room = liveRoomState.value.room ?: return
        val actor = currentUser.value?.nickname ?: "Moderator"
        roomManager.banUserFromRoom(userId, name)
        viewModelScope.launch {
            repository.roomBackend.logActivity(room.id, actor, "User Banned", "$name banned from room")
        }
    }

    fun assignMemberRole(targetUserId: String, targetName: String, newRole: RoomRole) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.assignRole(room.id, actor.nickname, session.currentUserRole, targetUserId, targetName, newRole)
                .onSuccess {
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
                    roomManager.refreshSnapshotRoles(owner, partner, roles, actor.uid)
                    _toastMessage.value = "$targetName updated to ${newRole.label}."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun transferHostToSeat(seatIndex: Int) {
        val seat = liveRoomState.value.seats.getOrNull(seatIndex) ?: return
        val targetId = seat.occupantId ?: return
        val targetName = seat.occupantName ?: return
        assignMemberRole(targetId, targetName, RoomRole.OWNER)
        roomManager.removeUserFromSeat(seatIndex)
    }

    fun publishRoomAnnouncement(category: AnnouncementCategory, title: String, content: String) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.publishAnnouncement(room.id, actor.nickname, session.currentUserRole, category, title, content)
                .onSuccess {
                    roomManager.updateRoomMetadata(announcement = content)
                    _toastMessage.value = "Announcement published!"
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun assignPartner(targetUserId: String, targetName: String) {
        assignMemberRole(targetUserId, targetName, RoomRole.PARTNER)
    }

    fun removePartner() {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.removePartner(room.id, actor.nickname, session.currentUserRole)
                .onSuccess {
                    val (owner, partner, roles) = repository.roomBackend.loadRoomSnapshot(room.id)
                    roomManager.refreshSnapshotRoles(owner, partner, roles, actor.uid)
                    _toastMessage.value = "Partner position cleared."
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun updateRoomBackdropTheme(backdropTheme: VoiceRoomBackdropTheme) {
        roomManager.updateRoomMetadata(backdropTheme = backdropTheme)
        _toastMessage.value = "Room backdrop changed to ${backdropTheme.label}."
    }

    fun updateRoomAppearance(title: String, theme: RoomThemeStyle, decoration: String, musicAdminsOnly: Boolean) {
        val room = liveRoomState.value.room ?: return
        val actor = currentUser.value?.nickname ?: "Owner"
        viewModelScope.launch {
            repository.roomBackend.updateRoomAppearanceAndPermissions(room.id, actor, title, theme, decoration, musicAdminsOnly)
            roomManager.updateRoomMetadata(title = title, theme = theme, decorationStyle = decoration, musicAdminsOnly = musicAdminsOnly)
            _toastMessage.value = "Room appearance & permissions saved."
        }
    }

    fun updateAdminLevelSeatConfig(seatCounts: Map<Int, Int>, pointsPerCoin: Double, diamondsPerCoin: Double) {
        val room = liveRoomState.value.room ?: return
        val session = liveRoomState.value
        val actor = currentUser.value ?: return
        viewModelScope.launch {
            repository.roomBackend.updateLevelSeatMappingAndRules(
                room.id, actor.nickname, session.currentUserRole, seatCounts, pointsPerCoin, diamondsPerCoin
            ).onSuccess {
                _toastMessage.value = "Level 1–10 seat capacities & conversion rules updated."
            }.onFailure { _toastMessage.value = it.message }
        }
    }

    fun sendRoomChat(text: String, isEmoji: Boolean = false) {
        val user = currentUser.value ?: return
        roomManager.sendRoomChat(text, user, isEmoji)
    }

    fun sendVirtualGiftInRoom(
        gift: VirtualGift,
        comboCount: Int,
        targetSeatIndices: List<Int>,
        receiverLabel: String,
    ) {
        val user = currentUser.value ?: return
        val session = liveRoomState.value
        val room = session.room ?: return
        val countTargets = targetSeatIndices.size.coerceAtLeast(1)
        val firstSeat = targetSeatIndices.firstOrNull()?.let { idx ->
            session.seats.firstOrNull { it.seatIndex == idx }
        }
        val receiverId = firstSeat?.occupantId ?: room.hostId
        viewModelScope.launch {
            repository.spendCoinsForGift(gift, comboCount, countTargets, receiverLabel)
                .onSuccess { totalCoins ->
                    val process = repository.roomBackend.processRoomGiftContribution(
                        roomId = room.id,
                        sender = user,
                        receiverId = receiverId,
                        receiverName = receiverLabel,
                        gift = gift,
                        comboCount = comboCount,
                        totalCoins = totalCoins,
                    )
                    roomManager.triggerGiftAndContributionUpdate(
                        gift = gift,
                        senderName = user.nickname,
                        receiverName = receiverLabel,
                        comboCount = comboCount,
                        totalCoins = totalCoins,
                        targetSeatIndices = targetSeatIndices,
                        updatedRoomLevel = process.updatedRoomLevel,
                        updatedTotalContribution = process.updatedTotalContribution,
                        updatedSeatCount = process.updatedSeatCount,
                        contributionPointsAdded = process.contributionPointsAdded,
                        levelUpEvent = process.levelUpEvent,
                    )
                    repository.pushNotification(
                        category = "Gifts",
                        title = "Gift Delivered!",
                        body = "Sent ${gift.name} x$comboCount ($totalCoins Coins, +${process.contributionPointsAdded} Room Pts) to $receiverLabel."
                    )
                }
                .onFailure { _toastMessage.value = it.message }
        }
    }

    fun startPkBattle(mode: String, opponentName: String) = roomManager.startPkBattle(mode, opponentName)
    fun endPkBattle() = roomManager.endPkBattle()

    fun toggleRoomMusic() {
        val session = liveRoomState.value
        val rm = session.room ?: return
        if (rm.musicAdminsOnly && !session.currentUserRole.canModerate()) {
            _toastMessage.value = "Music controls are restricted to Room Admins."
            return
        }
        val next = !rm.isMusicPlaying
        roomManager.toggleMusicState(next)
        audioEngine.toggleBackgroundMusic(next)
    }

    fun selectOrRequestTrack(track: MusicTrack) {
        val session = liveRoomState.value
        val user = currentUser.value ?: return
        val isAdmin = session.currentUserRole.canModerate() || !(session.room?.musicAdminsOnly ?: false)
        roomManager.requestOrSelectTrack(track, user.nickname, isAdmin)
        if (isAdmin) {
            audioEngine.toggleBackgroundMusic(true)
        } else {
            _toastMessage.value = "Song requested: ${track.title}"
        }
    }

    fun approveSongRequest(item: SongRequestItem) {
        if (!liveRoomState.value.currentUserRole.canModerate()) {
            _toastMessage.value = "Only Moderators/Admins can approve music queue."
            return
        }
        roomManager.approveAndPlayQueueItem(item)
        audioEngine.toggleBackgroundMusic(true)
    }

    fun createSocialPost(content: String, hashtag: String, mediaType: String, imageUri: String?) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.createSocialPost(content, hashtag, mediaType, imageUri)
            _toastMessage.value = "Moment published (+45 XP)!"
        }
    }

    fun toggleLikePost(post: SocialPost) {
        viewModelScope.launch { repository.toggleLikePost(post) }
    }

    fun commentOnPost(post: SocialPost) {
        viewModelScope.launch {
            repository.addCommentToPost(post)
            _toastMessage.value = "Comment added!"
        }
    }

    fun openThread(threadId: String?) {
        _activeThreadId.value = threadId
    }

    fun sendDirectMessage(threadId: String, text: String, gift: VirtualGift? = null, voiceSeconds: Int? = null) {
        viewModelScope.launch {
            if (gift != null) {
                val res = repository.spendCoinsForGift(gift, 1, 1, "Chat Peer")
                if (res.isFailure) {
                    _toastMessage.value = res.exceptionOrNull()?.message
                    return@launch
                }
            }
            repository.sendDirectMessage(threadId, text, gift?.name, voiceSeconds)
        }
    }

    fun deleteDirectMessage(id: String) {
        viewModelScope.launch { repository.deleteDirectMessage(id) }
    }

    fun reactToDirectMessage(message: DirectMessage, emoji: String) {
        viewModelScope.launch { repository.reactToDirectMessage(message, emoji) }
    }

    fun toggleMuteThread(threadId: String) {
        repository.toggleMuteThread(threadId)
        _toastMessage.value = "Conversation mute preference updated."
    }

    fun markAllThreadsRead() {
        repository.markAllThreadsRead()
        _toastMessage.value = "All chats marked as read."
    }

    fun acceptFriendRequest(threadId: String) {
        repository.acceptFriendRequest(threadId)
        _toastMessage.value = "Friend request accepted!"
    }

    fun declineFriendRequest(threadId: String) {
        repository.declineFriendRequest(threadId)
        _toastMessage.value = "Friend request removed."
    }

    fun startOrOpenChat(peerId: String, peerName: String, isGroup: Boolean = false) {
        val tid = repository.startOrOpenChat(peerId, peerName, isGroup)
        _activeThreadId.value = tid
    }

    fun recordGameMatch(gameTitle: String, outcome: String, score: Int, coinsWon: Long, xpEarned: Long) {
        viewModelScope.launch {
            repository.recordGameResult(gameTitle, outcome, score, coinsWon, xpEarned)
            _toastMessage.value = "$gameTitle: $outcome (+$coinsWon Coins, +$xpEarned XP)"
        }
    }

    fun toggleFamilyMembership(familyId: String) = repository.toggleFamilyMembership(familyId)
    fun createFamily(name: String, intro: String, emoji: String) {
        repository.createFamily(name, intro, emoji)
        _toastMessage.value = "Family '$name' created!"
    }

    fun toggleEventRegistration(eventId: String) {
        repository.toggleEventRegistration(eventId)
        _toastMessage.value = "Event registration updated."
    }

    fun toggleFollow(userId: String) = repository.toggleFollowUser(userId)

    fun toggleBlockUser(userId: String, name: String) {
        viewModelScope.launch {
            repository.toggleBlockUser(userId, name)
            _toastMessage.value = "Updated block list for $name."
        }
    }

    fun submitReport(targetType: String, targetName: String, reason: String) {
        viewModelScope.launch {
            repository.submitSafetyReport(targetType, targetName, reason)
            _toastMessage.value = "Report submitted to WEFUN Safety Queue."
        }
    }

    fun resolveReport(report: SafetyReport, status: String) {
        viewModelScope.launch {
            repository.resolveReport(report, status)
            _toastMessage.value = "Report marked $status."
        }
    }

    fun adminGrantCoins(amount: Long, reason: String) {
        viewModelScope.launch {
            repository.adminGrantCoins(amount, reason)
            _toastMessage.value = "Admin Ledger updated ($amount Coins)."
        }
    }

    fun markAllNotificationsRead() = repository.markAllNotificationsRead()

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
