package com.lws.insetter

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class InsetsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): View? {
        val attrs = chain.request().attrs
        val view = chain.proceed(chain.request())
        view?.run {
            val materialInsets = view.context.obtainStyledAttributes(
                attrs,
                com.google.android.material.R.styleable.Insets
            )
            val paddingLeftSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingLeftSystemWindowInsets,
                    false
                )

            val paddingTopSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingTopSystemWindowInsets,
                    false
                )
            val paddingRightSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingRightSystemWindowInsets,
                    false
                )
            val paddingBottomSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingBottomSystemWindowInsets,
                    false
                )


            materialInsets.recycle()
            if (paddingTopSystemWindowInsets || paddingBottomSystemWindowInsets || paddingLeftSystemWindowInsets || paddingRightSystemWindowInsets) {
                val initialPadding = Insets.of(
                    ViewCompat.getPaddingStart(view),
                    view.paddingTop,
                    ViewCompat.getPaddingEnd(view),
                    view.paddingBottom
                )
                ViewCompat.setOnApplyWindowInsetsListener(view) { v, insetsCompat ->
                    val insets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.updatePadding(
                        if (paddingLeftSystemWindowInsets) insets.left + initialPadding.left else initialPadding.left,
                        if (paddingTopSystemWindowInsets) insets.top + initialPadding.top else initialPadding.top,
                        if (paddingRightSystemWindowInsets) insets.right + initialPadding.right else initialPadding.right,
                        if (paddingBottomSystemWindowInsets) insets.bottom + initialPadding.bottom else initialPadding.bottom,
                    )
                    insetsCompat
                }
            }
        }
        return view
    }
}