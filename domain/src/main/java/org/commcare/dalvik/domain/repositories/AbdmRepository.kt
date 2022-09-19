package org.commcare.dalvik.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.model.request.AadhaarOtpRequestModel
import org.commcare.dalvik.data.model.request.MobileModel
import org.commcare.dalvik.domain.model.HqResponseModel
import org.json.JSONObject

interface AbdmRepository {
    suspend fun generateAbhaNumber()
    fun generateMobileOtp(mobileModel: MobileModel):Flow<HqResponseModel<String>>
    fun generateAadhaarOtp(aadhaarModel: AadhaarOtpRequestModel):Flow<HqResponseModel<String>>
    suspend fun verifyAbhaNumber()
    suspend fun verifyMobileOtp()
    suspend fun verifyAadhaarOtp()
}