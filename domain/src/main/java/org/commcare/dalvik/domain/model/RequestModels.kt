package org.commcare.dalvik.data.model.request

import com.google.gson.annotations.SerializedName

abstract class BaseModel

data class AadhaarOtpRequestModel(@SerializedName("aadhaar") val aadhaarNumber: String) : BaseModel()
data class MobileModel(@SerializedName("mobile") val mobileNUmber: String) : BaseModel()
