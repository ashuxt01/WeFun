package com.example.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.VipBadge
import com.example.ui.components.WefunBrandLogo
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.DeepNavyHeading
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.LightLavenderTop
import com.example.ui.theme.LocalWefunColors
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TurquoiseActive

private data class ProfileMenuEntry(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconTint: Color,
    val onClick: () -> Unit,
)

@Composable
fun ProfileAndEcosystemScreen(
    user: UserProfile,
    settings: WefunAppSettings,
    posts: List<SocialPost>,
    followedUserIds: Set<String>,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleParentalControl: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onPickProfilePhoto: (String) -> Unit,
    onOpenAvatarEditor: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenVip: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    val wefunColors = LocalWefunColors.current
    val clipboard = LocalClipboardManager.current
    var activeDetail by remember { mutableStateOf(ProfileMenuDetail.NONE) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onPickProfilePhoto(uri.toString()) }

    val menuItems = remember(user, settings) {
        listOf(
            ProfileMenuEntry("Moments", "${posts.size} posts", Icons.Rounded.PhotoLibrary, TurquoiseActive) {
                activeDetail = ProfileMenuDetail.MOMENTS
            },
            ProfileMenuEntry("Stats", "Lv.${user.userLevel} • ${user.xp} XP", Icons.Rounded.BarChart, PurplePrimary) {
                activeDetail = ProfileMenuDetail.STATS
            },
            ProfileMenuEntry("Visitors", "${user.visitorsCount} recent", Icons.Rounded.Visibility, Color(0xFF3B82F6)) {
                activeDetail = ProfileMenuDetail.VISITORS
            },
            ProfileMenuEntry("Invite Friends", "Code: ${user.wefunId}", Icons.Rounded.GroupAdd, EmeraldMint) {
                activeDetail = ProfileMenuDetail.INVITE_FRIENDS
            },
            ProfileMenuEntry("Badge", "5 unlocked", Icons.Rounded.EmojiEvents, GoldAmber) {
                activeDetail = ProfileMenuDetail.BADGES
            },
            ProfileMenuEntry("Contributions", "${user.diamonds} Diamonds", Icons.Rounded.WorkspacePremium, RoseNeon) {
                activeDetail = ProfileMenuDetail.CONTRIBUTIONS
            },
            ProfileMenuEntry("Language", user.language, Icons.Rounded.Language, TurquoiseActive) {
                activeDetail = ProfileMenuDetail.LANGUAGE
            },
            ProfileMenuEntry(
                "Parental Control",
                if (settings.parentalControlEnabled) "Active" else "Off",
                Icons.Rounded.ChildCare,
                Color(0xFF0EA5E9)
            ) {
                activeDetail = ProfileMenuDetail.PARENTAL_CONTROL
            },
            ProfileMenuEntry("Wallet", "${user.coins} Coins", Icons.Rounded.AccountBalanceWallet, GoldAmber) {
                onOpenWallet()
            },
            ProfileMenuEntry("My Gifts", "${user.giftsReceivedCount} received", Icons.Rounded.CardGiftcard, RoseNeon) {
                activeDetail = ProfileMenuDetail.MY_GIFTS
            },
            ProfileMenuEntry("My Friends", "${user.friendsCount} friends", Icons.Rounded.People, EmeraldMint) {
                activeDetail = ProfileMenuDetail.MY_FRIENDS
            },
            ProfileMenuEntry("Settings", "Theme, Alerts & Admin", Icons.Rounded.Settings, PurplePrimary) {
                activeDetail = ProfileMenuDetail.SETTINGS
            },
            ProfileMenuEntry("Help and Support", "FAQ & Guide", Icons.Rounded.HelpOutline, Color(0xFF64748B)) {
                activeDetail = ProfileMenuDetail.HELP_SUPPORT
            },
            ProfileMenuEntry("Privacy and Security", "18+ Verified", Icons.Rounded.Lock, Color(0xFF475569)) {
                activeDetail = ProfileMenuDetail.PRIVACY_SECURITY
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen 5 Profile Header (Reference Screenshot 5)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            if (wefunColors.isDark) {
                                listOf(Color(0xFF231E38), MaterialTheme.colorScheme.background)
                            } else {
                                listOf(LightLavenderTop, MaterialTheme.colorScheme.background)
                            }
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Me",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GoldAccent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "VIP ${user.vipLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, Brush.linearGradient(listOf(GoldAccent, TurquoiseActive, PurplePrimary)), CircleShape)
                            .clickable {
                                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                    ) {
                        if (user.photoUri != null) {
                            AsyncImage(
                                model = user.photoUri,
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_avatar_neon_host_1794578988450),
                                contentDescription = "Profile avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(TurquoiseActive)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.CameraAlt, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = user.nickname,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            VipBadge(vipLevel = user.vipLevel)
                            if (user.isHostVerified) {
                                Icon(Icons.Rounded.Verified, "Verified", tint = TurquoiseActive, modifier = Modifier.size(16.dp))
                            }
                        }

                        // 3 Badge Pills (Level, Host Level, Gifts) matching Screenshot 5
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            MiniCounterChip("♥ ${user.giftsReceivedCount}", Color(0xFFF43F5E))
                            MiniCounterChip("Lv.${user.userLevel}", EmeraldMint)
                            MiniCounterChip("★ Host ${user.hostLevel}", GoldAmber)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ID:${user.wefunId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = wefunColors.mutedText
                            )
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(user.wefunId))
                                    onShowToast("Copied WEFUN ID ${user.wefunId}!")
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Rounded.ContentCopy, "Copy User ID", tint = wefunColors.mutedText, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    IconButton(
                        onClick = onOpenAvatarEditor,
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, "Edit Profile", tint = wefunColors.mutedText)
                    }
                }

                // XP Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Level ${user.userLevel} XP", style = MaterialTheme.typography.labelSmall, color = TurquoiseActive, fontWeight = FontWeight.Bold)
                        Text("${user.xp} / ${user.nextLevelXp}", style = MaterialTheme.typography.labelSmall, color = wefunColors.mutedText)
                    }
                    LinearProgressIndicator(
                        progress = { (user.xp.toFloat() / user.nextLevelXp.toFloat()).coerceIn(0f, 1f) },
                        color = TurquoiseActive,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50))
                    )
                }

                // 4-Column Metric Row: Friends / Follow / Followers / Visitors
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileMetricColumn("${user.friendsCount}", "Friends") { activeDetail = ProfileMenuDetail.MY_FRIENDS }
                    ProfileMetricColumn("${user.followingCount}", "Follow") { activeDetail = ProfileMenuDetail.MY_FRIENDS }
                    ProfileMetricColumn("${user.followersCount}", "Followers") { activeDetail = ProfileMenuDetail.STATS }
                    ProfileMetricColumn("${user.visitorsCount}", "Visitors") { activeDetail = ProfileMenuDetail.VISITORS }
                }
            }
        }

        // Official WEFUN VIP Promotional Banner
        item {
            Surface(
                onClick = onOpenVip,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("profile_vip_tile"),
                shape = RoundedCornerShape(18.dp),
                color = CharcoalCard,
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF3A1D6E), Color(0xFF23163D))))
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WefunBrandLogo(
                            size = 40.dp,
                            cornerRadius = 11.dp,
                            showGoldBorder = true
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.WorkspacePremium, null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Text("WEFUN VIP", style = MaterialTheme.typography.titleSmall, color = GoldAccent, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(
                                text = "Open VIP and enjoy exclusive privileges",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.88f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF3DDA2)
                    ) {
                        Text(
                            text = "View",
                            style = MaterialTheme.typography.labelMedium,
                            color = DeepNavyHeading,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Dual Currency Row: Coins & Points/Diamonds (Reference Screenshot 5)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f).testTag("profile_wallet_tile"),
                    onClick = onOpenWallet
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Coins", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(GoldAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.MonetizationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = formatCompactCount(user.coins),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenWallet
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Points", style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(RoseNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Diamond, null, tint = Color.White, modifier = Modifier.size(15.dp))
                            }
                            Text(
                                text = formatCompactCount(user.diamonds),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Quick Access 4-Column Card: VIP Center, Shop, PLAY Show, My Home
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAccessTile("VIP Center", Icons.Rounded.AutoAwesome, GoldAmber, Color(0xFFFFF5DC), onOpenVip)
                    QuickAccessTile("Shop", Icons.Rounded.Storefront, RoseNeon, Color(0xFFFFEAF0)) {
                        activeDetail = ProfileMenuDetail.SHOP
                    }
                    QuickAccessTile("PLAY Show", Icons.Rounded.Face, PurplePrimary, Color(0xFFF1ECFF), onOpenAvatarEditor)
                    QuickAccessTile("My Home", Icons.Rounded.Home, TurquoiseActive, Color(0xFFE4FAF7)) {
                        activeDetail = ProfileMenuDetail.MY_HOME
                    }
                }
            }
        }

        // 14-Option Profile Menu List Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    menuItems.forEachIndexed { idx, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = entry.onClick)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .testTag("profile_menu_${entry.title.lowercase().replace(" ", "_")}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(entry.iconTint.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(entry.icon, contentDescription = entry.title, tint = entry.iconTint, modifier = Modifier.size(19.dp))
                                }
                                Text(entry.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (entry.subtitle != null) {
                                    Text(entry.subtitle, style = MaterialTheme.typography.bodySmall, color = wefunColors.mutedText)
                                }
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = wefunColors.mutedText, modifier = Modifier.size(18.dp))
                            }
                        }
                        if (idx < menuItems.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                                color = wefunColors.glassBorder
                            )
                        }
                    }
                }
            }
        }
    }

    ProfileDetailSheetHost(
        detail = activeDetail,
        user = user,
        settings = settings,
        posts = posts,
        followedUserIds = followedUserIds,
        onDismiss = { activeDetail = ProfileMenuDetail.NONE },
        onOpenWallet = onOpenWallet,
        onOpenVip = onOpenVip,
        onOpenAvatarEditor = onOpenAvatarEditor,
        onOpenNotifications = onOpenNotifications,
        onOpenAdminPanel = onOpenAdminPanel,
        onToggleDarkMode = onToggleDarkMode,
        onToggleParentalControl = onToggleParentalControl,
        onSelectLanguage = onSelectLanguage,
        onToggleFollow = onToggleFollow,
        onSignOut = onSignOut,
        onDeleteAccount = onDeleteAccount,
        onShowToast = onShowToast
    )
}

@Composable
private fun MiniCounterChip(text: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = tint
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ProfileMetricColumn(
    value: String,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = LocalWefunColors.current.mutedText)
    }
}

@Composable
private fun QuickAccessTile(
    label: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(23.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
