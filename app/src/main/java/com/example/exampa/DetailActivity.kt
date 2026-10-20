package com.example.exampa

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// 详情页:用 id 从 API 拿一部电影
class DetailActivity : AppCompatActivity() {


    private lateinit var tvTitle: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDirector: TextView
    private lateinit var tvLength: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvOverview: TextView
    private lateinit var btnBack: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 找控件

        tvTitle = findViewById(R.id.tvTitle)
        tvRating = findViewById(R.id.tvRating)
        tvDirector = findViewById(R.id.tvDirector)
        tvLength = findViewById(R.id.tvLength)
        tvDate = findViewById(R.id.tvDate)
        tvOverview = findViewById(R.id.tvOverview)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

        // Back:关掉这一页,回到列表
        btnBack.setOnClickListener { finish() }

        // 收到列表传来的 id
        val id = intent.getIntExtra("id", 0)
        loadMovie(id)
    }

    // 用 id 从 API 拿这一部电影
    private fun loadMovie(id: Int) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE // 显示转圈
            try {
                val movie = RetrofitClient.api.getMovieById(id)
                tvTitle.text = movie.title
                tvRating.text = "Rating: ${movie.rating}"
                tvDirector.text = "Director: ${movie.director}"
                tvLength.text = "Length: ${movie.length} min"
                tvDate.text = "Release: ${movie.release_date}"
                tvOverview.text = "Overview: ${movie.overview}"
            } catch (e: Exception) {
                Log.e("Detail", "错误", e)
                Toast.makeText(this@DetailActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE // 关掉转圈
            }
        }
    }
}