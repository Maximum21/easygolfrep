package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TournamentPaging (

    @SerializedName("paginator")
    @Expose
    var paginator: Paginator,

    @SerializedName("items")
    @Expose
    val items:List<Tournament>
)