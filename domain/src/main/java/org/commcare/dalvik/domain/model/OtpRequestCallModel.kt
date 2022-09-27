package org.commcare.dalvik.domain.model

import timber.log.Timber

const val OTP_BLOCK_TIME = 15 * 60 * 1000

data class OtpRequestCallModel(
    val id: String,
    var counter: Int,
    var blockedTS: Long = System.currentTimeMillis()
) {

    fun isBlocked() = counter >= 4

    fun increaseOtpCounter() {
        counter += 1
        if(counter >= 4){
            blockedTS = System.currentTimeMillis()
            Timber.d("----- OTP STATE ------ Blocking $id ---- $blockedTS")
        }
    }

    fun tryUnBlocking() {
        if (isBlocked()) {
            val timeLeft = OTP_BLOCK_TIME - (System.currentTimeMillis() - blockedTS)
            if (timeLeft <= 0) {
                counter = 0
                blockedTS = System.currentTimeMillis()
            }
        }
    }

    fun unblock() {
        if(isBlocked()) {
            counter = 0
        }
    }

    fun getTimeLeftToUnblock(): String {
        val timeLeft = OTP_BLOCK_TIME - (System.currentTimeMillis() - blockedTS)
        val minutesLeft = (timeLeft / 1000) / 60
        val secondsLeft = (timeLeft / 1000) % 60
        return minutesLeft.toString() + "min : ${secondsLeft}sec"
    }
}