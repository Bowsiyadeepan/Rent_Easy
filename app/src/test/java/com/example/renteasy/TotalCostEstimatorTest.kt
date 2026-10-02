package com.example.renteasy

import com.example.renteasy.data.model.Property
import com.example.renteasy.utils.TotalCostEstimator
import org.junit.Assert.assertEquals
import org.junit.Test

class TotalCostEstimatorTest {

    @Test
    fun testNormalCostCalculation() {
        val property = Property(
            propertyId = "test_1",
            title = "2BHK Indiranagar",
            rent = 25000.0,
            deposit = 50000.0,
            estimatedUtilityCost = 2500.0,
            estimatedMaintenanceCost = 2000.0
        )

        val breakdown = TotalCostEstimator.calculateBreakdown(property)

        assertEquals(25000.0, breakdown.monthlyRent, 0.001)
        assertEquals(2500.0, breakdown.estimatedUtilityCost, 0.001)
        assertEquals(2000.0, breakdown.estimatedMaintenanceCost, 0.001)
        assertEquals(29500.0, breakdown.totalMonthlyCost, 0.001)
        assertEquals(50000.0, breakdown.securityDeposit, 0.001)
        assertEquals(79500.0, breakdown.firstMonthTotalOutlay, 0.001) // 29500 + 50000
        assertEquals(404000.0, breakdown.annualEstimatedCost, 0.001) // (29500 * 12) + 50000
    }

    @Test
    fun testZeroValuesHandling() {
        val property = Property(
            propertyId = "test_zero",
            title = "Zero Rent Listing",
            rent = 0.0,
            deposit = 0.0,
            estimatedUtilityCost = 0.0,
            estimatedMaintenanceCost = 0.0
        )

        val breakdown = TotalCostEstimator.calculateBreakdown(property)

        assertEquals(0.0, breakdown.monthlyRent, 0.001)
        assertEquals(0.0, breakdown.totalMonthlyCost, 0.001)
        assertEquals(0.0, breakdown.firstMonthTotalOutlay, 0.001)
        assertEquals(0.0, breakdown.annualEstimatedCost, 0.001)
    }

    @Test
    fun testNegativeValuesClampedToZero() {
        val property = Property(
            propertyId = "test_neg",
            title = "Negative Inputs",
            rent = -5000.0,
            deposit = -10000.0,
            estimatedUtilityCost = -500.0,
            estimatedMaintenanceCost = -200.0
        )

        val breakdown = TotalCostEstimator.calculateBreakdown(property)

        assertEquals(0.0, breakdown.monthlyRent, 0.001)
        assertEquals(0.0, breakdown.estimatedUtilityCost, 0.001)
        assertEquals(0.0, breakdown.estimatedMaintenanceCost, 0.001)
        assertEquals(0.0, breakdown.totalMonthlyCost, 0.001)
        assertEquals(0.0, breakdown.securityDeposit, 0.001)
    }
}
