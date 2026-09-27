package com.example.billing

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object Security {

    const val MYKET_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqLMavRsKD9EyQbXEN21VrHdhzWtoYHwAwiRbpAuctaQkKGTD+sh5nEXw4t3xDpC/j8FMs9IK+UmSFNuK3osRFwl96x+fhxPhskHIu9nVgpOJoj7aiYeFeSs5uGw51FEPptOuYvGGZomL+AGPvBiHkqTMoBin8Jvj+feGA+M4kpQIDAQAB"

    fun verifyPurchase(base64PublicKey: String, signedData: String, signature: String): Boolean {
        if (signedData.isEmpty() || base64PublicKey.isEmpty() || signature.isEmpty()) {
            return false
        }
        val key = generatePublicKey(base64PublicKey) ?: return false
        return verify(key, signedData, signature)
    }

    private fun generatePublicKey(encodedPublicKey: String): PublicKey? {
        return try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance("RSA")
            keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (_: Exception) {
            null
        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        return try {
            val signatureAlgorithm = Signature.getInstance("SHA1withRSA")
            signatureAlgorithm.initVerify(publicKey)
            // ✅ Explicit UTF-8 charset — same one Myket uses to generate the signature
            signatureAlgorithm.update(signedData.toByteArray(Charsets.UTF_8))
            val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
            signatureAlgorithm.verify(signatureBytes)
        } catch (_: Exception) {
            false
        }
    }
}