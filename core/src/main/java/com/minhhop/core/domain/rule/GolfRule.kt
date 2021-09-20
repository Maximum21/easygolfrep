package com.minhhop.core.domain.rule
class GolfRule (var id :Int,
                var index :Int,
                var rules: List<GolfRuleItem>?,
                var title: String?,
                var content: String?,
                var slug: String? = null)