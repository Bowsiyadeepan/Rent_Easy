package com.example.renteasy.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF2563EB)
val PrimaryDarkBlue = Color(0xFF1D4ED8)
val PrimaryLightBlue = Color(0xFF3B82F6)
val TealSecondary = Color(0xFF0D9488)
val TealDark = Color(0xFF0F766E)
val DeepBlue = Color(0xFF1E293B)
val SlateDark = Color(0xFF0F172A)
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceWhite = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE2E8F0)
val CardBorderLight = Color(0xFFF1F5F9)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val TextMuted = Color(0xFF94A3B8)

// Status Colors
val StatusPending = Color(0xFFEA580C)
val StatusPendingBg = Color(0xFFFFF7ED)
val StatusApproved = Color(0xFF16A34A)
val StatusApprovedBg = Color(0xFFF0FDF4)
val StatusRejected = Color(0xFFDC2626)
val StatusRejectedBg = Color(0xFFFEF2F2)
val StatusRented = Color(0xFF4F46E5)
val StatusRentedBg = Color(0xFFEEF2FF)

// Score Colors
val ScoreHigh = Color(0xFF16A34A)
val ScoreHighBg = Color(0xFFECFDF5)
val ScoreMedium = Color(0xFFD97706)
val ScoreMediumBg = Color(0xFFFFFBEB)
val ScoreLow = Color(0xFFDC2626)
val ScoreLowBg = Color(0xFFFEF2F2)

// Premium UI Gradients
val PrimaryGradient = Brush.horizontalGradient(
    listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
)

val HeroGradient = Brush.verticalGradient(
    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
)

val CardShineGradient = Brush.linearGradient(
    listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.7f))
)

val OceanGradient = Brush.horizontalGradient(
    listOf(Color(0xFF2563EB), Color(0xFF0D9488))
)
