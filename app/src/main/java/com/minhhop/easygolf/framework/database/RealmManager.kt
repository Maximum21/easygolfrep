package com.minhhop.easygolf.framework.database

import android.util.Log
import com.google.gson.Gson
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.Hole
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.framework.models.entity.*
import io.realm.Case
import io.realm.ImportFlag
import io.realm.Realm
import io.realm.Sort
import java.util.*

class RealmManager {

    fun saveCountries(countries: List<Country>, onComplete: () -> Unit) {
        val countryEntities = countries.map {
            CountryEntity.fromCountry(it)
        }
        Realm.getDefaultInstance().use { realmInstance ->
            realmInstance.executeTransaction {
                it.copyToRealmOrUpdate(countryEntities)
                onComplete()
            }
        }
    }

    fun getCountries(keyword: String? = null): List<Country>? {
        Realm.getDefaultInstance().use { realm ->
            val realmQuery = if (keyword.isNullOrEmpty()) {
                realm.where(CountryEntity::class.java)
            } else {
                realm.where(CountryEntity::class.java).contains(CountryEntity::niceName.name, keyword, Case.INSENSITIVE)
            }
            val result = realmQuery.findAll()?.map {
                it.toCountry()
            }
            return if (result.isNullOrEmpty()) {
                null
            } else result
        }
    }

