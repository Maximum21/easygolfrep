package com.minhhop.easygolf.presentation.golf.component.tee

import com.minhhop.core.domain.golf.Tee
interface OnTeeListener {
    fun onClickTee(tee: Tee?, position: Int)
}