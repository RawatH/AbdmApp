package org.commcare.dalvik.domain.usecases

import org.commcare.dalvik.domain.repositories.AbdmRepository
import javax.inject.Inject


class RequestAadhaarOtpUsecase @Inject constructor() {

    suspend fun execute(aadhaarNumber: String, repository: AbdmRepository) {
        repository.generateAadhaarOtp(aadhaarNumber)
    }
}