    fun getDefaultCountry(iso: String?): Country? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(CountryEntity::class.java).equalTo(CountryEntity::iso.name, iso?.toUpperCase(Locale.getDefault())).findFirst()?.toCountry()
                    ?: realm.where(CountryEntity::class.java).findFirst()?.toCountry()
        }
    }

    fun insertOrUpdateProfileUser(user: User, onComplete: (User) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.executeTransaction { realm ->
                    realm.where(UserEntity::class.java).equalTo(UserEntity::id.name, user.id).findFirst()?.let { userEntity ->
                        userEntity.firstName = user.first_name ?: userEntity.firstName
                        userEntity.lastName = user.last_name ?: userEntity.lastName
                        userEntity.email = user.email ?: userEntity.email
                        userEntity.profileId = user.profile_id ?: userEntity.profileId
                        userEntity.gender = user.gender ?: userEntity.gender
                        userEntity.countryCode = user.country_code ?: userEntity.countryCode
                        userEntity.isPhoneNotification = user.phone_notification
                                ?: userEntity.isPhoneNotification
                        userEntity.isEmailNotification = user.email_notification
                                ?: userEntity.isEmailNotification
                        userEntity.birthday = user.birthday ?: userEntity.birthday
                        userEntity.phoneNumber = user.phone_number ?: userEntity.phoneNumber
                        userEntity.avatar = user.avatar ?: userEntity.avatar
                        userEntity.title = user.title ?: userEntity.title
                        userEntity.isEnabled = user.enabled ?: userEntity.isEnabled
                        userEntity.company = user.company ?: userEntity.company
                        userEntity.countryId = user.country_id ?: userEntity.countryId
                        userEntity.googleId = user.google_id ?: userEntity.googleId
                        userEntity.facebookId = user.facebook_id ?: userEntity.facebookId
                        userEntity.status = user.status ?: userEntity.status
                        userEntity.dateCreated = user.date_created ?: userEntity.dateCreated
                        userEntity.handicap = user.handicap ?: userEntity.handicap
                        userEntity.friends = user.friends ?: userEntity.friends
                        userEntity.following = user.following ?: userEntity.following
                        realm.insertOrUpdate(userEntity)
                    } ?: realm.insertOrUpdate(UserEntity.toMap(user))
                    realm.insertOrUpdate(UserEntity.toMap(user))
                    onComplete(user)
                }
            }
        } catch (e: Exception) {
            Log.e("WOW", "erroe save profile: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    fun getProfile(): User? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(UserEntity::class.java).findFirst()?.toUser()
        }
    }

    fun saveRules(rules: List<GolfRule>, onComplete: () -> Unit) {
        try {
            val golfrules = rules.map {
                GolfRuleEntity.fromGolfRule(it)
            }
            Realm.getDefaultInstance().use { realm ->
                for (rule in golfrules) {
                    // save
                    rule?.rules?.let {
                        for (e in it) {
                            e.index = rule.index
                            e.title = rule.title
                        }
                    }
                }

                realm!!.executeTransactionAsync {
                    it.copyToRealmOrUpdate(golfrules)
                    onComplete()
                }

            }
        } catch (e: Exception) {

        }
    }

    fun searchKeyRulesGolfV2(key: String): List<GolfRule> {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(GolfRuleEntity::class.java)
                    .beginGroup()
                    .contains("title", key, Case.INSENSITIVE)
                    .or()
                    .contains("rules.title", key, Case.INSENSITIVE)
                    .endGroup()
                    .or()
                    .contains("rules.content", key, Case.INSENSITIVE)
                    .findAll().map {
                        it.toGolfRule()
                    }
        }
    }

    fun removeProfile(onComplete: () -> Unit, onError: (String?) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.executeTransaction { realm ->
                    realm.deleteAll()
                    onComplete()
                }
            }
        } catch (e: Exception) {
            onError(e.localizedMessage)
        }
    }

    fun insertOrUpdateRoundGolf(roundGolf: RoundGolf, teeType: String?, onComplete: () -> Unit, onError: (String?) -> Unit) {
        RoundGolfEntity.fromRoundGolf(roundGolf)?.let { roundGolfEntity ->
            roundGolfEntity.teeType = teeType
            Log.e("WOW","----start dataScorecard-----")
            Log.e("WOW", Gson().toJson(roundGolfEntity.course?.scorecards))
            Log.e("WOW","----end dataScorecard-----")

            try {
                Realm.getDefaultInstance().use { realmInstance ->
                    realmInstance.executeTransaction {
                        it.insertOrUpdate(roundGolfEntity)
                        onComplete()
                    }
                }
            } catch (e: Exception) {
                onError(e.localizedMessage)
            }
        }
    }

    fun updateMathForRoundGolfInLocal(roundId: String, hole: Hole, onComplete: () -> Unit, error: (String?) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.where(RoundGolfEntity::class.java).equalTo(RoundGolfEntity::id.name, roundId).findFirst()?.let { roundGolfEntity ->
                    realmInstance.where(HoleEntity::class.java).equalTo(HoleEntity::holeId.name, hole.hole_id).findFirst()?.let { holeEntity ->
                        realmInstance.executeTransaction {
                            roundGolfEntity.math = holeEntity
                            onComplete()
                        }
                    }

                }
            }
        } catch (e: Exception) {
            error(e.localizedMessage)
        }
    }

    fun changeTeeTypeForRoundGolfInLocal(roundId: String, teeType: String?, onComplete: () -> Unit, error: (String?) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.where(RoundGolfEntity::class.java).equalTo(RoundGolfEntity::id.name, roundId).findFirst()?.let { roundGolfEntity ->
                    realmInstance.executeTransaction {
                        roundGolfEntity.teeType = teeType
                        onComplete()
                    }
                }
            }
        } catch (e: Exception) {
            error(e.localizedMessage)
        }
    }

    fun getRoundGolfExit(): RoundGolf? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(RoundGolfEntity::class.java).findFirst()?.toRoundGolf()
        }
    }

    fun getRoundGolfByCourse(courseId: String): RoundGolf? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(RoundGolfEntity::class.java).equalTo(RoundGolfEntity::courseId.name, courseId).findFirst()?.toRoundGolf()
        }
    }

    fun getRoundGolfById(roundId: String): RoundGolf? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(RoundGolfEntity::class.java).equalTo(RoundGolfEntity::id.name, roundId).findFirst()?.toRoundGolf()
        }
    }

    fun deleteDataScoreGolfRound(onComplete: () -> Unit, onError: (String?) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.executeTransaction { realm ->
                    realm.where(DataScoreGolfEntity::class.java).findAll()?.deleteAllFromRealm()
                }
            }
            deleteRoundGolf({}, {})
            onComplete()
        } catch (e: Exception) {
            onError(e.localizedMessage)
        }
    }

    fun deleteRoundGolf(onComplete: () -> Unit, onError: (String?) -> Unit) {
        try {
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.executeTransaction { realm ->
                    /**
                     * delete course
                     * */
                    realm.where(ScorecardEntity::class.java).findAll()?.deleteAllFromRealm()
                    realm.where(CourseEntity::class.java).findAll()?.deleteAllFromRealm()

                    realm.where(TeeEntity::class.java).findAll()?.deleteAllFromRealm()

                    realm.where(EasyGolfLocationEntity::class.java).findAll()?.deleteAllFromRealm()
                    realm.where(GreenEntity::class.java).findAll().deleteAllFromRealm()

                    realm.where(HoleEntity::class.java).findAll()?.deleteAllFromRealm()
                    realm.where(RoundGolfEntity::class.java).findAll().deleteAllFromRealm()

                    realm.where(EasyGolfAppEntity::class.java).findAll().deleteAllFromRealm()
                }
                onComplete()
            }
        } catch (e: Exception) {
            onError(e.localizedMessage)
        }
    }

    fun insertOrUpdateDataScoreGolf(roundId: String, holeId: String, data: DataScoreGolf, onComplete: () -> Unit, onError: (String?) -> Unit) {
        try {
            val dataScoreGolfEntity = DataScoreGolfEntity.fromDataScoreGolf(data)
            dataScoreGolfEntity.roundId = roundId
            dataScoreGolfEntity.holeId = holeId
            Realm.getDefaultInstance().use { realmInstance ->
                realmInstance.where(DataScoreGolfEntity::class.java).equalTo(DataScoreGolfEntity::roundId.name, roundId).and()
                        .equalTo(DataScoreGolfEntity::holeId.name, holeId).findFirst()?.let { dataExit ->
                            dataScoreGolfEntity.id = dataExit.id
                        }
                realmInstance.executeTransaction {
                    it.copyToRealmOrUpdate(dataScoreGolfEntity, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET)
                    onComplete()
                }
            }
        } catch (e: Exception) {
            onError(e.localizedMessage)
        }
    }

    fun getDataScoreGolf(roundId: String, holeId: String): DataScoreGolf? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(DataScoreGolfEntity::class.java).equalTo(DataScoreGolfEntity::roundId.name, roundId).and()
                    .equalTo(DataScoreGolfEntity::holeId.name, holeId).findFirst()?.toDataScoreGolf()
        }
    }

    fun getAllDataScoreGolfByRound(roundId: String, onError: (String?) -> Unit): List<DataScoreGolf>? {
        try {
            Realm.getDefaultInstance().use { realm ->
                return realm.where(DataScoreGolfEntity::class.java).equalTo(DataScoreGolfEntity::roundId.name, roundId).findAll()?.map {
                    it.toDataScoreGolf()
                }
            }
        } catch (e: Exception) {
            onError(e.localizedMessage)
            return null
        }
    }

    fun getEasyGolfAppData(): EasyGolfApp? {
        try {
            Realm.getDefaultInstance().use { realm ->
                return realm.where(EasyGolfAppEntity::class.java).sort(EasyGolfAppEntity::updatedAt.name,Sort.ASCENDING).findFirst()?.toEasyGolfApp()
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun updateEasyGolfAppData(data: EasyGolfApp) {
        Realm.getDefaultInstance().use { realmInstance ->
            realmInstance.executeTransaction { realm ->
                if(data.isOnFirebase){
                    realm.where(EasyGolfAppEntity::class.java).findAll()?.deleteAllFromRealm()
                }else{
                    realm.where(EasyGolfAppEntity::class.java).equalTo(EasyGolfAppEntity::isOnFirebase.name,false).findAll()?.deleteAllFromRealm()
                }
                realm.insertOrUpdate(EasyGolfAppEntity.fromEasyGolfApp(data))
            }
        }
    }

    fun deleteEasyGolfAppDataByRoundId(roundId: String?) {
        Realm.getDefaultInstance().use { realmInstance ->
            realmInstance.executeTransaction { realm ->
                realm.where(EasyGolfAppEntity::class.java).equalTo(EasyGolfAppEntity::roundId.name,roundId).findAll()?.deleteAllFromRealm()
            }
        }
    }
}