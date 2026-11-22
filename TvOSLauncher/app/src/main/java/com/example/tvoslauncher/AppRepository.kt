package com.example.tvoslauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppItem(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap,
    val tint: Color,
    val launchIntent: Intent,
)

object AppRepository {

    fun load(context: Context): List<AppItem> {
        val pm = context.packageManager
        val seen = HashSet<String>()
        val result = ArrayList<AppItem>()

        fun collect(category: String) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(query, 0)) {
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName || !seen.add(pkg)) continue
                val intent = Intent(Intent.ACTION_MAIN)
                    .addCategory(category)
                    .setClassName(pkg, info.activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                val bmp = info.loadIcon(pm).toBitmap(256, 256, Bitmap.Config.ARGB_8888)
                result += AppItem(
                    label = info.loadLabel(pm).toString(),
                    packageName = pkg,
                    icon = bmp.asImageBitmap(),
                    tint = dominantColor(bmp),
                    launchIntent = intent,
                )
            }
        }

        collect(Intent.CATEGORY_LEANBACK_LAUNCHER)
        collect(Intent.CATEGORY_LAUNCHER)

        return result.sortedBy { it.label.lowercase() }
    }

    /** Opens the system Settings screen from the end of the row. */
    fun settingsItem(context: Context): AppItem? {
        val intent = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resolved = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            ?: return null
        val bmp = resolved.loadIcon(context.packageManager).toBitmap(256, 256, Bitmap.Config.ARGB_8888)
        return AppItem(
            label = "Settings",
            packageName = resolved.activityInfo.packageName,
            icon = bmp.asImageBitmap(),
            tint = Color(0xFF8E8E93),
            launchIntent = intent,
        )
    }

    private fun dominantColor(bmp: Bitmap): Color {
        val px = Bitmap.createScaledBitmap(bmp, 1, 1, true).getPixel(0, 0)
        return Color(px).copy(alpha = 1f)
    }
}
