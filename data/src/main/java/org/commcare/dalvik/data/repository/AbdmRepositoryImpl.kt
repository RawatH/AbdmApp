package org.commcare.dalvik.data.repository

import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.model.request.AadhaarOtpRequestModel
import org.commcare.dalvik.data.model.request.MobileOtpRequestModel
import org.commcare.dalvik.data.model.request.VerifyOtpRequestModel
import org.commcare.dalvik.data.network.safeApiCall
import org.commcare.dalvik.data.services.HqServices
import org.commcare.dalvik.domain.model.HqResponseModel
import org.commcare.dalvik.domain.repositories.AbdmRepository
import javax.inject.Inject

class AbdmRepositoryImpl @Inject constructor(val hqServices: HqServices) : AbdmRepository {

    private val TAG = "AbdmRepositoryImpl"

    override suspend fun generateAbhaNumber() {

    }

    override fun generateMobileOtp(mobileModel: MobileOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.generateMobileOtp(mobileModel)
        }


    override fun generateAadhaarOtp(aadhaarModel: AadhaarOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.generateAadhaarOtp(aadhaarModel)
        }


    override suspend fun verifyAbhaNumber() {
    }

    override fun verifyMobileOtp(verifyOtpRequestModel: VerifyOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.verifyMobileOtp(verifyOtpRequestModel)
        }

    override fun verifyAadhaarOtp(verifyOtpRequestModel: VerifyOtpRequestModel): Flow<HqResponseModel> =
        safeApiCall {
            hqServices.verifyAadhaarOtp(verifyOtpRequestModel)
        }


//    override  fun getTranslationData(langCode:String) =
//        safeApiCall {
//            hqServices.getTranslationData(NetworkUtil.getTranslationEndpoint(langCode))
//        }


}