package com.example.renteasy.utils

import com.example.renteasy.data.model.Property

object TotalCostEstimator {
    data class CostBreakdown(
        val monthlyRent: Double,
        val estimatedUtilityCost: Double,
        val estimatedMaintenanceCost: Double,
        val totalMonthlyCost: Double,
        val securityDeposit: Double,
        val firstMonthTotalOutlay: Double,
        val annualEstimatedCost: Double
    )

    fun calculateBreakdown(property: Property): CostBreakdown {
        val rent = property.rent.coerceAtLeast(0.0)
        val utility = property.estimatedUtilityCost.coerceAtLeast(0.0)
        val maintenance = property.estimatedMaintenanceCost.coerceAtLeast(0.0)
        val deposit = property.deposit.coerceAtLeast(0.0)

        val totalMonthly = rent + utility + maintenance
        val firstMonthOutlay = totalMonthly + deposit
        val annualCost = (totalMonthly * 12) + deposit

        return CostBreakdown(
            monthlyRent = rent,
            estimatedUtilityCost = utility,
            estimatedMaintenanceCost = maintenance,
            totalMonthlyCost = totalMonthly,
            securityDeposit = deposit,
            firstMonthTotalOutlay = firstMonthOutlay,
            annualEstimatedCost = annualCost
        )
    }
}
