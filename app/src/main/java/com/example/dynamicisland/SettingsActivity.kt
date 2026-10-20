package com.example.dynamicisland

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.NotificationManagerCompat

private val Bg = Color(0xFF08090D)
private val CardBg = Color(0xFF15171F)
private val Accent = Color(0xFFB8F36B)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var refresh by remember { mutableIntStateOf(0) }
                val overlayGranted = remember(refresh) {
                    Settings.canDrawOverlays(this@SettingsActivity)
                }
                val notificationGranted = remember(refresh) {
                    NotificationManagerCompat.getEnabledListenerPackages(this@SettingsActivity)
                        .contains(packageName)
                }
                Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("DYNAMIC ISLAND", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("Setup & Settings", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("I-enable ang kinakailangang access dito. Hindi awtomatikong ibinibigay ng app ang permissions; ikaw ang mag-a-allow sa Android settings.", color = Color.LightGray, fontSize = 14.sp)
                        PermissionCard(
                            title = "Display over other apps",
                            description = "Kailangan kung gusto mong lumutang ang island sa ibabaw ng ibang apps.",
                            granted = overlayGranted,
                            actionLabel = if (overlayGranted) "OPEN OVERLAY SETTINGS" else "GRANT OVERLAY ACCESS",
                            onClick = {
                                runCatching {
                                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                                }.onFailure {
                                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                                }
                            }
                        )
                        PermissionCard(
                            title = "Notification access",
                            description = "Kailangan para mabasa ng notification listener ang notification events. I-enable ang Dynamic Island sa system list.",
                            granted = notificationGranted,
                            actionLabel = if (notificationGranted) "OPEN NOTIFICATION SETTINGS" else "GRANT NOTIFICATION ACCESS",
                            onClick = { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                        )
                        Button(
                            onClick = { refresh++ },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("REFRESH PERMISSION STATUS") }
                        OutlinedButton(
                            onClick = { startActivity(Intent(this@SettingsActivity, MainActivity::class.java)) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("OPEN MAIN APP") }
                        OutlinedButton(
                            onClick = { startActivity(Intent(this@SettingsActivity, DebugActivity::class.java)) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("OPEN DEBUG / CRASH LOG") }
                        Text("Note: permissions lang ang setup screen. Ang actual floating overlay at notification display service ay hiwalay na implementation.", color = Color(0xFF8C91A3), fontSize = 12.sp)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Permission status is refreshed when the user returns to this screen via lifecycle redraw.
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    actionLabel: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(CardBg, RoundedCornerShape(18.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(if (granted) "GRANTED" else "NOT GRANTED", color = if (granted) Accent else Color(0xFFFFB4AB), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Text(description, color = Color(0xFFB4B7C4), fontSize = 13.sp)
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(actionLabel) }
    }
}
