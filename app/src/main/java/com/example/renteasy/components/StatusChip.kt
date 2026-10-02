package com.example.renteasy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.renteasy.ui.theme.StatusApproved
import com.example.renteasy.ui.theme.StatusApprovedBg
import com.example.renteasy.ui.theme.StatusPending
import com.example.renteasy.ui.theme.StatusPendingBg
import com.example.renteasy.ui.theme.StatusRejected
import com.example.renteasy.ui.theme.StatusRejectedBg
import com.example.renteasy.ui.theme.StatusRented
import com.example.renteasy.ui.theme.StatusRentedBg
import com.example.renteasy.utils.Constants

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (textColor, bgColor) = when (status.uppercase()) {
        Constants.STATUS_APPROVED.uppercase(), Constants.REQUEST_ACCEPTED.uppercase() ->
            Pair(StatusApproved, StatusApprovedBg)
        Constants.STATUS_REJECTED.uppercase(), Constants.REQUEST_REJECTED.uppercase() ->
            Pair(StatusRejected, StatusRejectedBg)
        Constants.STATUS_RENTED.uppercase() ->
            Pair(StatusRented, StatusRentedBg)
        Constants.STATUS_PENDING.uppercase(), Constants.REQUEST_PENDING.uppercase() ->
            Pair(StatusPending, StatusPendingBg)
        else ->
            Pair(Color.Gray, Color.LightGray.copy(alpha = 0.2f))
    }

    val displayStatus = when (status.uppercase()) {
        Constants.STATUS_APPROVED.uppercase() -> "Approved"
        Constants.STATUS_PENDING.uppercase() -> "Pending Approval"
        Constants.STATUS_REJECTED.uppercase() -> "Rejected"
        Constants.STATUS_RENTED.uppercase() -> "Rented Out"
        Constants.REQUEST_ACCEPTED.uppercase() -> "Accepted"
        Constants.REQUEST_PENDING.uppercase() -> "Pending"
        Constants.REQUEST_REJECTED.uppercase() -> "Rejected"
        else -> status
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayStatus,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
