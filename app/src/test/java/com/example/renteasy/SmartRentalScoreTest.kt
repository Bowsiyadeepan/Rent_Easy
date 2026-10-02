package com.example.renteasy

import com.example.renteasy.data.model.Property
import com.example.renteasy.utils.SmartRentalScore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartRentalScoreTest {

    private val cityProperties = listOf(
        Property(
            propertyId = "p1",
            city = "Bengaluru",
            rent = 30000.0,
            deposit = 60000.0,
            bedrooms = 2,
            bathrooms = 2,
            estimatedUtilityCost = 2500.0,
            estimatedMaintenanceCost = 2000.0,
            amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Gym")
        ),
        Property(
            propertyId = "p2",
            city = "Bengaluru",
            rent = 50000.0,
            deposit = 120000.0,
            bedrooms = 3,
            bathrooms = 3,
            estimatedUtilityCost = 4000.0,
            estimatedMaintenanceCost = 3500.0,
            amenities = listOf("WiFi / Internet", "Power Backup")
        ),
        Property(
            propertyId = "p3",
            city = "Bengaluru",
            rent = 20000.0,
            deposit = 40000.0,
            bedrooms = 1,
            bathrooms = 1,
            estimatedUtilityCost = 1500.0,
            estimatedMaintenanceCost = 1000.0,
            amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking", "Gym", "Swimming Pool", "Balcony", "Furnished")
        )
    )

    @Test
    fun testNormalGoodValuePropertyScore() {
        // p3 is cheaper than the city average and has 8 amenities
        val p3 = cityProperties[2]
        val result = SmartRentalScore.calculate(p3, cityProperties)

        assertTrue("Total score should be >= 70 for an affordable high-amenity home", result.totalScore >= 70)
        assertTrue("Total score must be clamped <= 100", result.totalScore <= 100)
        assertTrue("Amenities score must be 20 for 8 amenities", result.amenitiesScore >= 19.9)
        assertTrue("Explanation must be present", result.explanation.isNotBlank())
    }

    @Test
    fun testSinglePropertyInCityEdgeCase() {
        val singleProp = Property(
            propertyId = "single_1",
            city = "Mysuru",
            rent = 18000.0,
            deposit = 36000.0,
            bedrooms = 2,
            bathrooms = 2,
            estimatedUtilityCost = 1500.0,
            estimatedMaintenanceCost = 1000.0,
            amenities = listOf("WiFi / Internet", "Power Backup")
        )

        val result = SmartRentalScore.calculate(singleProp, listOf(singleProp))

        assertTrue("Total score should be valid for single property", result.totalScore in 1..100)
        assertTrue("Affordability score should handle single property gracefully", result.affordabilityScore > 0)
        assertNotNull(result.explanation)
    }

    @Test
    fun testZeroRentEdgeCase() {
        val zeroRentProp = Property(
            propertyId = "zero_1",
            city = "Pune",
            rent = 0.0,
            deposit = 0.0,
            bedrooms = 1,
            bathrooms = 1,
            estimatedUtilityCost = 0.0,
            estimatedMaintenanceCost = 0.0,
            amenities = emptyList()
        )

        val result = SmartRentalScore.calculate(zeroRentProp, emptyList())

        assertTrue("Zero rent score must be clamped between 0 and 100", result.totalScore in 0..100)
        assertEquals(0.0, result.amenitiesScore, 0.001)
    }

    @Test
    fun testEmptyAmenitiesEdgeCase() {
        val noAmenitiesProp = Property(
            propertyId = "no_amenities",
            city = "Bengaluru",
            rent = 35000.0,
            deposit = 70000.0,
            bedrooms = 2,
            bathrooms = 2,
            amenities = emptyList()
        )

        val result = SmartRentalScore.calculate(noAmenitiesProp, cityProperties)

        assertEquals("Amenities score must be 0 for empty list", 0.0, result.amenitiesScore, 0.001)
        assertTrue("Total score must still be valid", result.totalScore in 0..100)
    }

    @Test
    fun testScoreClampingAtZeroAndHundred() {
        // High luxury property with very high deposit and hidden cost
        val expensiveProp = Property(
            propertyId = "exp_1",
            city = "Bengaluru",
            rent = 250000.0,
            deposit = 2500000.0, // 10x rent
            bedrooms = 1,
            bathrooms = 1,
            estimatedUtilityCost = 80000.0,
            estimatedMaintenanceCost = 70000.0,
            amenities = emptyList()
        )

        val resultLow = SmartRentalScore.calculate(expensiveProp, cityProperties)
        assertTrue("High cost property score should be low and clamped >= 0", resultLow.totalScore >= 0)

        // Super deal
        val superDeal = Property(
            propertyId = "deal_1",
            city = "Bengaluru",
            rent = 5000.0,
            deposit = 5000.0, // 1x rent
            bedrooms = 4,
            bathrooms = 4,
            estimatedUtilityCost = 200.0,
            estimatedMaintenanceCost = 100.0,
            amenities = (1..15).map { "Amenity $it" } // > 8 amenities
        )

        val resultHigh = SmartRentalScore.calculate(superDeal, cityProperties)
        assertTrue("Super deal should be clamped <= 100", resultHigh.totalScore <= 100)
    }
}
