package org.commcare.dalvik.domain.model


class AbdmErrorModel() {
    lateinit var code: String
    lateinit var message: String
    lateinit var details: List<AbdmErrorDetail>

    fun getActualMessage():String{
        return details[0].message
    }
    fun getAbdmErrorCode():String{
        return details[0].code
    }
}

class AbdmErrorDetail() {
    lateinit var message: String
    lateinit var code: String
    var attribute: String? = null
}

data class OtpResponseModel(val txnId:String)