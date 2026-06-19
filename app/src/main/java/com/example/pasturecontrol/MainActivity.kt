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
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PastureAdapter
    private lateinit var database: AppDatabase
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Potreros"

        database = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PastureAdapter(
            emptyList(),
            onItemClick = { pasture ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("pasture", pasture)
                startActivity(intent)
            },
            onItemLongClick = { pasture ->
                showDeleteConfirmation(pasture)
            }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnOpenRotation)
            .setOnClickListener {
                startActivity(Intent(this, RotationActivity::class.java))
            }

        loadPastures()
    }

    override fun onResume() {
        super.onResume()
        loadPastures()
    }

    private fun loadPastures() {
        lifecycleScope.launch {
            var pastures = database.pastureDao().getAll()
            if (pastures.isEmpty()) {
                seedDatabase()
                pastures = database.pastureDao().getAll()
            }
            adapter.updateData(pastures)
        }
    }

    private suspend fun seedDatabase() {
        val date = dateFormatter.format(Date())
        val initialPastures = listOf(
            Pasture(name = "Lote Norte 1", area = 5000.0, creationDate = date),
            Pasture(name = "Lote Sur A", area = 7500.0, creationDate = date),
            Pasture(name = "Potrero Central", area = 3000.0, creationDate = date),
            Pasture(name = "Lote Este 2", area = 4500.0, creationDate = date),
            Pasture(name = "Potrero de la Quebrada", area = 10000.0, creationDate = date)
        )
        for (pasture in initialPastures) {
            database.pastureDao().insert(pasture)
        }
    }

    private fun showDeleteConfirmation(pasture: Pasture) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Potrero")
            .setMessage("¿Desea eliminar el lote ${pasture.name} y sus históricos de rotación?")
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
