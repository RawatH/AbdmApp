package org.commcare.dalvik.domain.model


class AbdmErrorModel() {
    lateinit var code: String
    lateinit var message: String
    lateinit var details: List<AbdmErrorDetail>
}

class AbdmErrorDetail() {
    lateinit var message: String
    lateinit var code: String
    var attribute: String? = null
}