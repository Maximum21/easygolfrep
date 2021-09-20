package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.User
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class UserEntity: RealmObject(){

    companion object{
        fun toMap(user: User): UserEntity {
            val userEntity = UserEntity()
            user.let {
                userEntity.id = it.id
                userEntity.email = it.email
                userEntity.firstName = it.first_name
                userEntity.lastName = it.last_name
                userEntity.gender = it.gender
                userEntity.countryCode = it.country_code
                userEntity.isPhoneNotification = it.phone_notification
                userEntity.isEmailNotification = it.email_notification
                userEntity.birthday = it.birthday
                userEntity.phoneNumber = it.phone_number
                userEntity.avatar = it.avatar
                userEntity.profileId = it.profile_id
                userEntity.title = it.title
                userEntity.isEnabled = it.enabled
                userEntity.company = it.company
                userEntity.countryId = it.country_id
                userEntity.countryEntity = CountryEntity.fromCountry(it.country)

                userEntity.googleId = it.google_id
                userEntity.facebookId = it.facebook_id
                userEntity.status = it.status
                userEntity.dateCreated = it.date_created
                userEntity.handicap = it.handicap
                userEntity.friends = it.friends
                userEntity.following = it.following
            }
            return userEntity
        }
    }

    @PrimaryKey
    var id: String = ""
    var handicap: Double? = null
    var friends: Int? = null
    var following: Int? = null
    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var gender: String? = null
    var countryCode: String? = null
    var profileId: String? = null
    var isPhoneNotification: Boolean? = null
    var isEmailNotification: Boolean? = null
    var birthday: String? = null
    var phoneNumber: String? = null
    var avatar: String? = null
    var title: String? = null
    var isEnabled: Boolean? = null
    var company: String? = null
    var countryId: String? = null
    @Ignore
    var countryEntity: CountryEntity? = null
    var googleId: String? = null
    var facebookId: String? = null
    var status: String? = null
    var dateCreated: String? = null

    @Deprecated("do not user it", ReplaceWith("user"))
    val fullName: String
        get() = "${this.firstName} ${this.lastName}"

    @Deprecated("do not user it", ReplaceWith("user"))
    val shortName: String
        get() = "${this.firstName?.substring(0,1)}.${this.lastName}"


    fun toUser():User = User(
            id,firstName,lastName,email,profileId,gender,countryCode,isPhoneNotification,isEmailNotification,birthday,phoneNumber,avatar,
            title,isEnabled,company,countryId,countryEntity?.toCountry(),googleId,facebookId,status,dateCreated,this.handicap,this.friends,this.following
    )

}
