package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.Course
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CourseEntity : RealmObject() {
    @PrimaryKey
    var id:String = ""
    var name:String? = null
    var image:String? = null
    var scorecards:RealmList<ScorecardEntity>? = null
    companion object{
        fun fromCourse(course: Course?): CourseEntity?{
            return course?.let {
                val result = CourseEntity()
                result.id = it.id
                result.name = it.name
                result.image = it.image
                val listScorecardEntity = RealmList<ScorecardEntity>()
                it.scorecard?.forEach { item->
                    listScorecardEntity.add(ScorecardEntity.fromScorecard(it.id,item))
                }
                result.scorecards = listScorecardEntity
                return result
            }
        }
    }

    fun toCourse() = Course(
            this.id,
            this.name,
            this.image,
            this.scorecards?.map {
                it.toScorecard()
            }
    )
}