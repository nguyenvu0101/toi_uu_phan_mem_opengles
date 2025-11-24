package com.example.graphicstest

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // ✅ THÊM NÚT BACK TRONG ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kết quả so sánh"

        val textResult = findViewById<TextView>(R.id.text_result)

        val prefs = getSharedPreferences("results", MODE_PRIVATE)

        val canvasRender = prefs.getFloat("canvas_render_time", 0f).toDouble()
        val openglRender = prefs.getFloat("opengl_render_time", 0f).toDouble()

        if (canvasRender > 0 && openglRender > 0) {
            val ratio = canvasRender / openglRender
            val faster = if (openglRender < canvasRender) "OpenGL ES" else "Canvas"

            val result = """
                ═══════════════════════════════════
                📊 KẾT QUẢ SO SÁNH RENDER TIME
                ═══════════════════════════════════
                
                Task: Vẽ 10000 hình chữ nhật ngẫu nhiên
                
                🎨 Canvas (CPU):
                   Render time: ${String.format("%.2f", canvasRender)} ms
                   
                ⚡ OpenGL ES (GPU):
                   Render time: ${String.format("%.2f", openglRender)} ms
                
                ───────────────────────────────────
                
                🏆 KẾT LUẬN:
                
                $faster nhanh hơn ${String.format("%.2f", ratio)}x
                
                ───────────────────────────────────
                
                📝 GIẢI THÍCH:
                
                • Canvas: Dùng CPU để render từng pixel
                • OpenGL ES: Dùng GPU xử lý song song
                • GPU tối ưu cho đồ họa phức tạp
                
                ═══════════════════════════════════
            """.trimIndent()

            textResult.text = result

        } else {
            textResult.text = """
                ⚠️ CHƯA ĐỦ DỮ LIỆU
                
                Vui lòng test cả 2 phương pháp:
                1. Test Canvas
                2. Test OpenGL ES
                
                Sau đó quay lại xem kết quả
            """.trimIndent()
        }
    }

    // ✅ XỬ LÝ KHI CLICK NÚT BACK
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Đóng activity và quay về MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
