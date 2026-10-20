package com.example.dynamicisland

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView

/** Minimal framework-only launcher to isolate startup crashes. */
class BootActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = TextView(this).apply {
            text = "DYNAMIC ISLAND STARTUP TEST\n\nIf you can read this, Android can launch the app. The crash is in another startup component.\n\nNext: collect Logcat to identify the exact exception."
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(18, 20, 28))
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }
        setContentView(view)
    }
}
