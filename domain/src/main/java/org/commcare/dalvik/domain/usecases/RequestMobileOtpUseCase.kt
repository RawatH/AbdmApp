package org.commcare.dalvik.domain.usecases

import org.commcare.dalvik.domain.repositories.AbdmRepository
import javax.inject.Inject

class RequestMobileOtpUseCase @Inject constructor(val repository: AbdmRepository)   {

     fun execute(mobileNumber: String) =
        repository.generateMobileOtp(mobileNumber)

}