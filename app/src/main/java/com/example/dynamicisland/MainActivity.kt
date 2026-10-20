package com.example.dynamicisland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF08090D)
private val Panel = Color(0xFF15171F)
private val Muted = Color(0xFF8C91A3)
private val Lime = Color(0xFFB8F36B)
private val Violet = Color(0xFFB6A0FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashFile = java.io.File(filesDir, "last_crash.txt")
        val savedCrash = runCatching {
            if (crashFile.exists()) crashFile.readText() else null
        }.getOrNull()
        setContent {
            MaterialTheme {
                var showDebugScreen by remember { mutableStateOf(!savedCrash.isNullOrBlank()) }
                if (showDebugScreen) {
                    CrashReportScreen(
                        report = savedCrash.orEmpty(),
                        onRefresh = {
                            showDebugScreen = false
                        },
                        onClear = {
                            crashFile.delete()
                            showDebugScreen = false
                        }
                    )
                } else {
                    DynamicIslandApp(
                        onOpenDebug = {
                            showDebugScreen = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CrashReportScreen(
    report: String,
    onRefresh: () -> Unit,
    onClear: () -> Unit
) {
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("DEBUG / CRASH LOG", color = Lime, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            if (report.isBlank()) "Walang naka-save na crash report. Buksan ang app at ulitin ang problemang nangyayari; awtomatikong mase-save ang uncaught crash." else "May na-save na crash report. Kopyahin ito at ipadala rito para mahanap ang eksaktong sanhi.",
            color = Color.White,
            fontSize = 14.sp
        )
        Text(
            if (report.isBlank()) "No crash log available." else report,
            color = Color(0xFFFFC6C6),
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Panel, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        )
        androidx.compose.material3.Button(
            onClick = {
                clipboard.setText(androidx.compose.ui.text.AnnotatedString(report.ifBlank { "No crash report captured yet." }))
                copied = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (copied) "COPIED — PASTE IT HERE" else "COPY CRASH REPORT")
        }
        androidx.compose.material3.OutlinedButton(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BACK TO APP")
        }
        androidx.compose.material3.OutlinedButton(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CLEAR CRASH LOG")
        }
    }
}

@Composable
private fun DynamicIslandApp(onOpenDebug: () -> Unit) {
    var expanded by remember { mutableStateOf(true) }
    var musicPlaying by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Ink
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF19182A), Ink, Ink)
                    )
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("DYNAMIC", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("ISLAND", color = Lime, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF242633))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text("DEMO 01", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(34.dp))
            Text("A little island.", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("A lot happening.", color = Muted, fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))

            // The island is a prototype UI inside this app, not a system-wide overlay yet.
            IslandPill(
                expanded = expanded,
                musicPlaying = musicPlaying,
                onClick = { expanded = !expanded },
                onMusicClick = { musicPlaying = !musicPlaying }
            )

            Spacer(Modifier.height(18.dp))
            Text(
                if (expanded) "TAP THE ISLAND TO COLLAPSE" else "TAP THE ISLAND TO EXPAND",
                color = Muted,
                fontSize = 10.sp,
                letterSpacing = 1.4.sp
            )

            Spacer(Modifier.height(34.dp))
            SectionLabel("QUICK CONTROLS")
            Spacer(Modifier.height(12.dp))
            ControlCard(
                icon = { Icon(Icons.Default.MusicNote, contentDescription = null, tint = Lime, modifier = Modifier.size(21.dp)) },
                title = "Now playing",
                subtitle = if (musicPlaying) "Midnight City · M83" else "Playback paused",
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF292D38))
                            .clickable { musicPlaying = !musicPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (musicPlaying) Icons.Default.Waves else Icons.Default.PlayArrow,
                            contentDescription = if (musicPlaying) "Pause music demo" else "Play music demo",
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            ControlCard(
                icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Violet, modifier = Modifier.size(21.dp)) },
                title = "Live notifications",
                subtitle = if (notificationsEnabled) "Show activity previews" else "Previews turned off",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Ink,
                            checkedTrackColor = Lime,
                            uncheckedThumbColor = Muted,
                            uncheckedTrackColor = Color(0xFF30323C)
                        )
                    )
                }
            )

            Spacer(Modifier.height(22.dp))
            SectionLabel("BUILT FOR ANDROID")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                FeatureChip("Compose UI", Modifier.weight(1f))
                FeatureChip("Animated", Modifier.weight(1f))
                FeatureChip("Kotlin", Modifier.weight(1f))
            }
            Spacer(Modifier.height(22.dp))
            Text(
                "Prototype stage · System overlay and real notification integration are next",
                color = Color(0xFF656A7A),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.height(14.dp))
            androidx.compose.material3.OutlinedButton(
                onClick = onOpenDebug,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("OPEN DEBUG / CRASH LOG")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun IslandPill(
    expanded: Boolean,
    musicPlaying: Boolean,
    onClick: () -> Unit,
    onMusicClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "audio pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "audio pulse scale"
    )

    Row(
        modifier = Modifier
            .width(if (expanded) 310.dp else 148.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(Color.Black)
            .clickable(onClick = onClick)
            .animateContentSize(animationSpec = tween(350, easing = FastOutSlowInEasing))
            .padding(horizontal = 17.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF202B22)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = Lime, modifier = Modifier.size(19.dp))
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Now Playing", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (musicPlaying) "Midnight City" else "Paused", color = Muted, fontSize = 10.sp)
                }
            }
        }
        Row(
            modifier = Modifier.clickable { onMusicClick() }.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height((8 + index * 4).dp)
                        .scale(if (musicPlaying) pulse else 0.55f)
                        .clip(CircleShape)
                        .background(if (musicPlaying) Lime else Muted)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        modifier = Modifier.fillMaxWidth(),
        color = Muted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp
    )
}

@Composable
private fun ControlCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Panel)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF222530)),
            contentAlignment = Alignment.Center
        ) { icon() }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        trailing()
    }
}

@Composable
private fun FeatureChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1B1D27))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color(0xFFD4D6E1), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
