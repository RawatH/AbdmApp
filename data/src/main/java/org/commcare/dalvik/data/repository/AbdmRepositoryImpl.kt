package org.commcare.dalvik.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import org.commcare.dalvik.data.model.request.AadhaarModel
import org.commcare.dalvik.data.model.request.MobileModel
import org.commcare.dalvik.data.network.safeApiCall
import org.commcare.dalvik.data.services.HqServices
import org.commcare.dalvik.domain.model.HqResponseModel
import org.commcare.dalvik.domain.repositories.AbdmRepository
import java.lang.Exception

import javax.inject.Inject

class AbdmRepositoryImpl @Inject constructor(val hqServices: HqServices) : AbdmRepository {

    private  val TAG = "AbdmRepositoryImpl"

    override suspend fun generateAbhaNumber() {

    }

    override  fun generateMobileOtp(mobileModel: MobileModel): Flow<HqResponseModel<String>> =
        safeApiCall {
            hqServices.generateMobileOtp(mobileModel)
        }


    override  fun generateAadhaarOtp(aadhaarModel: AadhaarModel) =
        safeApiCall {
            hqServices.generateAadhaarOtp(aadhaarModel)
        }



    override suspend fun verifyAbhaNumber() {
    }

    override suspend fun verifyMobileOtp() {
        hqServices.verifyMobileOtp()
    }

    override suspend fun verifyAadhaarOtp() {
        hqServices.verifyAadhaarOtp()
    }


}