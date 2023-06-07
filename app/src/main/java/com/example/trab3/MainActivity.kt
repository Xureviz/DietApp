package com.example.trab3

import DietListAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trab3.database.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), DietListAdapter.OnDeleteClickListener, DietListAdapter.OnItemClickListener {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dietListAdapter: DietListAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                loadDiets()
            }
        }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dietListAdapter = DietListAdapter(this, emptyList(), this, this)
        recyclerView.adapter = dietListAdapter

        database = AppDatabase.getDatabase(this)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            resultLauncher.launch(Intent(this, DietActivity::class.java))
        }

        loadDiets()
    }

    private fun loadDiets() {
        CoroutineScope(Dispatchers.IO).launch {
            val diets = database.dietDao().getAll()
            Log.d("MainActivity", "Loaded diets: $diets")
            withContext(Dispatchers.Main) {
                dietListAdapter.updateDiets(diets)
            }
        }
    }
    override fun onItemClick(position: Int) {
        val diet = dietListAdapter.diets[position]
        val intent = Intent(this, DietActivity::class.java).apply {
            putExtra("DIET_ID", diet.id)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        val diet = dietListAdapter.diets[position]
        val dietDao = database.dietDao()

        CoroutineScope(Dispatchers.IO).launch {
            dietDao.delete(diet)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Diet Deleted Successfully!", Toast.LENGTH_SHORT).show()
                loadDiets()
            }
        }
    }
}
