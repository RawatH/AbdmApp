package org.commcare.dalvik.domain.usecases

import org.commcare.dalvik.domain.repositories.AbdmRepository
import javax.inject.Inject

class GetAuthenticationMethodsUsecase @Inject constructor(private val repository: AbdmRepository) {
    fun execute(healthId: String) =
        repository.getAuthenticationMethods(healthId)
}