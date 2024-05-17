package com.danaepp.sensitivedatadetector

import kotlinx.serialization.Serializable

@Serializable
data class PresideoResponse(
    val start: Int,    
    val end: Int,
    val entity_type: String,
    val score: Double    
)