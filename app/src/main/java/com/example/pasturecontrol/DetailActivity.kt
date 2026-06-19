package com.example.pasturecontrol

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etArea: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var tvPhotoPath: TextView
    private lateinit var tvVideoPath: TextView
    private lateinit var btnSave: Button

    private var currentPasture: Pasture? = null
    private var selectedPhotoUri: String? = null
    private var selectedVideoUri: String? = null
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val photoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it.toString()
            tvPhotoPath.text = it.toString()
        }
    }

    private val videoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedVideoUri = it.toString()
            tvVideoPath.text = it.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        etName = findViewById(R.id.etName)
        etArea = findViewById(R.id.etArea)
        etDate = findViewById(R.id.etDate)
        tvPhotoPath = findViewById(R.id.tvPhotoPath)
        tvVideoPath = findViewById(R.id.tvVideoPath)
        btnSave = findViewById(R.id.btnSave)

        currentPasture = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("pasture", Pasture::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("pasture") as? Pasture
        }

        currentPasture?.let {
            etName.setText(it.name)
            etArea.setText(it.area.toString())
            etDate.setText(it.creationDate)
            selectedPhotoUri = it.photoUri
            tvPhotoPath.text = it.photoUri ?: "No se ha seleccionado foto"
            selectedVideoUri = it.videoUri
            tvVideoPath.text = it.videoUri ?: "No se ha seleccionado video"
            btnSave.text = "Actualizar"
            runCatching {
                dateFormatter.parse(it.creationDate)?.let { date -> calendar.time = date }
            }
        } ?: run {
            etDate.setText(dateFormatter.format(calendar.time))
        }

        etDate.setOnClickListener { showDatePicker() }
        findViewById<Button>(R.id.btnSelectPhoto).setOnClickListener { photoPicker.launch("image/*") }
        findViewById<Button>(R.id.btnSelectVideo).setOnClickListener { videoPicker.launch("video/*") }

        btnSave.setOnClickListener { savePasture() }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                etDate.setText(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun savePasture() {
        val name = etName.text.toString()
        val areaStr = etArea.text.toString()
        val date = etDate.text.toString()

        if (name.isEmpty() || areaStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val area = areaStr.toDoubleOrNull() ?: 0.0
        val database = AppDatabase.getDatabase(this)

        val pasture = Pasture(
            id = currentPasture?.id ?: 0,
            name = name,
            area = area,
            creationDate = date,
            photoUri = selectedPhotoUri,
            videoUri = selectedVideoUri
        )

        lifecycleScope.launch {
            if (currentPasture == null) {
                database.pastureDao().insert(pasture)
            } else {
                database.pastureDao().update(pasture)
            }
            finish()
        }
    }
}
