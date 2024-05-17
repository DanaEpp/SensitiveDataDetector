package com.danaepp.sensitivedatadetector

import kotlinx.serialization.Serializable

@Serializable
data class SensitiveDataResult(
    val entityType: String,
    val score: Double,
    val data: String
)