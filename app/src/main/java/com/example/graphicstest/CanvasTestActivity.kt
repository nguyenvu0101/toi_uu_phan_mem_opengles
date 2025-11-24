package com.example.graphicstest

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class CanvasTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val canvasView = CanvasRenderView(this)
        setContentView(canvasView)

        canvasView.postDelayed({ finish() }, 3000)
    }
}

class CanvasRenderView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // Anti-aliasing tốn CPU
    private val rectangles = mutableListOf<Rect>()
    private var hasMeasured = false

    data class Rect(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val color: Int,
        val rotation: Float
    )

    init {
        // ✅ TĂNG LÊN 10,000 HÌNH
        for (i in 0 until 10000) {
            val left = Random.nextFloat() * 2000
            val top = Random.nextFloat() * 3000
            val width = 10f + Random.nextFloat() * 50
            val height = 10f + Random.nextFloat() * 50

            rectangles.add(
                Rect(
                    left = left,
                    top = top,
                    right = left + width,
                    bottom = top + height,
                    color = Color.argb(
                        150 + Random.nextInt(106), // Alpha để có blend
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    ),
                    rotation = Random.nextFloat() * 360f
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startRender = if (!hasMeasured) System.nanoTime() else 0L

        // Nền gradient (tốn CPU)
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            Color.BLACK, Color.DKGRAY,
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // ✅ VẼ 10,000 HÌNH VỚI ROTATION (CỰC TỐN CPU)
        for (rect in rectangles) {
            canvas.save()

            // Xoay hình (tốn CPU)
            canvas.rotate(
                rect.rotation,
                (rect.left + rect.right) / 2,
                (rect.top + rect.bottom) / 2
            )

            paint.color = rect.color
            paint.style = Paint.Style.FILL

            // Vẽ với shadow (tốn CPU)
            paint.setShadowLayer(5f, 2f, 2f, Color.BLACK)

            canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint)

            canvas.restore()
        }

        if (!hasMeasured) {
            val renderTime = (System.nanoTime() - startRender) / 1_000_000.0

            paint.clearShadowLayer()
            paint.color = Color.WHITE
            paint.textSize = 60f
            paint.style = Paint.Style.FILL
            canvas.drawText("Canvas (CPU)", 50f, 120f, paint)
            canvas.drawText("${String.format("%.2f", renderTime)} ms", 50f, 200f, paint)

            paint.textSize = 40f
            canvas.drawText("10,000 shapes + effects", 50f, 280f, paint)

            context.getSharedPreferences("results", Context.MODE_PRIVATE)
                .edit()
                .putFloat("canvas_render_time", renderTime.toFloat())
                .apply()

            Log.d("CanvasTest", "Canvas render time: $renderTime ms")

            Toast.makeText(
                context,
                "Canvas: ${String.format("%.2f", renderTime)} ms",
                Toast.LENGTH_LONG
            ).show()

            hasMeasured = true
        }
    }
}
