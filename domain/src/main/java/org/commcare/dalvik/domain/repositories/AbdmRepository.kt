package org.commcare.dalvik.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.model.request.AadhaarModel
import org.commcare.dalvik.data.model.request.MobileModel
import org.commcare.dalvik.domain.model.HqResponseModel

interface AbdmRepository {
    suspend fun generateAbhaNumber()
    fun generateMobileOtp(mobileModel: MobileModel):Flow<HqResponseModel<String>>
    fun generateAadhaarOtp(aadhaarModel: AadhaarModel):Flow<HqResponseModel<String>>
    suspend fun verifyAbhaNumber()
    suspend fun verifyMobileOtp()
    suspend fun verifyAadhaarOtp()
}