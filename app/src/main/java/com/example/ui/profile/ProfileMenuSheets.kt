package com.example.ui.profile

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.data.repository.WefunSeedData
import com.example.ui.components.GlassCard
import com.example.ui.components.WefunGradientButton
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TurquoiseActive

enum class ProfileMenuDetail {
    NONE,
    SHOP,
    MY_HOME,
    MOMENTS,
    STATS,
    VISITORS,
    INVITE_FRIENDS,
    BADGES,
    CONTRIBUTIONS,
    LANGUAGE,
    PARENTAL_CONTROL,
    MY_GIFTS,
    MY_FRIENDS,
    SETTINGS,
    HELP_SUPPORT,
    PRIVACY_SECURITY,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailSheetHost(
    detail: ProfileMenuDetail,
    user: UserProfile,
    settings: WefunAppSettings,
    posts: List<SocialPost>,
    followedUserIds: Set<String>,
    onDismiss: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenVip: () -> Unit,
    onOpenAvatarEditor: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleParentalControl: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    if (detail == ProfileMenuDetail.NONE) return
    val wefunColors = LocalWefunColors.current
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (detail) {
                ProfileMenuDetail.SHOP -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Storefront, null, tint = GoldAmber)
                            Text("WEFUN Shop & Avatar Frames", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Text("Cosmetic frames, entrance mounts & chat bubbles (preview demo store).", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                    }
                    val shopItems = listOf(
                        Triple("Astral Gold Halo", "Avatar Frame • 30 days", "600 Coins"),
                        Triple("Nebula Cruiser Mount", "Room Entry Effect • 30 days", "1,200 Coins"),
                        Triple("Turquoise Crystal Bubble", "Chat Bubble • Permanent", "450 Coins"),
                        Triple("Royal Lion Crest", "VIP Seat Aura • 30 days", "900 Coins"),
                    )
                    items(shopItems) { (title, sub, price) ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(sub, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                }
                                Surface(
                                    onClick = {
                                        onShowToast("Equipped preview for $title! Unlock VIP tiers or Wallet for full items.")
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = TurquoiseActive
                                ) {
                                    Text(price, color = Color.White, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                }
                            }
                        }
                    }
                    item {
                        WefunGradientButton(text = "Open VIP Privileges", onClick = { onDismiss(); onOpenVip() })
                    }
                }

                ProfileMenuDetail.MY_HOME -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Home, null, tint = TurquoiseActive)
                            Text("My Home & Personal Showcase", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${user.nickname} (@${user.username})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("WEFUN ID: ${user.wefunId} • ${user.country} (${user.language})", style = MaterialTheme.typography.bodySmall, color = TurquoiseActive)
                                Text(user.bio, style = MaterialTheme.typography.bodyMedium)
                                Text("3D Outfit: ${user.avatarConfig.hairStyle} • ${user.avatarConfig.outfitStyle} • ${user.avatarConfig.accessory}", style = MaterialTheme.typography.labelSmall, color = GoldAmber)
                            }
                        }
                    }
                    item {
                        WefunGradientButton(text = "Customize PLAY Show Avatar", onClick = { onDismiss(); onOpenAvatarEditor() })
                    }
                }

                ProfileMenuDetail.MOMENTS -> {
                    item {
                        Text("My Moments (${posts.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    items(posts, key = { it.id }) { p ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text(p.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(p.hashtag, style = MaterialTheme.typography.labelSmall, color = TurquoiseActive)
                                }
                                Text(p.content, style = MaterialTheme.typography.bodySmall)
                                Text("${p.likesCount} likes • ${p.commentsCount} comments", style = MaterialTheme.typography.labelSmall, color = wefunColors.mutedText)
                            }
                        }
                    }
                }

                ProfileMenuDetail.STATS -> {
                    val stats = WefunSeedData.hostStats
                    item {
                        Text("Host & Player Stats", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Account Level: Lv.${user.userLevel} (${user.xp} / ${user.nextLevelXp} XP)", fontWeight = FontWeight.Bold)
                                Text("Host Rating: ${stats.verifiedStatus} (Host Lv.${user.hostLevel})", color = TurquoiseActive)
                                Text("Live Hosting Hours: ${stats.totalHoursThisMonth} hrs this month")
                                Text("Avg Room Audience: ${stats.avgListeners} listeners")
                                Text("Gifts Sent: ${user.giftsSentCount} • Gifts Received: ${user.giftsReceivedCount}", color = GoldAmber)
                                Text("Diamonds Earned: ${user.diamonds} • Agency: ${stats.agencyName}")
                            }
                        }
                    }
                }

                ProfileMenuDetail.VISITORS -> {
                    item {
                        Text("Recent Profile Visitors (${user.visitorsCount})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    val visitors = listOf(
                        Triple("DJ Rhea", "Visited 12m ago", "VIP 5"),
                        Triple("Kai Storm", "Visited 48m ago", "SVIP"),
                        Triple("Zara Moon", "Visited 2h ago", "VIP 4"),
                        Triple("Milo Taktik", "Visited Yesterday", "VIP 3"),
                    )
                    items(visitors) { (name, whenText, vip) ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column {
                                    Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(whenText, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                }
                                Text(vip, style = MaterialTheme.typography.labelMedium, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ProfileMenuDetail.INVITE_FRIENDS -> {
                    item {
                        Text("Invite Friends & Earn Rewards", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Share your WEFUN ID (${user.wefunId}) to earn +300 Bonus Coins when friends join!", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Your Invite Code: ${user.wefunId}", style = MaterialTheme.typography.titleMedium, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                                WefunGradientButton(
                                    text = "Share Invite Link",
                                    leadingIcon = { Icon(Icons.Rounded.Share, null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "Join me on WEFUN Voice Party & Games! Use invite code ${user.wefunId}")
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Invite Friends to WEFUN"))
                                    }
                                )
                            }
                        }
                    }
                }

                ProfileMenuDetail.BADGES -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.EmojiEvents, null, tint = GoldAmber)
                            Text("Badges & Achievements", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(WefunSeedData.achievements, key = { it.id }) { ach ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (ach.isUnlocked) GoldAmber.copy(alpha = 0.5f) else null
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ach.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(ach.description, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    ach.progressText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (ach.isUnlocked) EmeraldMint else wefunColors.mutedText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                ProfileMenuDetail.CONTRIBUTIONS -> {
                    item {
                        Text("Contribution History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Total Diamonds: ${user.diamonds} • Total Gifts Sent: ${user.giftsSentCount}", style = MaterialTheme.typography.bodySmall, color = TurquoiseActive)
                    }
                    val contribs = listOf(
                        Triple("Midnight Neon Music (ID: 1579056)", "4,250 Room Pts", "Level 1 Active"),
                        Triple("Celestial House Townhall", "248,000 Room Pts", "Family Rank #1"),
                        Triple("Royal PK Arena", "75,000 Room Pts", "Level 3 PK"),
                    )
                    items(contribs) { (roomName, pts, badge) ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(roomName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(badge, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                }
                                Text(pts, style = MaterialTheme.typography.labelLarge, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ProfileMenuDetail.LANGUAGE -> {
                    item {
                        Text("Select Language", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    val langs = listOf("English", "Hindi", "Arabic", "Spanish", "Turkish", "Indonesian")
                    items(langs) { lang ->
                        val selected = user.language.equals(lang, ignoreCase = true)
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (selected) TurquoiseActive else null,
                            onClick = {
                                onSelectLanguage(lang)
                                onDismiss()
                            }
                        ) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text(lang, style = MaterialTheme.typography.titleSmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                                if (selected) Icon(Icons.Rounded.CheckCircle, null, tint = TurquoiseActive)
                            }
                        }
                    }
                }

                ProfileMenuDetail.PARENTAL_CONTROL -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Shield, null, tint = TurquoiseActive)
                            Text("Parental Control & Safe Mode", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Enable Parental Safety Lock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("Restricts coin top-ups, private requests, and locks 18+ voice rooms.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                    }
                                    Switch(
                                        checked = settings.parentalControlEnabled,
                                        onCheckedChange = onToggleParentalControl
                                    )
                                }
                            }
                        }
                    }
                }

                ProfileMenuDetail.MY_GIFTS -> {
                    item {
                        Text("My Gifts Gallery", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Received: ${user.giftsReceivedCount} • Sent: ${user.giftsSentCount}", style = MaterialTheme.typography.bodySmall, color = GoldAmber)
                    }
                    items(WefunSeedData.virtualGifts.take(8), key = { it.id }) { g ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(g.emoji, style = MaterialTheme.typography.headlineSmall)
                                    Column {
                                        Text(g.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(g.category.label, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                    }
                                }
                                Text("${g.coinPrice} Coins", style = MaterialTheme.typography.labelLarge, color = GoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ProfileMenuDetail.MY_FRIENDS -> {
                    item {
                        Text("My Friends (${user.friendsCount})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    val friends = listOf(
                        "host_rhea" to "DJ Rhea",
                        "host_kai" to "Kai Storm",
                        "host_zara" to "Zara Moon",
                        "host_milo" to "Milo Taktik",
                    )
                    items(friends) { (uid, name) ->
                        val following = uid in followedUserIds
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Column {
                                    Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(if (following) "Mutual Friend" else "Suggested", style = MaterialTheme.typography.bodySmall, color = TurquoiseActive)
                                }
                                OutlinedButton(onClick = { onToggleFollow(uid) }) {
                                    Text(if (following) "Unfollow" else "Follow")
                                }
                            }
                        }
                    }
                }

                ProfileMenuDetail.SETTINGS -> {
                    item {
                        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.DarkMode, null, tint = PurplePrimary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Dark Theme (Main Tabs)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text("Voice rooms always stay in dark studio mode", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                        }
                                    }
                                    Switch(
                                        checked = settings.isDarkMode,
                                        onCheckedChange = onToggleDarkMode,
                                        modifier = Modifier.testTag("theme_switch")
                                    )
                                }

                                Surface(
                                    onClick = { onDismiss(); onOpenNotifications() },
                                    shape = RoundedCornerShape(12.dp),
                                    color = TurquoiseActive.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Notifications, null, tint = TurquoiseActive)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Notifications & Alert Preferences", style = MaterialTheme.typography.titleSmall)
                                    }
                                }

                                Surface(
                                    onClick = { onDismiss(); onOpenAdminPanel() },
                                    shape = RoundedCornerShape(12.dp),
                                    color = RoyalGold.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f)),
                                    modifier = Modifier.fillMaxWidth().testTag("open_admin_panel_button")
                                ) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.AdminPanelSettings, null, tint = GoldAmber)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Admin & Safety Console", style = MaterialTheme.typography.titleSmall, color = GoldAmber, fontWeight = FontWeight.Bold)
                                            Text("Reports queue, audit logs & role controls", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { onDismiss(); onSignOut() },
                                        modifier = Modifier.weight(1f).testTag("logout_button")
                                    ) {
                                        Icon(Icons.Rounded.Logout, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Logout")
                                    }
                                    OutlinedButton(
                                        onClick = { showDeleteConfirm = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.DeleteForever, null, tint = RoseNeon, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Delete", color = RoseNeon)
                                    }
                                }
                            }
                        }
                    }
                }

                ProfileMenuDetail.HELP_SUPPORT -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.HelpOutline, null, tint = TurquoiseActive)
                            Text("Help & Support", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("• How do Voice Room Levels work?", fontWeight = FontWeight.Bold)
                                Text("Rooms start at Level 1 (8 seats) and automatically expand up to 26 seats as members send virtual gifts.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                Text("• Are Coins real money?", fontWeight = FontWeight.Bold)
                                Text("No. Coins and Diamonds are local/in-app virtual currencies for entertainment only.", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                OutlinedButton(onClick = { onShowToast("Support ticket logged in local demo queue.") }) {
                                    Text("Contact WEFUN Support")
                                }
                            }
                        }
                    }
                }

                ProfileMenuDetail.PRIVACY_SECURITY -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Lock, null, tint = PurplePrimary)
                            Text("Privacy & Security", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Age Verification: ${if (user.isAgeVerified) "Verified 18+" else "Unverified"}", color = EmeraldMint, fontWeight = FontWeight.Bold)
                                Text("Blocked Users: ${settings.blockedUserIds.size}")
                                Text("Show Gender: ${if (user.showGender) "Public" else "Hidden"} • Show Age: ${if (user.showAge) "Public" else "Hidden"}")
                                WefunGradientButton(text = "Edit Privacy in Profile Studio", onClick = { onDismiss(); onOpenAvatarEditor() })
                            }
                        }
                    }
                }

                ProfileMenuDetail.NONE -> {}
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account & Local Data?") },
            text = { Text("This clears your local WEFUN profile, wallet history, and preferences from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDismiss()
                    onDeleteAccount()
                }) { Text("Delete", color = RoseNeon) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
