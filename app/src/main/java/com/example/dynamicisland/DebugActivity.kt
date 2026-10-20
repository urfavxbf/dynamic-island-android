package com.example.dynamicisland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logFile = File(filesDir, "last_crash.txt")
        setContent {
            MaterialTheme {
                var report by remember { mutableStateOf(readReport(logFile)) }
                var copied by remember { mutableStateOf(false) }
                val clipboard = LocalClipboardManager.current
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF08090D)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("DYNAMIC ISLAND DEBUG", color = Color(0xFFB8F36B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (report.isBlank()) "Walang crash log na na-save. Buksan ang main app para ulitin ang crash, pagkatapos bumalik dito." else "Nakuha ang huling uncaught crash. Pindutin ang copy at i-paste ang report sa chat.",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            report.ifBlank { "Walang crash report sa ngayon.\n\nFile: files/last_crash.txt" },
                            color = Color(0xFFFFC6C6),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.weight(1f).fillMaxWidth()
                                .background(Color(0xFF15171F), RoundedCornerShape(12.dp))
                                .padding(12.dp).verticalScroll(rememberScrollState())
                        )
                        Button(
                            onClick = {
                                clipboard.setText(AnnotatedString(report.ifBlank { "No crash report captured yet." }))
                                copied = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (copied) "COPIED — PASTE IN CHAT" else "COPY CRASH REPORT") }
                        OutlinedButton(
                            onClick = { report = readReport(logFile); copied = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("REFRESH LOG") }
                        OutlinedButton(
                            onClick = { logFile.delete(); report = ""; copied = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("CLEAR LOG") }
                    }
                }
            }
        }
    }

    private fun readReport(file: File): String =
        runCatching { if (file.exists()) file.readText() else "" }.getOrDefault("")
}
