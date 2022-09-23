package org.commcare.dalvik.data.repository

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.network.safeApiCall
import org.commcare.dalvik.data.services.HqServices
import org.commcare.dalvik.domain.model.*
import org.commcare.dalvik.domain.repositories.AbdmRepository
import javax.inject.Inject

class AbdmRepositoryImpl @Inject constructor(val hqServices: HqServices) : AbdmRepository {


    override fun generateMobileOtp(mobileModel: MobileOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.generateMobileOtp(mobileModel)
        }


    override fun generateAadhaarOtp(aadhaarModel: AadhaarOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.generateAadhaarOtp(aadhaarModel)
        }

    override fun getAuthenticationMethods(healthId:String): Flow<HqResponseModel>  =
        safeApiCall {
            hqServices.getAuthenticationMethods(healthId)
        }

    override fun generateAuthOtp(generateAuthOtp: GenerateAuthOtpModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.generateAuthOtp(generateAuthOtp)
        }


    override fun verifyMobileOtp(verifyOtpRequestModel: VerifyOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.verifyMobileOtp(verifyOtpRequestModel)
        }

    override fun verifyAadhaarOtp(verifyOtpRequestModel: VerifyOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.verifyAadhaarOtp(verifyOtpRequestModel)
        }

}