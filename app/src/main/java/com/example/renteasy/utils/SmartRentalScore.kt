package com.example.renteasy.utils

import com.example.renteasy.data.model.Property
import kotlin.math.roundToInt

object SmartRentalScore {

    data class ScoreResult(
        val totalScore: Int, // 0 to 100 clamped
        val affordabilityScore: Double, // max 35
        val spaceScore: Double, // max 20
        val amenitiesScore: Double, // max 20
        val depositScore: Double, // max 10
        val hiddenCostScore: Double, // max 15
        val explanation: String,
        val priceDiffPercentage: Double
    )

    fun calculate(
        property: Property,
        allPropertiesInCity: List<Property>
    ): ScoreResult {
        val totalMonthlyCost = property.rent + property.estimatedUtilityCost + property.estimatedMaintenanceCost

        // 1. Affordability Component (35%)
        val validCityProperties = allPropertiesInCity.filter { it.rent > 0 }
        val cityAverageCost = if (validCityProperties.isNotEmpty()) {
            validCityProperties.map { it.rent + it.estimatedUtilityCost + it.estimatedMaintenanceCost }.average()
        } else {
            totalMonthlyCost
        }

        val priceDiffPercentage: Double = if (cityAverageCost > 0 && totalMonthlyCost > 0) {
            ((cityAverageCost - totalMonthlyCost) / cityAverageCost) * 100.0
        } else {
            0.0
        }

        val affordabilityScore: Double = if (property.rent <= 0) {
            35.0
        } else if (validCityProperties.size <= 1 || cityAverageCost <= 0) {
            // Edge case: Only one property in city
            if (totalMonthlyCost <= 25000) 28.0 else if (totalMonthlyCost <= 45000) 24.0 else 20.0
        } else {
            // Scale based on priceDiffPercentage:
            // +30% cheaper -> 35 pts
            // 0% (exact average) -> 22 pts
            // -30% (30% more expensive) -> 10 pts
            // -60% -> 0 pts
            val raw = 22.0 + (priceDiffPercentage * (13.0 / 30.0))
            raw.coerceIn(0.0, 35.0)
        }

        // 2. Space Component (20%)
        val totalRooms = (property.bedrooms.coerceAtLeast(1) * 1.0) + (property.bathrooms.coerceAtLeast(1) * 0.5)
        val spaceScore: Double = if (property.rent <= 0) {
            20.0
        } else {
            val costPerRoomUnit = totalMonthlyCost / totalRooms
            // Typical room unit cost ~ 15000. Lower cost per room = higher score.
            val rawSpace = (20.0 - (costPerRoomUnit / 25000.0) * 10.0) + (property.bedrooms * 2.0)
            rawSpace.coerceIn(0.0, 20.0)
        }

        // 3. Amenities Component (20%)
        // Cap at 8 amenities
        val maxCapAmenities = 8
        val validAmenitiesCount = property.amenities.size.coerceAtMost(maxCapAmenities)
        val amenitiesScore: Double = ((validAmenitiesCount.toDouble() / maxCapAmenities.toDouble()) * 20.0).coerceIn(0.0, 20.0)

        // 4. Deposit Friendliness Component (10%)
        val depositRatio = if (property.rent > 0) property.deposit / property.rent else 0.0
        val depositScore: Double = if (property.rent <= 0 || property.deposit <= 0) {
            10.0
        } else if (depositRatio <= 1.0) {
            10.0
        } else {
            (10.0 - ((depositRatio - 1.0) * 1.5)).coerceIn(0.0, 10.0)
        }

        // 5. Hidden-Cost Ratio Component (15%)
        val hiddenCosts = property.estimatedUtilityCost + property.estimatedMaintenanceCost
        val hiddenCostRatio = if (property.rent > 0) hiddenCosts / property.rent else 0.0
        val hiddenCostScore: Double = if (property.rent <= 0) {
            15.0
        } else {
            (15.0 - ((hiddenCostRatio / 0.40) * 15.0)).coerceIn(0.0, 15.0)
        }

        // Total Clamped Score
        val totalRaw = affordabilityScore + spaceScore + amenitiesScore + depositScore + hiddenCostScore
        val finalScore = totalRaw.roundToInt().coerceIn(0, 100)

        // Generate explainable summary
        val explanation = buildExplanation(
            finalScore = finalScore,
            priceDiffPercentage = priceDiffPercentage,
            city = property.city,
            amenitiesCount = property.amenities.size,
            depositRatio = depositRatio,
            isSingleProperty = validCityProperties.size <= 1
        )

        return ScoreResult(
            totalScore = finalScore,
            affordabilityScore = affordabilityScore,
            spaceScore = spaceScore,
            amenitiesScore = amenitiesScore,
            depositScore = depositScore,
            hiddenCostScore = hiddenCostScore,
            explanation = explanation,
            priceDiffPercentage = priceDiffPercentage
        )
    }

    private fun buildExplanation(
        finalScore: Int,
        priceDiffPercentage: Double,
        city: String,
        amenitiesCount: Int,
        depositRatio: Double,
        isSingleProperty: Boolean
    ): String {
        return when {
            finalScore >= 80 -> {
                if (!isSingleProperty && priceDiffPercentage > 5.0) {
                    "Exceptional deal: ${priceDiffPercentage.roundToInt()}% cheaper than $city average with $amenitiesCount amenities"
                } else {
                    "Excellent overall value with low hidden costs and rich amenities"
                }
            }
            finalScore >= 65 -> {
                if (!isSingleProperty && priceDiffPercentage > 0.0) {
                    "Great value: ${priceDiffPercentage.roundToInt()}% cheaper than $city average"
                } else if (depositRatio <= 2.0) {
                    "Good value with tenant-friendly security deposit"
                } else {
                    "Balanced pricing with good space and essential amenities"
                }
            }
            finalScore >= 45 -> {
                if (!isSingleProperty && priceDiffPercentage < -10.0) {
                    "Fair listing: ${(-priceDiffPercentage).roundToInt()}% above $city average due to premium amenities"
                } else {
                    "Moderate value: consider total utility and deposit requirements"
                }
            }
            else -> {
                "Premium or high-maintenance listing relative to typical market rates"
            }
        }
    }
}
