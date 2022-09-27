package org.commcare.dalvik.domain.model

data class OtpRequestCallModel(val id: String, var counter: Int , var blockedTS: Long = System.currentTimeMillis()) {

    fun isBlocked() = counter >= 4

    fun increaseOtpCounter(){
        counter += 1
    }
}