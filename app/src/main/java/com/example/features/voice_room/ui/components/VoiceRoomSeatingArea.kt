package com.example.features.voice_room.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomMemberRoleEntry
import com.example.data.model.RoomRole
import com.example.data.model.RoomSeat
import com.example.ui.components.AvatarCircle
import com.example.ui.components.LevelBadge
import com.example.ui.components.LiveVoiceBars
import com.example.ui.components.RoleBadgePill
import com.example.ui.components.VipBadge
import com.example.ui.components.WefunGradientButton
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun VoiceRoomSeatingGrid(
    seats: List<RoomSeat>,
    currentUserSeatIndex: Int?,
    pendingMoveFromSeat: Int?,
    isDarkStage: Boolean,
    onSeatClick: (RoomSeat) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seatRows = remember(seats) { seats.chunked(4) }
    val gridHeight = if (seats.size <= 8) 186.dp else 232.dp
    val connectorColor = if (isDarkStage) Color.White.copy(alpha = 0.45f) else PrimaryPurple.copy(alpha = 0.4f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(gridHeight)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        seatRows.forEach { rowSeats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                val leftPair = rowSeats.take(2)
                val rightPair = rowSeats.drop(2)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    leftPair.forEachIndexed { idx, seat ->
                        VoiceMicSeatTile(
                            seat = seat,
                            isCurrentUser = currentUserSeatIndex == seat.seatIndex,
                            isPendingMoveTarget = pendingMoveFromSeat != null && seat.occupantId == null && !seat.isSeatLocked,
                            isDarkStage = isDarkStage,
                            onClick = { onSeatClick(seat) }
                        )
                        if (idx == 0 && leftPair.size > 1) {
                            VoiceWaveConnector(
                                isActive = leftPair.any { it.isSpeaking && !it.isMuted },
                                accent = connectorColor,
                                widthDp = 16
                            )
                        }
                    }
                }

                if (rightPair.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        rightPair.forEachIndexed { idx, seat ->
                            VoiceMicSeatTile(
                                seat = seat,
                                isCurrentUser = currentUserSeatIndex == seat.seatIndex,
                                isPendingMoveTarget = pendingMoveFromSeat != null && seat.occupantId == null && !seat.isSeatLocked,
                                isDarkStage = isDarkStage,
                                onClick = { onSeatClick(seat) }
                            )
                            if (idx == 0 && rightPair.size > 1) {
                                VoiceWaveConnector(
                                    isActive = rightPair.any { it.isSpeaking && !it.isMuted },
                                    accent = connectorColor,
                                    widthDp = 16
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceMicSeatTile(
    seat: RoomSeat,
    isCurrentUser: Boolean,
    isPendingMoveTarget: Boolean,
    isDarkStage: Boolean,
    onClick: () -> Unit,
) {
    val pulseTransition = rememberInfiniteTransition(label = "seatSpeak")
    val speakScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (seat.isSpeaking && !seat.isMuted) 1.1f else 1f,
        animationSpec = infiniteRepeatable(tween(520, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "speakScale"
    )
    val labelColor = if (isDarkStage) Color.White else PrimaryText

    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .testTag("mic_seat_${seat.seatIndex}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (seat.occupantId != null) {
            // OCCUPIED SEAT STATE
            Box(contentAlignment = Alignment.Center) {
                if (seat.isSpeaking && !seat.isMuted) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .scale(speakScale)
                            .clip(CircleShape)
                            .border(2.dp, PremiumGold.copy(alpha = 0.78f), CircleShape)
                    )
                }
                AvatarCircle(
                    initials = seat.occupantInitials,
                    accentColor = Color(seat.accentColorHex),
                    size = 50.dp,
                    vipLevel = seat.vipLevel,
                    isSpeaking = seat.isSpeaking && !seat.isMuted,
                    isVerified = seat.isCoHost
                )
                // User Level Pill at Top-Start
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PrimaryPurple,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${seat.userLevel}",
                        fontSize = 8.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                // VIP Crown / Pill at Top-End
                if (seat.vipLevel > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PremiumGold,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "V${seat.vipLevel}",
                            fontSize = 8.sp,
                            color = PrimaryText,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                // Mic Status Badge
                if (seat.isMuted || seat.isMicLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ErrorRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MicOff, "Mic muted", tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (seat.isSpeaking && !seat.isMuted) {
                    LiveVoiceBars(isActive = true, rmsLevel = seat.rmsLevel)
                }
                Text(
                    text = seat.occupantName ?: "Speaker",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) PremiumGold else labelColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                RoleBadgePill(role = seat.role, compact = true)
                if (seat.charmCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Diamond,
                            contentDescription = "Charm",
                            tint = PinkAccent,
                            modifier = Modifier.size(9.dp)
                        )
                        Text(
                            text = formatCompactCount(seat.charmCount),
                            fontSize = 8.sp,
                            color = PinkAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // EMPTY / LOCKED / RESERVED SEAT STATES
            val isReserved = seat.reservedForName != null
            val emptyBg = when {
                isPendingMoveTarget -> PrimaryPurple.copy(alpha = 0.35f)
                seat.isSeatLocked -> if (isDarkStage) Color.Black.copy(alpha = 0.38f) else Color(0xFFEEEFF5)
                isDarkStage -> Color.Black.copy(alpha = 0.25f)
                else -> Color.White.copy(alpha = 0.9f)
            }
            val emptyBorder = when {
                isPendingMoveTarget -> CyanPulse
                seat.isSeatLocked -> if (isDarkStage) Color.White.copy(alpha = 0.25f) else BorderColor
                isReserved -> PinkAccent
                else -> PremiumGold.copy(alpha = 0.88f)
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(emptyBg)
                    .border(
                        width = if (isPendingMoveTarget) 2.dp else 1.2.dp,
                        color = emptyBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        seat.isSeatLocked -> Icons.Rounded.Lock
                        isReserved -> Icons.Rounded.Star
                        else -> Icons.Rounded.Add
                    },
                    contentDescription = when {
                        seat.isSeatLocked -> "Locked seat ${seat.seatIndex + 1}"
                        isReserved -> "Reserved seat ${seat.seatIndex + 1}"
                        else -> "Open mic seat ${seat.seatIndex + 1}"
                    },
                    tint = when {
                        seat.isSeatLocked -> if (isDarkStage) Color.White.copy(alpha = 0.82f) else SecondaryText
                        isReserved -> PinkAccent
                        else -> if (isDarkStage) PremiumGold else PrimaryPurple
                    },
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = when {
                    seat.isSeatLocked -> "Locked"
                    isReserved -> "★ ${seat.reservedForName}"
                    else -> "Seat ${seat.seatIndex + 1}"
                },
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    isReserved -> PinkAccent
                    seat.isSeatLocked -> labelColor.copy(alpha = 0.55f)
                    else -> labelColor.copy(alpha = 0.65f)
                },
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceSeatActionSheet(
    seat: RoomSeat,
    currentUserId: String,
    currentUserSeatIndex: Int?,
    currentUserRole: RoomRole,
    memberRoles: List<RoomMemberRoleEntry>,
    onJoinSeat: (Int) -> Unit,
    onLeaveSeat: (Int) -> Unit,
    onSendGift: () -> Unit,
    onAssignUserToSeat: (Int, String, String, Int) -> Unit,
    onReserveSeat: (Int, String?) -> Unit,
    onToggleOccupantMute: (Int) -> Unit,
    onToggleMicLock: (Int) -> Unit,
    onToggleSeatLock: (Int) -> Unit,
    onStartMoveSeat: (Int) -> Unit,
    onToggleCoHost: (Int) -> Unit,
    onRemoveFromSeat: (Int) -> Unit,
    onTransferHost: (Int) -> Unit,
    onAssignPartner: (String, String) -> Unit,
    onBlockUser: (String, String) -> Unit,
    onReportUser: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var reserveInput by remember(seat.seatIndex) { mutableStateOf(seat.reservedForName ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (seat.occupantId != null) {
                // OCCUPIED SEAT PROFILE & ACTIONS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarCircle(
                        initials = seat.occupantInitials,
                        accentColor = Color(seat.accentColorHex),
                        size = 54.dp,
                        vipLevel = seat.vipLevel,
                        isSpeaking = seat.isSpeaking && !seat.isMuted,
                        isVerified = seat.isCoHost
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = seat.occupantName ?: "Speaker",
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryText
                            )
                            RoleBadgePill(role = seat.role)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LevelBadge(level = seat.userLevel)
                            VipBadge(vipLevel = seat.vipLevel)
                            Text(
                                text = "Charm: ${formatCompactCount(seat.charmCount)} 💎",
                                style = MaterialTheme.typography.labelSmall,
                                color = PinkAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WefunGradientButton(
                        text = "Send Gift",
                        onClick = { onDismiss(); onSendGift() },
                        modifier = Modifier.weight(1f)
                    )
                    if (currentUserSeatIndex == seat.seatIndex) {
                        OutlinedButton(
                            onClick = { onDismiss(); onLeaveSeat(seat.seatIndex) },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) { Text("Leave Seat") }
                    }
                }
            } else {
                // EMPTY / LOCKED / RESERVED SEAT ACTIONS
                Text(
                    text = "Seat #${seat.seatIndex + 1} • ${
                        when {
                            seat.isSeatLocked -> "Locked"
                            seat.reservedForName != null -> "Reserved for ${seat.reservedForName}"
                            else -> "Open Mic Seat"
                        }
                    }",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText
                )
                WefunGradientButton(
                    text = if (currentUserRole.canModerate()) "Take Seat #${seat.seatIndex + 1}" else "Join / Request Mic",
                    onClick = { onDismiss(); onJoinSeat(seat.seatIndex) }
                )
            }

            if (currentUserRole.canModerate()) {
                Text(
                    text = "Owner & Admin Seat Controls",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (seat.occupantId != null) {
                        FilterChip(
                            selected = seat.isMuted,
                            onClick = { onToggleOccupantMute(seat.seatIndex); onDismiss() },
                            label = { Text(if (seat.isMuted) "Unmute" else "Mute") }
                        )
                        FilterChip(
                            selected = false,
                            onClick = { onStartMoveSeat(seat.seatIndex); onDismiss() },
                            label = { Text("Move Seat") }
                        )
                        FilterChip(
                            selected = false,
                            onClick = { onRemoveFromSeat(seat.seatIndex); onDismiss() },
                            label = { Text("Remove") }
                        )
                    }
                    FilterChip(
                        selected = seat.isMicLocked,
                        onClick = { onToggleMicLock(seat.seatIndex); onDismiss() },
                        label = { Text(if (seat.isMicLocked) "Unlock Mic" else "Lock Mic") }
                    )
                    FilterChip(
                        selected = seat.isSeatLocked,
                        onClick = { onToggleSeatLock(seat.seatIndex); onDismiss() },
                        label = { Text(if (seat.isSeatLocked) "Unlock Seat" else "Lock Seat") }
                    )
                    if (currentUserRole.canManageRoles() && seat.occupantId != null) {
                        FilterChip(
                            selected = seat.isCoHost,
                            onClick = { onToggleCoHost(seat.seatIndex); onDismiss() },
                            label = { Text(if (seat.isCoHost) "Revoke Co-Host" else "Make Co-Host") }
                        )
                    }
                    if (currentUserRole == RoomRole.OWNER && seat.occupantId != null && seat.occupantName != null) {
                        FilterChip(
                            selected = false,
                            onClick = { onAssignPartner(seat.occupantId, seat.occupantName); onDismiss() },
                            label = { Text("Set Partner") }
                        )
                        FilterChip(
                            selected = false,
                            onClick = { onTransferHost(seat.seatIndex); onDismiss() },
                            label = { Text("Transfer Ownership") }
                        )
                    }
                }

                if (seat.occupantId == null) {
                    Text(
                        text = "Invite / Assign Member to Seat",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        memberRoles.filter { it.role != RoomRole.OWNER }.take(4).forEach { member ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    onAssignUserToSeat(seat.seatIndex, member.userId, member.displayName, member.vipLevel)
                                    onDismiss()
                                },
                                label = { Text("Invite ${member.displayName.substringBefore(" ")}") }
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = reserveInput,
                            onValueChange = { reserveInput = it },
                            label = { Text("Reserve for nickname") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = {
                            onReserveSeat(seat.seatIndex, reserveInput.ifBlank { null })
                            onDismiss()
                        }) {
                            Text(if (reserveInput.isBlank()) "Clear" else "Reserve")
                        }
                    }
                }
            }

            if (seat.occupantId != null && seat.occupantId != currentUserId) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(onClick = {
                        onBlockUser(seat.occupantId, seat.occupantName ?: "Speaker")
                        onDismiss()
                    }) { Text("Block User", color = ErrorRed) }
                    TextButton(onClick = {
                        onReportUser(seat.occupantName ?: "Speaker")
                        onDismiss()
                    }) { Text("Report User", color = ErrorRed) }
                }
            }
        }
    }
}
