package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.Country
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CountryEntity : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var name: String? = null
    var niceName: String? = null
    var currencyCode: String? = null
    var iso3: String? = null
    var status: String? = null
    var phoneCode: Int? = null
    var lastUpdated: String? = null
    var currencyName: String? = null
    var iso: String = ""
    var numberCode: Int? = null
    var dateCreated: String? = null
    var code: Int? = null

    companion object {
        fun fromCountry(_country: Country?): CountryEntity? {
            return _country?.let { country ->
                val result = CountryEntity()
                result.id = country.id
                result.name = country.name
                result.niceName = country.nice_name
                result.currencyCode = country.currency_code
                result.iso3 = country.iso3
                result.status = country.status
                result.phoneCode = country.phone_code
                result.lastUpdated = country.last_updated
                result.currencyName = country.currency_name
                result.iso = country.iso
                result.numberCode = country.number_code
                result.dateCreated = country.date_created
                result.code = country.code
                result
            }
        }
    }

    fun toCountry() = Country(
            this.id,
            this.name,
            this.niceName,
            this.currencyCode,
            this.iso3,
            this.status,
            this.phoneCode,
            this.lastUpdated,
            this.currencyName,
            this.iso,
            this.numberCode,
            this.dateCreated,
            this.code
    )
}
