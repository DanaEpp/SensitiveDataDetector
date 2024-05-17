package com.danaepp.sensitivedatadetector

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

import com.danaepp.sensitivedatadetector.SensitiveDataResult
import com.danaepp.sensitivedatadetector.PresideoRequest
import com.danaepp.sensitivedatadetector.PresideoResponse

class Presidio
{
    companion object {
        private const val PRESIDIO_ANALYZER_URL = "http://localhost:5001/analyze"
    }
    
    fun analyze(payload: String) : List<SensitiveDataResult>
    {
        val presidioRequest = PresideoRequest(payload)
    
        val client = OkHttpClient()

        val MEDIA_TYPE = "application/json".toMediaType()
        
        val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
        val requestBody = json.encodeToString(presidioRequest)
        
        val request = Request.Builder()
            .url(PRESIDIO_ANALYZER_URL)
            .post(requestBody.toRequestBody(MEDIA_TYPE))
            .header("Content-Type", "application/json")
            .build()
        
        val results: MutableList<SensitiveDataResult> = mutableListOf() 
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            
            val retData = response.body?.string() ?: ""
            try {
                if( retData != "" )
                {
                    val deserializedResults: List<PresideoResponse>? = json.decodeFromString(retData)
                    
                    if( deserializedResults != null )
                    {
                        for( r in deserializedResults ) {
                            results.add(
                                SensitiveDataResult(
                                    r.entity_type, 
                                    r.score, 
                                    payload.substring(r.start,r.end))
                            )
                        }
                    }
                    
                }
            }
            catch(e: SerializationException) {
                throw e
            }  
        }

        return results
    }
}