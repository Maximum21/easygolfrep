package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.RoundGolf
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RoundGolfEntity : RealmObject() {
    /**
     * it is round id
     * */
    @PrimaryKey
    var id: String = ""
    var clubName: String? = null
    var courseName: String? = null
    var courseId: String? = null
    var course:CourseEntity? = null
    var date: String? = null
    var status: String? = null
    var tee: String? = null
    var unit: String? = null
    var holes: RealmList<HoleEntity>? = null
    var math: HoleEntity? = null
    var hdc: Double = 0.0
    var hcp: Double = 0.0
    var differential: Double = 0.0
    var isHost: Boolean? = false
    var stage: String? = null
    var createdAt: Long = 0L
    var teeType:String? = null

    companion object {
        fun fromRoundGolf(roundGolf: RoundGolf): RoundGolfEntity? {
            return roundGolf.id?.let { roundId ->
                val result = RoundGolfEntity()
                result.id = roundId
                result.clubName = roundGolf.club_name
                result.courseName = roundGolf.course_name
                result.courseId = roundGolf.course_id
                result.date = roundGolf.date
                result.status = roundGolf.status
                result.tee = roundGolf.tee
                result.unit = roundGolf.unit
                val holesRealmList = RealmList<HoleEntity>()
                roundGolf.holes?.map {
                    val holeEntity = HoleEntity.fromHole(roundId, it)
                    holeEntity?.roundId = roundGolf.id
                    holesRealmList.add(holeEntity)
                }
                result.holes = holesRealmList
                result.course = CourseEntity.fromCourse(roundGolf.course)
                val holeEntityMath = HoleEntity.fromHole(roundId, roundGolf.math)
                holeEntityMath?.roundId = roundGolf.id
                result.math = holeEntityMath
                result.hdc = roundGolf.hdc
                result.hcp = roundGolf.hcp
                result.differential = roundGolf.differential
                result.isHost = roundGolf.is_host
                result.stage = roundGolf.stage
                result.createdAt = Calendar.getInstance().timeInMillis
                result
            }
        }
    }

    fun toRoundGolf() = RoundGolf(
            this.id,
            this.clubName,
            this.courseName,
            this.courseId,
            this.date,
            this.status,
            this.tee,
            this.unit,
            this.course?.toCourse(),
            this.holes?.map {
                it.toHole()
            },
            this.math?.toHole(),
            this.hdc,
            this.hcp,
            this.differential,
            this.isHost,
            this.stage,
            null,
            null,
            this.teeType
    )
}
