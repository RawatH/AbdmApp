package org.commcare.dalvik.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.model.request.AadhaarOtpRequestModel
import org.commcare.dalvik.data.model.request.MobileOtpRequestModel
import org.commcare.dalvik.data.model.request.VerifyOtpRequestModel
import org.commcare.dalvik.domain.model.HqResponseModel

interface AbdmRepository {
    suspend fun generateAbhaNumber()
    fun generateMobileOtp(mobileModel: MobileOtpRequestModel):Flow<HqResponseModel>
    fun generateAadhaarOtp(aadhaarModel: AadhaarOtpRequestModel):Flow<HqResponseModel>
    suspend fun verifyAbhaNumber()
    fun verifyMobileOtp(verifyOtpRequestModel: VerifyOtpRequestModel):Flow<HqResponseModel>
    fun verifyAadhaarOtp(verifyOtpRequestModel: VerifyOtpRequestModel):Flow<HqResponseModel>
}