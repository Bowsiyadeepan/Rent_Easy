package com.example.renteasy.navigation

object Routes {
    // Auth & Launch
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Tenant
    const val TENANT_HOME = "tenant_home"
    const val TENANT_WISHLIST = "tenant_wishlist"
    const val TENANT_REQUESTS = "tenant_requests"
    const val TENANT_COMPARE = "tenant_compare"
    const val TENANT_PROFILE = "tenant_profile"
    const val PROPERTY_DETAIL = "property_detail/{propertyId}"

    fun propertyDetail(propertyId: String): String = "property_detail/$propertyId"

    // Owner
    const val OWNER_DASHBOARD = "owner_dashboard"
    const val OWNER_ADD_PROPERTY = "owner_add_property"
    const val OWNER_EDIT_PROPERTY = "owner_edit_property/{propertyId}"
    const val OWNER_REQUESTS = "owner_requests"
    const val OWNER_PROFILE = "owner_profile"

    fun ownerEditProperty(propertyId: String): String = "owner_edit_property/$propertyId"

    // Admin
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_APPROVALS = "admin_approvals"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_PROPERTIES = "admin_properties"
    const val ADMIN_REQUESTS = "admin_requests"
    const val ADMIN_PROFILE = "admin_profile"
}
