package org.commcare.dalvik.data.model.request

import com.google.gson.annotations.SerializedName

abstract class BaseModel

data class AadhaarOtpRequestModel(@SerializedName("aadhaar") val aadhaarNumber: String) : BaseModel()
data class MobileOtpRequestModel(@SerializedName("mobile_number") val mobileNUmber: String,val txn_id:String) : BaseModel()
data class VerifyOtpRequestModel(val txn_id:String,val otp:String)
