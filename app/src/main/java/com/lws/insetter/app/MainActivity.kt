package com.lws.insetter.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

//        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
//            val sysWindow = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.updatePadding(top = sysWindow.top)
//            insets
//        }

    }
}