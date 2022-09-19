package org.commcare.dalvik.domain.model

import com.google.gson.Gson

object LanguageManager {

    val DEFAULT_TRANSLATIONS = "{\n" +
            "\t\"meta\": {\n" +
            "\t\t\"code\": \"EN\"\n" +
            "\t},\n" +
            "\t\"data\": {\n" +
            "\t\t\"VERIFY\": \"Verify-1\",\n" +
            "\t\t\"START_VERIFICATION\": \"Start Verification\",\n" +
            "\t\t\"VERIFY_OTP\": \"Verify OTP\",\n" +
            "\t\t\"RESEND_OTP\": \"Resend OTP\",\n" +
            "\t\t\"GEN_OTP\": \"Generate OTP\",\n" +
            "\t\t\"ENTER_ADHR_OTP\": \"Enter Aadhaar OTP\",\n" +
            "\t\t\"BENF_MOB_NUM\": \"Beneficiary Mobile Number\",\n" +
            "\t\t\"BENF_ADHR_NUM\": \"Beneficiary Aadhaar Number\",\n" +
            "\t\t\"USE_ADHR_DATA_IN_COMMCARE\": \"Use Aadhaar data in Commcare\",\n" +
            "\t\t\"ADHR_DATA\": \"Aadhaar Data\",\n" +
            "\t\t\"ABHA_NUM\": \"ABHA Number\",\n" +
            "\t\t\"RETURN\": \"Return\",\n" +
            "\t\t\"STATUS\": \"Status\",\n" +
            "\t\t\"VERIFICATION_STATUS\": \"Verification Status\",\n" +
            "\t\t\"SEL_AUTH_METHOD\": \"Select auth method\"\n" +
            "\t}\n" +
            "}"

    lateinit var translationModel: TranslationModel


    fun getTranslatedValue(key: TranslationKey) = translationModel.getTranslatedString(key.name)

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
    ENTER_ADHR_OTP,
    START_VERIFICATION,
    SEL_AUTH_METHOD,
    VERIFICATION_STATUS,
    STATUS,
    RETURN,
    ABHA_NUM,
    ADHR_DATA,
    USE_ADHR_DATA_IN_COMMCARE


}

enum class LanguageCode(val code: String){
    EN("en") ,
    HI ("hi");
}

