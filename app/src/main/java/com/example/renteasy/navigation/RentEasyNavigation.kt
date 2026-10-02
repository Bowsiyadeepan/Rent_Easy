package com.example.renteasy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.renteasy.ui.admin.AdminApprovalsScreen
import com.example.renteasy.ui.admin.AdminDashboardScreen
import com.example.renteasy.ui.admin.AdminPropertiesScreen
import com.example.renteasy.ui.admin.AdminRequestsScreen
import com.example.renteasy.ui.admin.AdminUsersScreen
import com.example.renteasy.ui.auth.LoginScreen
import com.example.renteasy.ui.auth.RegisterScreen
import com.example.renteasy.ui.owner.AddEditPropertyScreen
import com.example.renteasy.ui.owner.OwnerDashboardScreen
import com.example.renteasy.ui.owner.OwnerRequestsScreen
import com.example.renteasy.ui.profile.ProfileScreen
import com.example.renteasy.ui.splash.SplashScreen
import com.example.renteasy.ui.tenant.ComparePropertiesScreen
import com.example.renteasy.ui.tenant.PropertyDetailScreen
import com.example.renteasy.ui.tenant.TenantHomeScreen
import com.example.renteasy.ui.tenant.TenantRequestsScreen
import com.example.renteasy.ui.tenant.WishlistScreen

@Composable
fun RentEasyNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // --- Splash & Auth ---
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = { targetHome ->
                    navController.navigate(targetHome) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { targetHome ->
                    navController.navigate(targetHome) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- Tenant Flow ---
        composable(Routes.TENANT_HOME) {
            TenantHomeScreen(
                onNavigateToDetail = { propertyId ->
                    navController.navigate(Routes.propertyDetail(propertyId))
                },
                onNavigateToCompare = {
                    navController.navigate(Routes.TENANT_COMPARE)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.PROPERTY_DETAIL,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCompare = { navController.navigate(Routes.TENANT_COMPARE) }
            )
        }

        composable(Routes.TENANT_WISHLIST) {
            WishlistScreen(
                onNavigateToDetail = { propertyId ->
                    navController.navigate(Routes.propertyDetail(propertyId))
                },
                onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                }
            )
        }

        composable(Routes.TENANT_COMPARE) {
            ComparePropertiesScreen(
                onNavigateToDetail = { propertyId ->
                    navController.navigate(Routes.propertyDetail(propertyId))
                },
                onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                }
            )
        }

        composable(Routes.TENANT_REQUESTS) {
            TenantRequestsScreen(
                onNavigateToDetail = { propertyId ->
                    navController.navigate(Routes.propertyDetail(propertyId))
                },
                onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                }
            )
        }

        composable(Routes.TENANT_PROFILE) {
            ProfileScreen(
                currentRoute = Routes.TENANT_PROFILE,
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- Owner Flow ---
        composable(Routes.OWNER_DASHBOARD) {
            OwnerDashboardScreen(
                onNavigateToAddProperty = { navController.navigate(Routes.OWNER_ADD_PROPERTY) },
                onNavigateToEditProperty = { propertyId ->
                    navController.navigate(Routes.ownerEditProperty(propertyId))
                },
                onNavigateToRequests = { navController.navigate(Routes.OWNER_REQUESTS) },
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.OWNER_ADD_PROPERTY) {
            AddEditPropertyScreen(
                propertyId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.OWNER_EDIT_PROPERTY,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            AddEditPropertyScreen(
                propertyId = propertyId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.OWNER_REQUESTS) {
            OwnerRequestsScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.OWNER_PROFILE) {
            ProfileScreen(
                currentRoute = Routes.OWNER_PROFILE,
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- Admin Flow ---
        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.ADMIN_APPROVALS) {
            AdminApprovalsScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.ADMIN_PROPERTIES) {
            AdminPropertiesScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.ADMIN_REQUESTS) {
            AdminRequestsScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.ADMIN_USERS) {
            AdminUsersScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

        composable(Routes.ADMIN_PROFILE) {
            ProfileScreen(
                currentRoute = Routes.ADMIN_PROFILE,
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
