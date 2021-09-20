package com.minhhop.easygolf.services

import android.util.Log
import com.google.gson.Gson
import com.minhhop.core.domain.Country
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import com.minhhop.easygolf.framework.models.entity.CountryEntity
import com.minhhop.easygolf.framework.models.entity.DataRoundGolfEntity
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.listeners.OnComplete
import com.minhhop.easygolf.utils.AppUtil
import io.realm.Case
import io.realm.Realm
import io.realm.Sort

@Deprecated("remove soon")
class DatabaseService {

    private var mRealm: Realm? = null

    companion object {
        private var instance: DatabaseService? = null

        fun getInstance(): DatabaseService {
            if (instance == null) {
                instance = DatabaseService()
            }
            return instance!!
        }
    }

    val countryEntities: List<CountryEntity>
        get() {
            initRealm()
            return mRealm!!.where(CountryEntity::class.java).findAll()
        }


    fun defaultCountry(): Country? {
        return RealmManager().getDefaultCountry(null)
    }

    val rulesGolf: List<RuleGolf>
        get() {
            initRealm()
            return mRealm!!.where(RuleGolf::class.java).findAll()
        }


    val currentUserEntity: UserEntity?
        get() {
            initRealm()
            return mRealm!!.where(UserEntity::class.java).findFirst()
        }

    /**
     * record contact
     * */
    fun getLastTimeRecordContact():Long{
        initRealm()
        return mRealm!!.where(RecordContact::class.java).findFirst()?.lastTimeUpdate?.toLong()?: 0L
    }


    private fun removeLastTimeRecordContact(onComplete: ()->Unit){
        initRealm()
        mRealm!!.executeTransactionAsync(Realm.Transaction { realm ->
            realm.where(RecordContact::class.java).findAll().deleteAllFromRealm()
        },Realm.Transaction.OnSuccess {
            onComplete()
        })

    }


    fun addCountRecordContact(newLastTime:String){
        initRealm()
        mRealm!!.executeTransactionAsync { realm ->
            val exitData = realm.where(RecordContact::class.java).findFirst()
            if (exitData != null) {
                exitData.lastTimeUpdate = newLastTime
                realm.copyToRealmOrUpdate(exitData)
            } else {
                val temp = RecordContact()
                temp.lastTimeUpdate = newLastTime
                realm.copyToRealmOrUpdate(temp)
            }
        }
    }


    fun getCountriesByKey(key: String): List<CountryEntity> {
        initRealm()
        return mRealm!!.where(CountryEntity::class.java).contains("name", key, Case.INSENSITIVE).findAll()
    }




    fun saveRulesGolf(ruleGolfs: List<RuleGolf>, onComplete: OnComplete) {
        initRealm()
        for (rule in ruleGolfs) {
            // save
            for (e in rule.rules) {
                e.indexParent = rule.index
                e.titleParent = rule.title
            }
        }
        mRealm!!.executeTransactionAsync(
                Realm.Transaction { realm -> realm.copyToRealmOrUpdate(ruleGolfs) },
                Realm.Transaction.OnSuccess { onComplete.complete() })
    }


    fun searchKeyRulesGolf(key: String): List<RuleGolfItem> {
        initRealm()
        return mRealm!!.where(RuleGolfItem::class.java)
                .contains("title", key, Case.INSENSITIVE)
                .or()
                .contains("content", key, Case.INSENSITIVE)
                .findAll()
    }

    fun searchKeyRulesGolfV2(key: String): List<RuleGolf> {
        initRealm()
        return mRealm!!.where(RuleGolf::class.java)
                .beginGroup()
                .contains("title", key, Case.INSENSITIVE)
                .or()
                .contains("rules.title", key, Case.INSENSITIVE)
                .endGroup()
                .or()
                .contains("rules.content", key, Case.INSENSITIVE)
                .findAll()
    }

    private fun removeCurrentUser(onComplete: OnComplete) {
        initRealm()
        mRealm!!.executeTransactionAsync { realm ->
            realm.where(UserEntity::class.java).findAll().deleteAllFromRealm()
            onComplete.complete()
        }
    }

    fun saveUser(userEntity: UserEntity, onComplete: OnComplete) {
        initRealm()
        mRealm!!.executeTransactionAsync(
                Realm.Transaction { realm -> realm.copyToRealmOrUpdate(userEntity) },
                Realm.Transaction.OnSuccess { onComplete.complete() })
    }

    fun saveToken(token: String): Boolean {
        return PreferenceService.getInstance().setToken(token)
    }

    fun removeToken(onComplete: ()->Unit) {
        removeCurrentUser(OnComplete {
            PreferenceService.getInstance().removeToken()
        })

        removeLastTimeRecordContact(onComplete)
    }

    private fun initRealm() {
        mRealm = Realm.getDefaultInstance()
    }


