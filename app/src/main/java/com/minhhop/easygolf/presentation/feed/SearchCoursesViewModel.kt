package com.minhhop.easygolf.presentation.feed

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel

class SearchCoursesViewModel(interactors: Interactors) : EasyGolfViewModel() {

    val clubsList = MutableLiveData<List<Club>>()
}