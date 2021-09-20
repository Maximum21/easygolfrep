package com.minhhop.core.data.datasource

import com.minhhop.core.domain.Contact
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage
@Deprecated("ContactDataSource", ReplaceWith(" use GeneralDataSource"))
interface ContactDataSource {
    suspend fun sync(contacts: List<Contact>,result: (VerificationMessage)->Unit,errorResponse: (ErrorResponse)->Unit)
    fun getContactFromPhone(): List<Contact>
}