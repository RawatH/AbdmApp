package org.commcare.dalvik.domain.model

import com.google.gson.Gson
import com.google.gson.JsonObject

object LanguageManager {

    const val DEFAULT_TRANSLATIONS = "{\n" +
            "  \"meta\": {\n" +
            "    \"code\": \"EN\"\n" +
            "  },\n" +
            "  \"data\": {\n" +
            "    \"VERIFY\": \"Verify\",\n" +
            "    \"START_VERIFICATION\": \"Start Verification\",\n" +
            "    \"VERIFY_OTP\": \"Verify OTP\",\n" +
            "    \"RESEND_OTP\": \"Resend OTP\",\n" +
            "    \"GEN_OTP\": \"Generate OTP\",\n" +
            "    \"ENTER_ADHR_OTP\": \"Enter Aadhaar OTP\",\n" +
            "    \"ENTER_MOB_OTP\": \"Enter Mobile OTP\",\n" +
            "    \"BENF_ABHA_NUM\": \"Beneficiary ABHA Number\",\n" +
            "    \"BENF_MOB_NUM\": \"Beneficiary Mobile Number\",\n" +
            "    \"BENF_ADHR_NUM\": \"Beneficiary Aadhaar Number\",\n" +
            "    \"USE_ADHR_DATA_IN_COMMCARE\": \"Use Aadhaar data in Commcare\",\n" +
            "    \"ADHR_DATA\": \"Aadhaar Data\",\n" +
            "    \"ABHA_NUM\": \"ABHA Number\",\n" +
            "    \"RETURN\": \"Return\",\n" +
            "    \"STATUS\": \"Status\",\n" +
            "    \"VERIFICATION_STATUS\": \"Verification Status\",\n" +
            "    \"SEL_AUTH_METHOD\": \"Select auth method\",\n" +
            "    \"healthIdNumber\": \"HealthIdNumber\",\n" +
            "    \"name\": \"Name\",\n" +
            "    \"gender\": \"Gender\",\n" +
            "    \"yearOfBirth\": \"Yearofbirth\",\n" +
            "    \"monthOfBirth\": \"Monthofbirth\",\n" +
            "    \"dayOfBirth\": \"Dayofbirth\",\n" +
            "    \"firstName\": \"Firstname\",\n" +
            "    \"healthId\": \"HealthId\",\n" +
            "    \"lastName\": \"Lastname\",\n" +
            "    \"middleName\": \"Middlename\",\n" +
            "    \"stateCode\": \"Statecode\",\n" +
            "    \"districtCode\": \"Districtcode\",\n" +
            "    \"stateName\": \"Statename\",\n" +
            "    \"email\": \"Email\",\n" +
            "    \"kycPhoto\": \"KYCphoto\",\n" +
            "    \"profilePhoto\": \"Profilephoto\",\n" +
            "    \"mobile\": \"Mobile\",\n" +
            "    \"authMethods\": \"Authmethods\",\n" +
            "    \"pincode\": \"Pincode\",\n" +
            "    \"ABHA_VERIFICATION\": \"ABHA Verification\",\n" +
            "    \"ABHA_CREATION\": \"ABHA Creation\"\n" +
            "  }\n" +
            "}"

    lateinit var translationModel: TranslationModel


    fun getDefaultTranslation(key: String):String{
        return Gson().fromJson(DEFAULT_TRANSLATIONS,JsonObject::class.java).run {
             this.getAsJsonObject("data")?.get(key)?.let {
                 return it.asString
             }?:key
         }
    }
    fun getTranslatedValue(key: TranslationKey) = translationModel.getTranslatedString(key.name)

    fun getTranslatedValue(key: String) = translationModel.getTranslatedString(key)


    fun init() {
        translationModel =
            Gson().fromJson(DEFAULT_TRANSLATIONS, TranslationModel::class.java)
    }
}

enum class TranslationKey {
    GEN_OTP,
    RESEND_OTP,
    VERIFY,
    BENF_MOB_NUM,
    BENF_ADHR_NUM,
    BENF_ABHA_NUM,
    ENTER_ADHR_OTP,
    ENTER_MOB_OTP,
    START_VERIFICATION,
    SEL_AUTH_METHOD,
    VERIFICATION_STATUS,
    STATUS,
    RETURN,
    ABHA_NUM,
    ADHR_DATA,
    USE_ADHR_DATA_IN_COMMCARE,
    ABHA_VERIFICATION,
    ABHA_CREATION
}


