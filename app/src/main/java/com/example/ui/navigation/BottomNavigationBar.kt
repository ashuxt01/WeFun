package com.example.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onSelectDestination: (WefunDestination) -> Unit,
    modifier: Modifier = Modifier,
    unreadChatsCount: Int = 0,
    unreadNotificationsCount: Int = 0,
    showVipGoldIndicator: Boolean = true,
) {
    val barSurface = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(barSurface)
    ) {
        HorizontalDivider(thickness = 1.dp, color = borderColor)
        NavigationBar(
            containerColor = barSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets.navigationBars,
        ) {
            WefunDestination.entries.forEach { destination ->
                val isSelected = currentRoute == destination.route
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) activeColor else inactiveColor,
                    animationSpec = tween(durationMillis = 220),
                    label = "nav_icon_tint_${destination.route}"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) activeColor else inactiveColor,
                    animationSpec = tween(durationMillis = 220),
                    label = "nav_label_color_${destination.route}"
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.12f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "nav_icon_scale_${destination.route}"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectDestination(destination) },
                    modifier = Modifier
                        .testTag("nav_tab_${destination.route}")
                        .semantics { selected = isSelected },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = activeColor,
                        selectedTextColor = activeColor,
                        unselectedIconColor = inactiveColor,
                        unselectedTextColor = inactiveColor,
                        indicatorColor = PrimaryPurple.copy(alpha = 0.12f)
                    ),
                    icon = {
                        DestinationTabIcon(
                            destination = destination,
                            selected = isSelected,
                            tint = iconTint,
                            scale = iconScale,
                            unreadChatsCount = unreadChatsCount,
                            unreadNotificationsCount = unreadNotificationsCount,
                            showVipGoldIndicator = showVipGoldIndicator,
                            barSurface = barSurface,
                        )
                    },
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = destination.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = labelColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .height(2.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(PremiumGold)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WefunNavigationRail(
    currentRoute: String?,
    onSelectDestination: (WefunDestination) -> Unit,
    modifier: Modifier = Modifier,
    unreadChatsCount: Int = 0,
    unreadNotificationsCount: Int = 0,
    showVipGoldIndicator: Boolean = true,
) {
    val barSurface = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

    NavigationRail(
        containerColor = barSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxHeight()
            .border(width = 1.dp, color = borderColor)
    ) {
        Spacer(modifier = Modifier.weight(0.2f))
        WefunDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val iconTint by animateColorAsState(
                targetValue = if (isSelected) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = 220),
                label = "rail_icon_tint_${destination.route}"
            )
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.12f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "rail_icon_scale_${destination.route}"
            )
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectDestination(destination) },
                modifier = Modifier
                    .testTag("nav_rail_${destination.route}")
                    .semantics { selected = isSelected },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = activeColor,
                    selectedTextColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    unselectedTextColor = inactiveColor,
                    indicatorColor = PrimaryPurple.copy(alpha = 0.12f)
                ),
                icon = {
                    DestinationTabIcon(
                        destination = destination,
                        selected = isSelected,
                        tint = iconTint,
                        scale = iconScale,
                        unreadChatsCount = unreadChatsCount,
                        unreadNotificationsCount = unreadNotificationsCount,
                        showVipGoldIndicator = showVipGoldIndicator,
                        barSurface = barSurface,
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DestinationTabIcon(
    destination: WefunDestination,
    selected: Boolean,
    tint: Color,
    scale: Float,
    unreadChatsCount: Int,
    unreadNotificationsCount: Int,
    showVipGoldIndicator: Boolean,
    barSurface: Color,
) {
    val iconVector: ImageVector = when (destination) {
        WefunDestination.Home -> if (selected) Icons.Rounded.SportsEsports else Icons.Outlined.SportsEsports
        WefunDestination.Party -> if (selected) Icons.Rounded.Mic else Icons.Outlined.MicNone
        WefunDestination.Chats -> if (selected) Icons.Rounded.ChatBubble else Icons.Outlined.ChatBubbleOutline
        WefunDestination.Discover -> if (selected) Icons.Rounded.Explore else Icons.Outlined.Explore
        WefunDestination.Profile -> if (selected) Icons.Rounded.Person else Icons.Outlined.PersonOutline
    }

    val iconModifier = Modifier
        .size(24.dp)
        .scale(scale)

    when {
        destination == WefunDestination.Chats && unreadChatsCount > 0 -> {
            BadgedBox(
                badge = {
                    Badge(containerColor = PinkAccent, contentColor = Color.White) {
                        Text(
                            text = if (unreadChatsCount > 99) "99+" else "$unreadChatsCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                Icon(imageVector = iconVector, contentDescription = destination.label, tint = tint, modifier = iconModifier)
            }
        }
        destination == WefunDestination.Profile && (unreadNotificationsCount > 0 || showVipGoldIndicator) -> {
            Box {
                Icon(imageVector = iconVector, contentDescription = destination.label, tint = tint, modifier = iconModifier)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-1).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (unreadNotificationsCount > 0) PinkAccent else PremiumGold)
                        .border(1.dp, barSurface, CircleShape)
                )
            }
        }
        else -> {
            Icon(imageVector = iconVector, contentDescription = destination.label, tint = tint, modifier = iconModifier)
        }
    }
}
