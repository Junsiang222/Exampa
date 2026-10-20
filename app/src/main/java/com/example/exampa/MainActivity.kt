package com.example.exampa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var etSearch: EditText
    private lateinit var spinnerSort: Spinner
    private lateinit var tvEmpty: TextView

    private var movieAdapter: MovieAdapter? = null   // 保存 adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 找控件
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        etSearch = findViewById(R.id.etSearch)
        spinnerSort = findViewById(R.id.spinnerSort)
        tvEmpty = findViewById(R.id.tvEmpty)

        // 一行一格
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 搜索:文字一变就搜,然后检查有没有结果
        etSearch.addTextChangedListener { text ->
            movieAdapter?.filter(text.toString())
            showEmptyMessage()
        }

        // 排序选择栏(顺序要和 Adapter 里的数字对应)
        val options = listOf(
            "No sort", "Title A-Z", "Title Z-A"
        )
        spinnerSort.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)

        // 选了哪一个,就用哪一种方式排序
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                movieAdapter?.sort(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadMovies()
    }


    private fun showEmptyMessage() {
        // 加载中不显示提示
        if (progressBar.visibility == View.VISIBLE) return

        val count = movieAdapter?.itemCount ?: 0   // adapter 还没有,就当作 0
        if (count == 0) {
            // 搜索框没字 = 本来就没数据;有字 = 搜不到
            tvEmpty.text = if (etSearch.text.isBlank()) "No data available" else "No movie found"
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
        }
    }

    private fun loadMovies() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE // 显示转圈
            tvEmpty.visibility = View.GONE        // 先隐藏提示
            try {
                val list = RetrofitClient.api.getAllMovie()

                movieAdapter = MovieAdapter(list) { movie ->
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("id", movie.id)
                    startActivity(intent)
                }
                recyclerView.adapter = movieAdapter

                movieAdapter?.sort(spinnerSort.selectedItemPosition)
            } catch (e: Exception) {
                Log.e("Movie", "error message", e)
                Toast.makeText(this@MainActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE // 关掉转圈
                showEmptyMessage()                 // 转圈关掉后,检查要不要显示提示
            }
        }
    }
}