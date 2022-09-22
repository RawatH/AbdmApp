package org.commcare.dalvik.data.services

import com.google.gson.JsonObject
import org.commcare.dalvik.data.model.request.AadhaarOtpRequestModel
import org.commcare.dalvik.data.model.request.MobileOtpRequestModel
import org.commcare.dalvik.data.model.request.VerifyOtpRequestModel
import retrofit2.Response
import retrofit2.http.*

interface HqServices {

    @POST("generate_mobile_otp")
    suspend fun generateMobileOtp(@Body mobileModel: MobileOtpRequestModel):Response<JsonObject>

    @POST("generate_aadhaar_otp")
    suspend fun generateAadhaarOtp(@Body aadhaarModel: AadhaarOtpRequestModel):Response<JsonObject>

    @POST("verify_aadhaar_otp")
    suspend fun verifyAadhaarOtp(@Body verifyOtpRequestModel: VerifyOtpRequestModel):Response<JsonObject>

    @POST("verify_mobile_otp")
    suspend fun verifyMobileOtp(@Body verifyOtpRequestModel: VerifyOtpRequestModel):Response<JsonObject>

    @GET("get_auth_methods")
    suspend fun getAuthenticationMethods()

}