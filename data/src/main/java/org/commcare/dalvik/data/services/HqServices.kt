package org.commcare.dalvik.data.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HqServices {

    @POST("/generate_mobile_otp")
    suspend fun generateMobileOtp(@Body mobileNumber:String):Response<String>

    @POST("/generate_aadhaar_otp")
    suspend fun generateAadhaarOtp(@Body aadhaarNumber:String):Response<String>

    @POST("/verify_aadhaar_otp")
    suspend fun verifyAadhaarOtp()

    @POST("/verify_mobile_otp")
    suspend fun verifyMobileOtp()

    @GET("/get_auth_methods")
    suspend fun getAuthenticationMethods()

}