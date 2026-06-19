package com.example.pasturecontrol

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PastureAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PastureAdapter(emptyList(), { pasture ->
            // Click to edit
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pasture", pasture)
            startActivity(intent)
        }, { pasture ->
            // Long click to delete
            showDeleteConfirmation(pasture)
        })

        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        loadPastures()
    }

    override fun onResume() {
        super.onResume()
        loadPastures()
    }

    private fun loadPastures() {
        lifecycleScope.launch {
            var list = database.pastureDao().getAll()
            if (list.isEmpty()) {
                seedDatabase()
                list = database.pastureDao().getAll()
            }
            adapter.updateData(list)
        }
    }

    private suspend fun seedDatabase() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.format(Date())
        val initialPastures = listOf(
            Pasture(name = "Lote Norte 1", area = 5000.0, creationDate = date),
            Pasture(name = "Lote Sur A", area = 7500.0, creationDate = date),
            Pasture(name = "Potrero Central", area = 3000.0, creationDate = date),
            Pasture(name = "Lote Este 2", area = 4500.0, creationDate = date),
            Pasture(name = "Potrero de la Quebrada", area = 10000.0, creationDate = date)
        )
        for (p in initialPastures) {
            database.pastureDao().insert(p)
        }
    }

    private fun showDeleteConfirmation(pasture: Pasture) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Potrero")
            .setMessage("¿Estás seguro de que deseas eliminar el lote ${pasture.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    database.pastureDao().delete(pasture)
                    loadPastures()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
