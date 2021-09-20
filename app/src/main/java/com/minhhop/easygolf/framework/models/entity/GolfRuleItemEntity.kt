package com.minhhop.easygolf.framework.models.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.core.domain.rule.GolfRuleItem
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GolfRuleItemEntity : RealmObject() {

    var index = 0
    var id = 0
    var title: String? = null
    var content: String? = null
    var slug: String? = null

    companion object{
        fun fromGolfRuleItem(scorecard: GolfRuleItem):GolfRuleItemEntity{
            val result = GolfRuleItemEntity()
            result.index = scorecard.index
            result.id = scorecard.id
            result.title = scorecard.title
            result.content = scorecard.content
            result.slug = scorecard.slug
            return result
        }
    }

    fun toGolfRuleItem() = GolfRuleItem(
            this.index,
            this.id,
            this.title,
            this.content,
            this.slug
    )
}