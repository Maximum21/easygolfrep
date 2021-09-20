package com.minhhop.core.domain

data class Country (
    val id: String,
    val name: String?,
    val nice_name: String?,
    val currency_code: String?,
    val iso3: String?,
    val status: String?,
    val phone_code: Int?,
    val last_updated: String?,
    val currency_name: String?,
    val iso: String,
    val number_code: Int?,
    val date_created: String?,
    val code: Int?
)
