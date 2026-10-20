package com.example.dynamicisland

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat

private val Ink = Color(0xFF08090D)
private val Panel = Color(0xFF15171F)
private val Muted = Color(0xFF969BAD)
private val Lime = Color(0xFFB8F36B)
private val Violet = Color(0xFFB6A0FF)

class MainActivity : ComponentActivity() {
    private val phonePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PremiumIslandHome(
                    overlayEnabled = Settings.canDrawOverlays(this),
                    notificationsEnabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName),
                    onOverlayPermission = {
                        runCatching {
                            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                        }.onFailure { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) }
                    },
                    onNotificationPermission = { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                    onToggleOverlay = { enabled ->
                        if (enabled) {
                            if (Settings.canDrawOverlays(this)) {
                                getSharedPreferences(IslandOverlayService.PREFS, MODE_PRIVATE).edit()
                                    .putBoolean(IslandOverlayService.KEY_OVERLAY_ENABLED, true).apply()
                                startService(Intent(this, IslandOverlayService::class.java))
                            } else {
                                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                            }
                        } else {
                            getSharedPreferences(IslandOverlayService.PREFS, MODE_PRIVATE).edit()
                                .putBoolean(IslandOverlayService.KEY_OVERLAY_ENABLED, false).apply()
                            stopService(Intent(this, IslandOverlayService::class.java))
                        }
                    },
                    onToggleAccent = { violet ->
                        getSharedPreferences(IslandOverlayService.PREFS, MODE_PRIVATE).edit()
                            .putBoolean(IslandOverlayService.KEY_VIOLET_ACCENT, violet).apply()
                        if (getSharedPreferences(IslandOverlayService.PREFS, MODE_PRIVATE).getBoolean(IslandOverlayService.KEY_OVERLAY_ENABLED, false)) {
                            startService(Intent(this, IslandOverlayService::class.java))
                        }
                    },
                    onCallPermission = { phonePermission.launch(Manifest.permission.READ_PHONE_STATE) },
                    onOpenDebug = { startActivity(Intent(this, DebugActivity::class.java)) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Permission state is refreshed whenever Compose is recreated after returning from system settings.
    }
}

@Composable
private fun PremiumIslandHome(
    overlayEnabled: Boolean,
    notificationsEnabled: Boolean,
    onOverlayPermission: () -> Unit,
    onNotificationPermission: () -> Unit,
    onToggleOverlay: (Boolean) -> Unit,
    onToggleAccent: (Boolean) -> Unit,
    onCallPermission: () -> Unit,
    onOpenDebug: () -> Unit
) {
    var islandOn by remember { mutableStateOf(false) }
    var violetAccent by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF1B1930), Ink, Ink)))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("DYNAMIC", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("ISLAND", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                }
                Box(Modifier.clip(CircleShape).background(Color(0xFF242633)).padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text("PREMIUM", color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(8.dp))
                Text("Your phone,\nwith a little more life.", color = Color.White, fontSize = 29.sp, lineHeight = 35.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text("A smarter island for the things happening now.", color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(22.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color.Black)
                        .padding(horizontal = 18.dp, vertical = 13.dp)
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(Modifier.size(9.dp).clip(CircleShape).background(if (islandOn) Lime else Violet))
                    Column {
                        Text(if (islandOn) "Island is ready" else "Your Dynamic Island", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Tap the toggle below to activate", color = Muted, fontSize = 10.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf(8, 14, 10, 17).forEachIndexed { i, h ->
                            Box(Modifier.size(width = 3.dp, height = h.dp).clip(CircleShape).background(if (islandOn) Lime else Color(0xFF444653)))
                        }
                    }
                }
            }

            Text("LIVE ACTIVITIES", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            FeatureRow(icon = { androidx.compose.material3.Icon(Icons.Default.NotificationsActive, null, tint = Violet) }, title = "Smart notifications", subtitle = if (notificationsEnabled) "Notification access connected" else "Connect notification access to show alerts", status = if (notificationsEnabled) "READY" else "SETUP")
            FeatureRow(icon = { androidx.compose.material3.Icon(Icons.Default.MusicNote, null, tint = Lime) }, title = "Music & media", subtitle = "Show media notifications and playback activity", status = "LIVE")
            FeatureRow(icon = { androidx.compose.material3.Icon(Icons.Default.Call, null, tint = Color(0xFFFFB4AB)) }, title = "Call status", subtitle = "Incoming and ongoing call indicators", status = "LIVE")
            FeatureRow(icon = { androidx.compose.material3.Icon(Icons.Default.BatteryChargingFull, null, tint = Color(0xFF8FD8FF)) }, title = "Charging & battery", subtitle = "Charging state and battery percentage", status = "LIVE")

            Text("PERSONALIZE", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            SettingRow(title = "Floating island", subtitle = if (overlayEnabled) "Overlay permission granted" else "Requires display-over-other-apps access", checked = islandOn, onCheckedChange = {
                islandOn = it
                onToggleOverlay(it)
            })
            SettingRow(title = "Violet accent", subtitle = "Switch between lime and soft violet", checked = violetAccent, onCheckedChange = {
                violetAccent = it
                onToggleAccent(it)
            })

            Button(
                onClick = onOverlayPermission,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)
            ) { Text(if (overlayEnabled) "MANAGE OVERLAY ACCESS" else "ENABLE OVERLAY ACCESS", fontWeight = FontWeight.Bold) }
            OutlinedButton(onClick = onNotificationPermission, modifier = Modifier.fillMaxWidth()) {
                Text(if (notificationsEnabled) "MANAGE NOTIFICATION ACCESS" else "CONNECT NOTIFICATIONS", color = Color.White)
            }
            OutlinedButton(onClick = onCallPermission, modifier = Modifier.fillMaxWidth()) {
                Text("ENABLE CALL STATUS ACCESS", color = Color.White)
            }
            OutlinedButton(onClick = onOpenDebug, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Icon(Icons.Default.Settings, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.size(8.dp))
                Text("DIAGNOSTICS & CRASH LOG", color = Color.White)
            }
            Text("Android 8.0+ · Premium Hybrid UI", color = Color(0xFF656A7A), fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun FeatureRow(icon: @Composable () -> Unit, title: String, subtitle: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Panel).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF222530)), contentAlignment = Alignment.Center) { icon() }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, fontSize = 10.sp, lineHeight = 14.sp)
        }
        Text(status, color = Lime, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Ink, checkedTrackColor = Lime, uncheckedThumbColor = Muted, uncheckedTrackColor = Color(0xFF30323C)
            )
        )
    }
}
