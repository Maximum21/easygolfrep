package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.ContactDataSource
import com.minhhop.core.domain.Contact
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class ContactRepository(private val contactDataSource: ContactDataSource) {
    suspend fun sync(contacts: List<Contact>,result: (VerificationMessage)->Unit,errorResponse: (ErrorResponse)->Unit) = contactDataSource.sync(contacts,result,errorResponse)

    fun getContactFromPhone(): List<Contact> = contactDataSource.getContactFromPhone()
}