    /**
     * save math when start golf play
     */
    fun saveDataRoundGolf(dataRoundGolfEntity: DataRoundGolfEntity, onComplete: OnComplete) {
        initRealm()
        val valueRoundMatch = dataRoundGolfEntity.roundMatch
        valueRoundMatch?.apply {
            this.idClub = dataRoundGolfEntity.id
            this.course.idClub = dataRoundGolfEntity.id

            for (item in this.holes) {
                item.greens?.let {myGreen->
                    myGreen.idClub = dataRoundGolfEntity.id

                    myGreen.red.idClub = dataRoundGolfEntity.id
                    myGreen.blue.idClub = dataRoundGolfEntity.id
                    myGreen.white.idClub = dataRoundGolfEntity.id
                }
                item.idClub = dataRoundGolfEntity.id

                for (itemTee in item.tees) {
                    itemTee.idClub = dataRoundGolfEntity.id
                }
            }

            val gson = Gson()
            Log.e("WOW",gson.toJson(dataRoundGolfEntity))


            mRealm!!.executeTransactionAsync(Realm.Transaction { realm -> realm.copyToRealmOrUpdate(dataRoundGolfEntity) }
                    , Realm.Transaction.OnSuccess { onComplete.complete() })
        }
    }

    fun updateIndexHoleOfRoundGolf(clubId: String, index:Int) {
        initRealm()
        Log.e("WOW","id round: $clubId")
        mRealm!!.executeTransactionAsync {realm ->
        val exitData = realm.where(TrackingHole::class.java).equalTo("clubId", clubId)
                    .findFirst()
            if(exitData != null){
                exitData.numberHole = index
                realm.copyToRealmOrUpdate(exitData)
            }else{
                val temp = TrackingHole()
                temp.clubId = clubId
                temp.numberHole = index
                realm.copyToRealmOrUpdate(temp)
            }
        }
    }

    fun getNumberTrackingHoleGolf(clubId: String): Int {
        initRealm()
        Log.e("WOW","get id round: $clubId")
        val it = mRealm!!.where(TrackingHole::class.java).equalTo("clubId", clubId)
                .findFirst()
        return it?.numberHole?.minus(1) ?: 0
    }

    fun getDataRoundGolf(id: String): DataRoundGolfEntity? {
        initRealm()
        return mRealm!!.where(DataRoundGolfEntity::class.java).equalTo("id", id).findFirst()
    }

    fun getDataPlayGolf(idClub: String): List<DataPlayGolf> {
        initRealm()
        return mRealm!!.where(DataPlayGolf::class.java).equalTo("idClub", idClub)
                .and()
                .notEqualTo("holeId", "")
                .sort("numberHole", Sort.ASCENDING)
                .findAll()
    }

    fun findDataPlayGolfNoHaveScore(idClub: String): DataPlayGolf? {
        initRealm()
        return mRealm!!.where(DataPlayGolf::class.java).equalTo("idClub", idClub)
                .and()
                .equalTo("mValueScore",0L)
                .notEqualTo("holeId", "")
                .sort("numberHole", Sort.ASCENDING)
                .findFirst()
    }

    fun updateScoreDataRoundGolf(numberHole: Int, dataScores: DataPlayGolf, onComplete: OnComplete) {
        initRealm()
        val exitData = mRealm!!.where(DataPlayGolf::class.java).equalTo("idClub", dataScores.idClub)
                .and().equalTo("numberHole", numberHole)
                .findFirst()


        exitData?.apply {
            dataScores.distance = distance
            dataScores.index = index
            dataScores.par = par
            dataScores.id = id
            dataScores.holeId = holeId
            dataScores.flagLatitude = flagLatitude
            dataScores.flagLongitude = flagLongitude
            dataScores.teeId = teeId
            dataScores.numberHole = numberHole
        }

        if (exitData == null) {
            dataScores.id = AppUtil.getRandomString(100)
            dataScores.numberHole = numberHole
        }

        mRealm!!.executeTransactionAsync(Realm.Transaction { realm -> realm.copyToRealmOrUpdate(dataScores) },
                Realm.Transaction.OnSuccess { onComplete.complete() })
    }

    fun removeDataRoundGolf(idClub: String , onComplete: OnComplete,removeTracking:Boolean = true) {
        initRealm()
        mRealm!!.executeTransactionAsync(Realm.Transaction { realm ->
            realm.where(HolderGreen::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(Green::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(Tee::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(Hole::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(PointMap::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(Match::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(Course::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(DataPlayGolf::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            realm.where(DataRoundGolfEntity::class.java).equalTo("id", idClub).findAll().deleteAllFromRealm()
            realm.where(RoundMatch::class.java).equalTo("idClub", idClub).findAll().deleteAllFromRealm()
            if(removeTracking) {
                realm.where(TrackingHole::class.java).equalTo("clubId", idClub).findAll().deleteAllFromRealm()
            }
        }, Realm.Transaction.OnSuccess { onComplete.complete() })
    }


}