package com.lws.insetter

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import com.lws.insetter.Insetter.TAG
import java.lang.reflect.Constructor
import kotlin.collections.set

class InsetterFactory : LayoutInflater.Factory2 {

    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sConstructorMap: MutableMap<String, Constructor<out View>> = mutableMapOf()

    var factory2: LayoutInflater.Factory2? = null
    var factory: LayoutInflater.Factory? = null
    private val interceptors = mutableListOf<Interceptor>()
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        val createView = Interceptor {
            val (parent, name, context, attrs) = it.request()
            val view = factory2?.onCreateView(parent, name, context, attrs)
                ?: factory2?.onCreateView(null, name, context, attrs)
                ?: factory?.onCreateView(name, context, attrs)
                ?: createViewFromTag(context, name, attrs)
            Log.e(TAG, "onCreateView: $view")
            return@Interceptor view
        }

        val test = Interceptor {
            Log.d(TAG, "test: start $it")
            val view = it.proceed(it.request())
            Log.d(TAG, "test end : $it")
            return@Interceptor view
        }

        interceptors.add(test)

        interceptors.add(InsetsInterceptor())

        interceptors.add(createView)

        val request = Interceptor.Request(parent, name, context, attrs)

        val chain = RealInterceptorChain(
            interceptors = interceptors,
            index = 0,
            request = request,
        )

        val view = chain.proceed(request)

        Log.e(TAG, "onCreateView: $factory2 $view")
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
                if ("View" == currentName || "ViewStub" == currentName) {
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
            Log.w(TAG, "cannot create [$currentName]  ", e)
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
        } catch (e: Exception) {
            Log.w(TAG, "cannot create [$name]  ", e)
            null
        }
    }


}