package org.commcare.dalvik.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.domain.model.*

interface AbdmRepository {
    fun generateMobileOtp(mobileModel: MobileOtpRequestModel):Flow<HqResponseModel>
    fun generateAadhaarOtp(aadhaarModel: AadhaarOtpRequestModel):Flow<HqResponseModel>
    fun verifyMobileOtp(verifyOtpRequestModel: VerifyOtpRequestModel):Flow<HqResponseModel>
    fun verifyAadhaarOtp(verifyOtpRequestModel: VerifyOtpRequestModel):Flow<HqResponseModel>
    fun getAuthenticationMethods(healthId:String):Flow<HqResponseModel>
    fun generateAuthOtp(generateAuthOtp: GenerateAuthOtpModel):Flow<HqResponseModel>
}