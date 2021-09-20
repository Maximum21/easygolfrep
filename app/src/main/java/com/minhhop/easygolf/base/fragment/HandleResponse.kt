package com.minhhop.easygolf.base.fragment

interface HandleResponse<T> {
    fun onSuccess(result: T)
}