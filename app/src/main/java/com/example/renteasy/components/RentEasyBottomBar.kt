package com.example.renteasy.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.renteasy.navigation.Routes
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun RentEasyBottomBar(
    role: String,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = when (role) {
        Constants.ROLE_ADMIN -> listOf(
            BottomNavItem(Routes.ADMIN_DASHBOARD, "Overview", Icons.Default.Dashboard),
            BottomNavItem(Routes.ADMIN_APPROVALS, "Approvals", Icons.Default.CheckCircle),
            BottomNavItem(Routes.ADMIN_PROPERTIES, "Properties", Icons.Default.Apartment),
            BottomNavItem(Routes.ADMIN_REQUESTS, "Requests", Icons.Default.ListAlt),
            BottomNavItem(Routes.ADMIN_USERS, "Users", Icons.Default.People)
        )
        Constants.ROLE_OWNER -> listOf(
            BottomNavItem(Routes.OWNER_DASHBOARD, "Listings", Icons.Default.Home),
            BottomNavItem(Routes.OWNER_ADD_PROPERTY, "Add", Icons.Default.AddCircle),
            BottomNavItem(Routes.OWNER_REQUESTS, "Requests", Icons.Default.ListAlt),
            BottomNavItem(Routes.OWNER_PROFILE, "Profile", Icons.Default.Person)
        )
        else -> listOf(
            BottomNavItem(Routes.TENANT_HOME, "Explore", Icons.Default.Home),
            BottomNavItem(Routes.TENANT_WISHLIST, "Wishlist", Icons.Default.Favorite),
            BottomNavItem(Routes.TENANT_COMPARE, "Compare", Icons.Default.Compare),
            BottomNavItem(Routes.TENANT_REQUESTS, "Requests", Icons.Default.ListAlt),
            BottomNavItem(Routes.TENANT_PROFILE, "Profile", Icons.Default.Person)
        )
    }

    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
