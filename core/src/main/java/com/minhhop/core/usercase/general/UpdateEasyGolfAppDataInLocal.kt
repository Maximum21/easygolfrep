package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository
import com.minhhop.core.domain.EasyGolfApp

class UpdateEasyGolfAppDataInLocal(private val generalRepository: GeneralRepository) {
    operator fun invoke(data: EasyGolfApp) = generalRepository.updateEasyGolfAppData(data)
}