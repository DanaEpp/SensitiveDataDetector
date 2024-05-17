package com.danaepp.sensitivedatadetector

import kotlinx.serialization.Serializable

@Serializable
data class PresideoRequest(
    val text: String, 
    val language: String = "en", 
    val score_threshold: Double = 0.75,
    val entities: List<String> = listOf(
        "EMAIL_ADDRESS", "IBAN Generic", "IP_ADDRESS",
        "PHONE_NUMBER", "LOCATION", "PERSON", "URL",
        "US_BANK_NUMBER", "US_DRIVER_LICENSE",
        "US_ITIN", "US_PASSPORT", "US_SSN" 
    ) 
)