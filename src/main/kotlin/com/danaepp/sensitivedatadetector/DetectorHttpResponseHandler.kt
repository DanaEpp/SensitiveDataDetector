package com.danaepp.sensitivedatadetector

import burp.api.montoya.MontoyaApi
import burp.api.montoya.proxy.http.ProxyResponseHandler
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction
import burp.api.montoya.proxy.http.InterceptedResponse
import burp.api.montoya.http.message.MimeType
import burp.api.montoya.core.Annotations
import burp.api.montoya.core.HighlightColor

import com.danaepp.sensitivedatadetector.Presidio
import com.danaepp.sensitivedatadetector.SensitiveDataResult

class DetectorHttpResponseHandler(private val api: MontoyaApi) : ProxyResponseHandler
{
    override fun handleResponseReceived(interceptedResponse: InterceptedResponse?): ProxyResponseReceivedAction {
        /* This should never happen */
        if (interceptedResponse == null) {
            api.logging().logToError("Null response received. Dropping message.")
            return ProxyResponseReceivedAction.drop()
        }

        /* Condition to determine if we should ignore this response */
        if(interceptedResponse.inferredMimeType() == MimeType.JSON)
        {
            val presidio = Presidio()
            val results: List<SensitiveDataResult> = presidio.analyze(interceptedResponse.bodyToString())
           
            if (results.size > 0 ) { 
                api.logging().logToOutput(
                    "Detected potentially sensitive data. Check proxy history for highlighted suspect entries.")
                                         
                var note: String = "Potentially sensitive data detected\n======\n"

                for( r in results ) {
                    note += "${r.entityType} (Score: ${r.score})\n${r.data}\n------\n"
                }

                val annotations = Annotations.annotations(note, HighlightColor.ORANGE)
      
                return ProxyResponseReceivedAction.continueWith(interceptedResponse, annotations)
            }
        }

        return ProxyResponseReceivedAction.continueWith(interceptedResponse)
    }

    override fun handleResponseToBeSent(interceptedResponse: InterceptedResponse?): ProxyResponseToBeSentAction {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse)
    }
}