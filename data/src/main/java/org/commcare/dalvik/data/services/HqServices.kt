package org.commcare.dalvik.data.services

import com.google.gson.JsonObject
import org.commcare.dalvik.domain.model.AadhaarOtpRequestModel
import org.commcare.dalvik.domain.model.GenerateAuthOtpModel
import org.commcare.dalvik.domain.model.MobileOtpRequestModel
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
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

    @POST("generate_auth_otp")
    suspend fun generateAuthOtp(@Body generateAuthOtpModel: GenerateAuthOtpModel):Response<JsonObject>

    @GET("get_auth_methods")
    suspend fun getAuthenticationMethods(@Query("health_id") healthId:String):Response<JsonObject>

}