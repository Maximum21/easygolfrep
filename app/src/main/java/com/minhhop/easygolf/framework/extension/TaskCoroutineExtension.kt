package com.minhhop.easygolf.framework.extension

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                        "Task $this was cancelled")
            } else {
                result
            }
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                if (isCanceled) cont.cancel() else cont.resume(result, {
                    cont.resumeWithException(it)
                })
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

suspend fun DatabaseReference.getSingleValue(): DataSnapshot {
    return suspendCancellableCoroutine { cont ->
        addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(Throwable(error.message))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                cont.resume(snapshot){
                    cont.resumeWithException(it)
                }
            }
        })
    }
}

suspend fun Query.getSingleValue(): DataSnapshot {
    return suspendCancellableCoroutine { cont ->
        addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(Throwable(error.message))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("WOW","done: $snapshot")
                cont.resume(snapshot){
                    cont.resumeWithException(it)
                }
            }
        })
    }
}