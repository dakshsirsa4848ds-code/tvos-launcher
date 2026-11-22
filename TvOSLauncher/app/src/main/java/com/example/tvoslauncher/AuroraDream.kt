package com.example.tvoslauncher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.service.dreams.DreamService
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/** Screensaver: the same slow aurora as the home screen, with a drifting clock. */
class AuroraDream : DreamService() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        setContentView(AuroraView(this))
    }
}

private class AuroraView(context: Context) : View(context) {

    private val colors = intArrayOf(0xFF7C5CFF.toInt(), 0xFF3B2A8F.toInt(), 0xFF0A7E8C.toInt())
    private val blobPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xB3FFFFFF.toInt()
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    private val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
    private val start = System.nanoTime()

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val t = (System.nanoTime() - start) / 1_000_000_000.0 / 90.0 * 2 * Math.PI

        canvas.drawColor(0xFF05050A.toInt())

        val radius = w * 0.5f
        colors.forEachIndexed { i, c ->
            val a = t + i * 2.1
            val cx = (w * (0.5 + 0.38 * cos(a * (1 + i * 0.25)))).toFloat()
            val cy = (h * (0.5 + 0.32 * sin(a * (1 + i * 0.15)))).toFloat()
            val faded = (c and 0x00FFFFFF) or (0x8C shl 24)
            blobPaint.shader = RadialGradient(
                cx, cy, radius, faded, c and 0x00FFFFFF, Shader.TileMode.CLAMP,
            )
            canvas.drawCircle(cx, cy, radius, blobPaint)
        }

        // Clock drifts slowly so no pixel stays lit in one place (burn-in).
        val now = Date()
        val x = (w * (0.5 + 0.12 * sin(t * 3))).toFloat()
        val y = (h * (0.5 + 0.10 * cos(t * 2))).toFloat()
        timePaint.textSize = h * 0.2f
        datePaint.textSize = h * 0.04f
        canvas.drawText(timeFormat.format(now), x, y, timePaint)
        canvas.drawText(dateFormat.format(now), x, y + h * 0.07f, datePaint)

        postInvalidateOnAnimation()
    }
}
