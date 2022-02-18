package com.lws.insetter

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat

object Insetter {

    const val TAG = "Insetter"
    fun inject(context: Context) {
        val inflater = if (context is Activity) {
            context.layoutInflater
        } else {
            LayoutInflater.from(context)
        }
        if (inflater == null) return

        Log.w(TAG, "inject: ${inflater.factory2}")

        if (inflater.factory2 == null) {
            inflater.factory2 = InsetterFactory().also {
                if (context is AppCompatActivity) {
                    val delegate = context.delegate
                    it.factory2 = object : LayoutInflater.Factory2 {
                        override fun onCreateView(
                            parent: View?,
                            name: String,
                            context: Context,
                            attrs: AttributeSet
                        ): View? {
                            return delegate.createView(parent, name, context, attrs)
                        }

                        override fun onCreateView(
                            name: String,
                            context: Context,
                            attrs: AttributeSet
                        ): View? {
                            return delegate.createView(null, name, context, attrs)
                        }

                    }
                }
            }
        } else if (inflater.factory2 !is InsetterFactory) {
            forceSetFactory2(inflater)
        }
    }

    private fun forceSetFactory2(inflater: LayoutInflater) {
        val compatClass = LayoutInflaterCompat::class.java
        val inflaterClass = LayoutInflater::class.java
        try {
            val sCheckedField = compatClass.getDeclaredField("sCheckedField")
            sCheckedField.isAccessible = true
            sCheckedField.setBoolean(compatClass, false)
            val mFactory = inflaterClass.getDeclaredField("mFactory")
            mFactory.isAccessible = true
            val mFactory2 = inflaterClass.getDeclaredField("mFactory2")
            mFactory2.isAccessible = true
            val factory = InsetterFactory()
            if (inflater.factory2 != null) {
                factory.factory2 = inflater.factory2
            } else if (inflater.factory != null) {
                factory.factory = inflater.factory
            }
            mFactory2[inflater] = factory
            mFactory[inflater] = factory

            Log.e(TAG, "forceSetFactory2: ")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }
}