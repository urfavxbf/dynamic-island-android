package com.example.dynamicisland

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File

/**
 * Deliberately uses native Android Views rather than Compose so this screen can
 * still be used to inspect startup failures if the Compose UI fails to render.
 */
class DebugActivity : Activity() {
    private lateinit var reportView: TextView
    private lateinit var reportFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportFile = File(filesDir, "last_crash.txt")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.rgb(8, 9, 13))
        }

        val title = TextView(this).apply {
            text = "DYNAMIC ISLAND DEBUG"
            textSize = 22f
            setTextColor(Color.rgb(184, 243, 107))
            typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(title, matchWrap())

        val intro = TextView(this).apply {
            text = "Native diagnostic screen (no Compose). Kung lumabas ito, gamitin ang mga button para siyasatin ang app."
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, dp(10), 0, dp(12))
        }
        root.addView(intro, matchWrap())

        reportView = TextView(this).apply {
            textSize = 12f
            setTextColor(Color.rgb(255, 198, 198))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(Color.rgb(21, 23, 31))
            text = readReport()
        }
        val scroll = ScrollView(this).apply { addView(reportView) }
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        addButton(root, "REFRESH CRASH LOG") { reportView.text = readReport() }
        addButton(root, "COPY CRASH LOG") {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Dynamic Island crash log", reportView.text))
            android.widget.Toast.makeText(this, "Crash log copied", android.widget.Toast.LENGTH_SHORT).show()
        }
        addButton(root, "OPEN SETTINGS SCREEN") {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        addButton(root, "OPEN MAIN DEMO") {
            startActivity(Intent(this, MainActivity::class.java))
        }
        addButton(root, "CLEAR CRASH LOG") {
            reportFile.delete()
            reportView.text = readReport()
        }

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        if (::reportView.isInitialized) reportView.text = readReport()
    }

    private fun readReport(): String = runCatching {
        if (reportFile.exists()) reportFile.readText() else
            "WALANG NA-SAVE NA CRASH LOG.\n\n1. Pindutin ang OPEN MAIN DEMO.\n2. Kung magsara ito, bumalik dito at pindutin ang REFRESH CRASH LOG.\n\nKung blanko pa rin ang screen na ito, posibleng mas maagang startup/native error ang nangyayari."
    }.getOrElse { "Hindi mabasa ang crash log: $it" }

    private fun addButton(parent: LinearLayout, label: String, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { runCatching(action).onFailure {
                android.widget.Toast.makeText(this@DebugActivity, it.toString(), android.widget.Toast.LENGTH_LONG).show()
            } }
        }
        parent.addView(button, matchWrap())
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
