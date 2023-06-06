package com.example.trab3

import DietListAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trab3.database.AppDatabase
import com.example.trab3.entities.EntityDiet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), DietListAdapter.OnDeleteClickListener {
    companion object {
        private const val ADD_DIET_REQUEST = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var dietListAdapter: DietListAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dietListAdapter = DietListAdapter(this, emptyList(), this)
        recyclerView.adapter = dietListAdapter

        database = AppDatabase.getDatabase(this)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivityForResult(Intent(this, DietActivity::class.java), ADD_DIET_REQUEST)
        }

        loadDiets()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_DIET_REQUEST && resultCode == Activity.RESULT_OK) {
            loadDiets()
        }
    }

    private fun loadDiets() {
        CoroutineScope(Dispatchers.IO).launch {
            val diets = database.dietDao().getAll()
            withContext(Dispatchers.Main) {
                dietListAdapter.updateDiets(diets)
            }
        }
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
