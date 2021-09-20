package com.minhhop.core.usercase.contact

import com.minhhop.core.data.repository.ContactRepository

class GetContactFromPhone(private val contactRepository: ContactRepository) {
    operator fun invoke() = contactRepository.getContactFromPhone()
}