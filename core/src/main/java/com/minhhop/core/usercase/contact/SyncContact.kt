package com.minhhop.core.usercase.contact

import com.minhhop.core.data.repository.ContactRepository
import com.minhhop.core.domain.Contact
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class SyncContact(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contacts: List<Contact>,result: (VerificationMessage)->Unit, errorResponse: (ErrorResponse)->Unit)
            = contactRepository.sync(contacts,result,errorResponse)
}