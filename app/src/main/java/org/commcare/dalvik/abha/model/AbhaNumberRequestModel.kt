package org.commcare.dalvik.abha.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import org.commcare.dalvik.abha.BR


data class AbhaNumberRequestModel(val mobileNumber: String) : BaseObservable() {

    var txnId: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.txnId)
        }

    var aadhaar: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.aadhaar)
        }


    var isMobileOtpVerified: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.mobileOtpVerified)
        }

    var isAadharOtpVerified: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.aadharOtpVerified)
        }

}