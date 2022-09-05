package org.commcare.dalvik.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.domain.model.HqResponseModel

interface AbdmRepository {
    suspend fun generateAbhaNumber()
    fun generateMobileOtp(mobileNumber: String):Flow<HqResponseModel<String>>
    fun generateAadhaarOtp(aadharNumber: String):Flow<HqResponseModel<String>>
    suspend fun verifyAbhaNumber()
    suspend fun verifyMobileOtp()
    suspend fun verifyAadhaarOtp()
}