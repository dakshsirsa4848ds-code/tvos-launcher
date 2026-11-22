package com.example.tvoslauncher

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    apps: List<AppItem>,
    onLaunch: (AppItem) -> Unit,
) {
    // The system Home button should never close the launcher.
    BackHandler(enabled = true) {}

    var focused by remember { mutableStateOf<AppItem?>(null) }
    val current = focused ?: apps.firstOrNull()

    val tint by animateColorAsState(
        targetValue = current?.tint ?: Color(0xFF1C1C1E),
        animationSpec = tween(durationMillis = 600),
        label = "tint",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground(tint = tint)
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()

            // Hero area: shows the focused app, like the tvOS top shelf.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                if (current != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            bitmap = current.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(22.dp)),
                        )
                        Spacer(Modifier.size(24.dp))
                        Text(
                            text = current.label,
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            AppRow(
                apps = apps,
                onFocus = { focused = it },
                onLaunch = onLaunch,
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}

@Composable
private fun TopBar() {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1_000)
        }
    }
    val time = remember(now) { SimpleDateFormat("h:mm", Locale.getDefault()).format(now) }
    val date = remember(now) { SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(now) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp, vertical = 36.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = date, color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp)
        Spacer(Modifier.size(20.dp))
        Text(text = time, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AppRow(
    apps: List<AppItem>,
    onFocus: (AppItem) -> Unit,
    onLaunch: (AppItem) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 64.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        itemsIndexed(apps, key = { _, item -> item.packageName }) { _, item ->
            AppTile(item = item, onFocus = { onFocus(item) }, onClick = { onLaunch(item) })
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AppTile(
    item: AppItem,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(28.dp)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(170.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocus()
            }
            .then(
                if (isFocused) Modifier.border(3.dp, Color.White.copy(alpha = 0.9f), shape) else Modifier,
            ),
        shape = ClickableSurfaceDefaults.shape(shape = shape, focusedShape = shape),
        scale = ClickableSurfaceDefaults.scale(scale = 1f, focusedScale = 1.14f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
    ) {
        // Icon plate: tinted gradient tile, centred icon, glossy highlight on top.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(item.tint.copy(alpha = 0.60f), Color(0xFF14141A)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(104.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isFocused) 0.26f else 0.10f),
                                Color.Transparent,
                            ),
                            endY = 220f,
                        ),
                    ),
            )
        }
    }
}
