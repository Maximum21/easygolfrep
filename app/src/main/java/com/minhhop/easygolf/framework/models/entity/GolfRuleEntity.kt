package com.minhhop.easygolf.framework.models.entity
import com.minhhop.core.domain.golf.Course
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.core.domain.rule.GolfRuleItem
import com.minhhop.easygolf.framework.models.RuleGolfItem
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GolfRuleEntity : RealmObject() {
    var index = 0

    @PrimaryKey
    var id =0

    var rules: RealmList<GolfRuleItemEntity>? = null

    var title: String? = null

    var content: String? = null

    var slug: String? = null

    companion object{
        fun fromGolfRule(rule: GolfRule?): GolfRuleEntity?{
            return rule?.let {
                val result = GolfRuleEntity()
                result.id = it.id
                result.index = it.index
                result.slug = it.slug
                result.content = it.content
                result.title = it.title
                if(!it.rules.isNullOrEmpty()){
                    val listRulesEntity = RealmList<GolfRuleItemEntity>()
                    it.rules?.forEach { item->
                        listRulesEntity.add(GolfRuleItemEntity.fromGolfRuleItem(item))
                    }
                    result.rules = listRulesEntity
                }
                return result
            }
        }
    }

    fun toGolfRule() = GolfRule(
            this.id,
            this.index,
            this.rules?.map{
                it.toGolfRuleItem()
            },
            this.title,
            this.content,
            this.slug
    )
}