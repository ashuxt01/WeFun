package com.example.ui.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.WefunAppSettings
import com.example.data.model.SocialPost
import com.example.data.model.UserProfile
import com.example.ui.profile.ProfileAndEcosystemScreen
import com.example.ui.theme.PrimaryBackground

@Composable
fun ProfileScreen(
    user: UserProfile = UserProfile(
        uid = "usr_nova_01",
        wefunId = "WF-804921",
        username = "ariavance",
        nickname = "Aria Vance",
        bio = "Music host, late-night storyteller & Ludo strategist."
    ),
    settings: WefunAppSettings = WefunAppSettings(),
    posts: List<SocialPost> = emptyList(),
    followedUserIds: Set<String> = emptySet(),
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleParentalControl: (Boolean) -> Unit = {},
    onSelectLanguage: (String) -> Unit = {},
    onToggleFollow: (String) -> Unit = {},
    onPickProfilePhoto: (String) -> Unit = {},
    onOpenAvatarEditor: () -> Unit = {},
    onOpenWallet: () -> Unit = {},
    onOpenVip: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenAdminPanel: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PrimaryBackground
    ) {
        ProfileAndEcosystemScreen(
            user = user,
            settings = settings,
            posts = posts,
            followedUserIds = followedUserIds,
            onToggleDarkMode = onToggleDarkMode,
            onToggleParentalControl = onToggleParentalControl,
            onSelectLanguage = onSelectLanguage,
            onToggleFollow = onToggleFollow,
            onPickProfilePhoto = onPickProfilePhoto,
            onOpenAvatarEditor = onOpenAvatarEditor,
            onOpenWallet = onOpenWallet,
            onOpenVip = onOpenVip,
            onOpenNotifications = onOpenNotifications,
            onOpenAdminPanel = onOpenAdminPanel,
            onSignOut = onSignOut,
            onDeleteAccount = onDeleteAccount,
            onShowToast = onShowToast
        )
    }
}
