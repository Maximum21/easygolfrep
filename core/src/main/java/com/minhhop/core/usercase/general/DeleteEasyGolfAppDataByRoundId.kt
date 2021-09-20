package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository

class DeleteEasyGolfAppDataByRoundId(private val generalRepository: GeneralRepository) {
    operator fun invoke(roundId: String?) = generalRepository.deleteEasyGolfAppData(roundId)
}