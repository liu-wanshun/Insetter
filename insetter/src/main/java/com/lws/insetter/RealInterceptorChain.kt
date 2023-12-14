package com.lws.insetter

import android.view.View

class RealInterceptorChain(
    private val interceptors: List<Interceptor>,
    private val index: Int,
    private val request: Interceptor.Request,
) : Interceptor.Chain {

    private fun copy(
        index: Int = this.index,
        request: Interceptor.Request = this.request,
    ) = RealInterceptorChain(interceptors, index, request)

    override fun request(): Interceptor.Request {
        return request
    }

    override fun proceed(request: Interceptor.Request): View? {
        // Call the next interceptor in the chain.
        val next = copy(index = index + 1, request = request)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)
    }

}