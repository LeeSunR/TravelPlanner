package com.leesunr.travelplanner.util

import android.util.Base64
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object JWT {
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String):JSONObject? {
        try {
            val split = JWTEncoded.split(".").toTypedArray()
            if (split.size==3)
                return JSONObject(
                    getJson(
                        split[1]
                    )
                )
            else
                return null
        } catch (e: UnsupportedEncodingException) {
            return null
        }
        return null
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charset.forName("utf-8"))
    }
}