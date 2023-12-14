package com.lws.insetter

import android.content.Context
import android.util.AttributeSet
import android.view.View

fun interface Interceptor {

    fun intercept(chain: Chain): View?

    interface Chain {

        fun request(): Request

        fun proceed(request: Request): View?

    }

    data class Request(
        val parent: View?,
        val name: String,
        val context: Context,
        val attrs: AttributeSet
    )
}