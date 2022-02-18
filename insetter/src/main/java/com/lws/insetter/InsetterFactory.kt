package com.lws.insetter

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lws.insetter.Insetter.TAG
import java.lang.reflect.Constructor
import kotlin.collections.set

class InsetterFactory : LayoutInflater.Factory2 {

    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sConstructorMap: MutableMap<String, Constructor<out View>> = mutableMapOf()

    var factory2: LayoutInflater.Factory2? = null
    var factory: LayoutInflater.Factory? = null

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        val view = factory2?.onCreateView(parent, name, context, attrs)
            ?: factory2?.onCreateView(null, name, context, attrs)
            ?: factory?.onCreateView(name, context, attrs)
            ?: createViewFromTag(context, name, attrs)


        view?.run {
            val materialInsets = view.context.obtainStyledAttributes(
                attrs,
                com.google.android.material.R.styleable.Insets
            )
            val paddingTopSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingTopSystemWindowInsets,
                    false
                )
            val paddingBottomSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingBottomSystemWindowInsets,
                    false
                )
            val paddingLeftSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingLeftSystemWindowInsets,
                    false
                )
            val paddingRightSystemWindowInsets =
                materialInsets.getBoolean(
                    com.google.android.material.R.styleable.Insets_paddingRightSystemWindowInsets,
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
                        if (paddingTopSystemWindowInsets) insets.top + initialPadding.top else initialPadding.left,
                        if (paddingRightSystemWindowInsets) insets.right + initialPadding.right else initialPadding.right,
                        if (paddingBottomSystemWindowInsets) insets.bottom + initialPadding.bottom else initialPadding.bottom,
                    )
                    insetsCompat
                }
            }

        }
        return view
    }


    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var currentName = name
        if (TextUtils.isEmpty(currentName)) {
            return null
        }
        if (currentName == "view") {
            currentName = attrs.getAttributeValue(null, "class")
        }
        return try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == currentName.indexOf('.')) {
                var view: View? = null
                if ("View" == currentName) {
                    view = createView(context, currentName, "android.view.")
                }
                if (view == null) {
                    view = createView(context, currentName, "android.widget.")
                }
                if (view == null) {
                    view = createView(context, currentName, "android.webkit.")
                }
                view
            } else {
                createView(context, currentName, null)
            }
        } catch (e: Exception) {
            Log.w(TAG, "cannot create 【$currentName】 : ")
            null
        } finally {
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }


    @Throws(InflateException::class)
    private fun createView(context: Context, name: String, prefix: String?): View? {
        var constructor: Constructor<out View>? = sConstructorMap[name]
        return try {
            if (constructor == null) {
                val clazz = context.classLoader.loadClass(
                    if (prefix != null) prefix + name else name
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*mConstructorArgs)
        } catch (e: java.lang.Exception) {
            Log.w(TAG, "cannot create 【$name】 : ")
            null
        }
    }


}