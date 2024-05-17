package com.danaepp.sensitivedatadetector

import burp.api.montoya.BurpExtension
import burp.api.montoya.MontoyaApi

import com.danaepp.sensitivedatadetector.DetectorHttpResponseHandler

@Suppress("unused")
class SensitiveDataDetector : BurpExtension {
   override fun initialize(api: MontoyaApi?) {
        if (api == null) {
            return
        }

        api.extension().setName("Sensitive Data Detector")
        api.logging().logToOutput("Loading Sensitve Data Detector")

        api.proxy().registerResponseHandler(DetectorHttpResponseHandler(api))
    }
}