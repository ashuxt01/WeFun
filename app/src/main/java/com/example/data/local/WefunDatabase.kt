package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val wefunId: String,
    val username: String,
    val nickname: String,
    val bio: String,
    val phone: String,
    val email: String,
    val dob: String,
    val isAgeVerified: Boolean,
    val gender: String,
    val showGender: Boolean,
    val showAge: Boolean,
    val country: String,
    val language: String,
    val photoUri: String?,
    val accentColorHex: Long,
    val faceStyle: String,
    val hairStyle: String,
    val outfitStyle: String,
    val accessory: String,
    val backgroundAura: String,
    val frameName: String,
    val vipLevel: Int,
    val userLevel: Int,
    val xp: Long,
    val nextLevelXp: Long,
    val coins: Long,
    val diamonds: Long,
    val bonusPoints: Long,
    val followersCount: Int,
    val followingCount: Int,
    val friendsCount: Int,
    val giftsReceivedCount: Int,
    val giftsSentCount: Int,
    val visitorsCount: Int,
    val familyId: String?,
    val isHostVerified: Boolean,
    val hostLevel: Int,
    val agencyName: String,
    val lastCheckInDay: Int,
    val isCheckedInToday: Boolean,
)

@Entity(tableName = "voice_rooms")
data class VoiceRoomEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val categoryName: String,
    val password: String?,
    val hostId: String,
    val hostName: String,
    val hostVip: Int,
    val maxSeats: Int,
    val roomLevel: Int = 1,
    val totalContribution: Long = 0L,
    val isFavorited: Boolean = false,
    val createdAtMillis: Long = 1762000000000L,
    val decorationStyle: String = "Charcoal Gold Standard",
    val musicAdminsOnly: Boolean = false,
    val themeName: String,
    val announcement: String,
    val popularity: Long,
    val onlineCount: Int,
    val language: String,
    val familyName: String?,
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val typeName: String,
    val currencyName: String,
    val amount: Long,
    val isCredit: Boolean,
    val description: String,
    val referenceId: String,
    val timestamp: Long,
)

@Entity(tableName = "social_posts")
data class SocialPostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorVip: Int,
    val content: String,
    val hashtag: String,
    val mediaType: String,
    val imageUri: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val timestamp: Long,
)

@Entity(tableName = "direct_messages")
data class DirectMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val giftName: String?,
    val voiceSeconds: Int?,
    val reaction: String?,
    val isRead: Boolean,
    val timestamp: Long,
)

@Entity(tableName = "game_matches")
data class GameMatchEntity(
    @PrimaryKey val id: String,
    val gameTitle: String,
    val outcome: String,
    val score: Int,
    val coinsWon: Long,
    val xpEarned: Long,
    val timestamp: Long,
)

@Entity(tableName = "safety_reports")
data class SafetyReportEntity(
    @PrimaryKey val id: String,
    val targetType: String,
    val targetName: String,
    val reason: String,
    val status: String,
    val timestamp: Long,
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeCurrentUser(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getCurrentUser(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUsers()
}

@Dao
interface VoiceRoomDao {
    @Query("SELECT * FROM voice_rooms ORDER BY popularity DESC")
    fun observeRooms(): Flow<List<VoiceRoomEntity>>

    @Query("SELECT * FROM voice_rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoomById(roomId: String): VoiceRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRooms(rooms: List<VoiceRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoom(room: VoiceRoomEntity)

    @Query("SELECT COUNT(*) FROM voice_rooms")
    suspend fun countRooms(): Int
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun observeTransactions(): Flow<List<WalletTransactionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wallet_transactions WHERE referenceId = :referenceId)")
    suspend fun existsByReference(referenceId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(tx: WalletTransactionEntity)

    @Query("SELECT COUNT(*) FROM wallet_transactions")
    suspend fun countTransactions(): Int
}

@Dao
interface SocialPostDao {
    @Query("SELECT * FROM social_posts ORDER BY timestamp DESC")
    fun observePosts(): Flow<List<SocialPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(posts: List<SocialPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPost(post: SocialPostEntity)

    @Query("SELECT COUNT(*) FROM social_posts")
    suspend fun countPosts(): Int
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM direct_messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun observeMessagesForThread(threadId: String): Flow<List<DirectMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessages(messages: List<DirectMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DirectMessageEntity)

    @Query("DELETE FROM direct_messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("SELECT COUNT(*) FROM direct_messages")
    suspend fun countMessages(): Int
}

@Dao
interface GameMatchDao {
    @Query("SELECT * FROM game_matches ORDER BY timestamp DESC")
    fun observeMatches(): Flow<List<GameMatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: GameMatchEntity)

    @Query("SELECT COUNT(*) FROM game_matches")
    suspend fun countMatches(): Int
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM safety_reports ORDER BY timestamp DESC")
    fun observeReports(): Flow<List<SafetyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReport(report: SafetyReportEntity)

    @Query("SELECT COUNT(*) FROM safety_reports")
    suspend fun countReports(): Int
}

@Database(
    entities = [
        UserProfileEntity::class,
        VoiceRoomEntity::class,
        WalletTransactionEntity::class,
        SocialPostEntity::class,
        DirectMessageEntity::class,
        GameMatchEntity::class,
        SafetyReportEntity::class,
        RoomLevelEntity::class,
        RoomSeatEntity::class,
        RoomOwnerEntity::class,
        RoomPartnerEntity::class,
        RoomAdminEntity::class,
        RoomModeratorEntity::class,
        RoomParticipantEntity::class,
        RoomContributionEntity::class,
        RoomGiftTransactionEntity::class,
        ContributorRankingEntity::class,
        RoomAnnouncementEntity::class,
        RoomSettingsEntity::class,
        RoomActivityLogEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class WefunDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun voiceRoomDao(): VoiceRoomDao
    abstract fun transactionDao(): TransactionDao
    abstract fun socialPostDao(): SocialPostDao
    abstract fun messageDao(): MessageDao
    abstract fun gameMatchDao(): GameMatchDao
    abstract fun reportDao(): ReportDao
    abstract fun voiceRoomSystemDao(): VoiceRoomSystemDao

    companion object {
        @Volatile
        private var INSTANCE: WefunDatabase? = null

        fun getInstance(context: Context): WefunDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WefunDatabase::class.java,
                    "wefun_local.db"
                ).fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
