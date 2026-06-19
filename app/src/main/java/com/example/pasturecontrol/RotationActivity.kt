package com.example.pasturecontrol

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RotationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RotationPastureAdapter
    private lateinit var database: AppDatabase
    private lateinit var etSelectedDate: TextInputEditText
    private lateinit var btnLoadCattle: Button

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDateMillis: Long = normalizeToStartOfDay(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)
        title = "Rotación de Potreros"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = AppDatabase.getDatabase(this)
        etSelectedDate = findViewById(R.id.etSelectedDate)
        btnLoadCattle = findViewById(R.id.btnLoadCattle)
        recyclerView = findViewById(R.id.recyclerViewRotation)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RotationPastureAdapter(
            emptyList(),
            onSelectionChanged = { selectedCount ->
                btnLoadCattle.isEnabled = selectedCount > 0
            },
            onItemLongClick = { pastureDisplay ->
                showRotationHistory(pastureDisplay)
            }
        )

        recyclerView.adapter = adapter
        etSelectedDate.setText(formatDate(selectedDateMillis))
        etSelectedDate.setOnClickListener { showDatePicker() }

        btnLoadCattle.isEnabled = false
        btnLoadCattle.setOnClickListener { loadSelectedPasturesWithCattle() }

        loadPastures()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadPastures()
    }

    private fun loadPastures() {
        lifecycleScope.launch {
            val pastures = database.pastureDao().getAll()
            val rotations = database.rotationRecordDao().getRotationsUpTo(selectedDateMillis)
            val latestRotationByPasture = rotations
                .groupBy { it.pastureId }
                .mapValues { (_, records) -> records.maxByOrNull { it.startDateMillis } }

            val displayItems = pastures.map { pasture ->
                buildPastureDisplay(
                    pasture = pasture,
                    rotation = latestRotationByPasture[pasture.id],
                    selectedDateMillis = selectedDateMillis
                )
            }
            adapter.updateData(displayItems)
        }
    }

    private fun showDatePicker() {
        calendar.timeInMillis = selectedDateMillis
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateMillis = normalizeToStartOfDay(calendar.timeInMillis)
                etSelectedDate.setText(formatDate(selectedDateMillis))
                adapter.clearSelection()
                loadPastures()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadSelectedPasturesWithCattle() {
        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Seleccione uno o más potreros", Toast.LENGTH_SHORT).show()
            return
        }

        val unavailable = selectedItems.filter { it.state != RotationState.Available }
        if (unavailable.isNotEmpty()) {
            val names = unavailable.joinToString { it.pasture.name }
            Toast.makeText(
                this,
                "Solo se pueden cargar potreros verdes: $names",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            val records = selectedItems.map {
                RotationRecord(
                    pastureId = it.pasture.id,
                    startDateMillis = selectedDateMillis,
                    createdAtMillis = System.currentTimeMillis()
                )
            }
            database.rotationRecordDao().insertAll(records)
            Toast.makeText(this@RotationActivity, "Potreros cargados con ganado", Toast.LENGTH_SHORT).show()
            adapter.clearSelection()
            loadPastures()
        }
    }

    private fun showRotationHistory(pastureDisplay: PastureDisplay) {
        lifecycleScope.launch {
            val rotations = database.rotationRecordDao().getRotationsForPasture(pastureDisplay.pasture.id)
            if (rotations.isEmpty()) {
                AlertDialog.Builder(this@RotationActivity)
                    .setTitle(pastureDisplay.pasture.name)
                    .setMessage("No hay rotaciones registradas para este potrero.")
                    .setPositiveButton("Cerrar", null)
                    .show()
                return@launch
            }

            val expandableListView = ExpandableListView(this@RotationActivity)
            expandableListView.setAdapter(HistoryExpandableAdapter(rotations))

            AlertDialog.Builder(this@RotationActivity)
                .setTitle("Historial de ${pastureDisplay.pasture.name}")
                .setView(expandableListView)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    private inner class HistoryExpandableAdapter(
        private val rotations: List<RotationRecord>
    ) : BaseExpandableListAdapter() {
        override fun getGroupCount(): Int = rotations.size
        override fun getChildrenCount(groupPosition: Int): Int = 2
        override fun getGroup(groupPosition: Int): Any = rotations[groupPosition]
        override fun getChild(groupPosition: Int, childPosition: Int): Any = childPosition
        override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
        override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
        override fun hasStableIds(): Boolean = true

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@RotationActivity)
                .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
            val tv = view.findViewById<TextView>(android.R.id.text1)
            tv.text = "Rotación iniciada ${formatDate(rotations[groupPosition].startDateMillis)}"
            return view
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@RotationActivity)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            val tv = view.findViewById<TextView>(android.R.id.text1)

            val record = rotations[groupPosition]
            if (childPosition == 0) {
                val start = record.startDateMillis
                val end = addDays(record.startDateMillis, RED_DAYS - 1)
                tv.text = "Rojo: ${formatDate(start)} - ${formatDate(end)}"
                tv.setTextColor(Color.rgb(198, 40, 40))
            } else {
                val start = addDays(record.startDateMillis, RED_DAYS)
                val end = addDays(record.startDateMillis, RED_DAYS + ORANGE_DAYS - 1)
                tv.text = "Anaranjado: ${formatDate(start)} - ${formatDate(end)}"
                tv.setTextColor(Color.rgb(239, 108, 0))
            }
            return view
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false
    }

    private fun buildPastureDisplay(
        pasture: Pasture,
        rotation: RotationRecord?,
        selectedDateMillis: Long
    ): PastureDisplay {
        if (rotation == null || rotation.startDateMillis > selectedDateMillis) {
            return PastureDisplay(pasture = pasture, state = RotationState.Available)
        }

        val elapsedDays = daysBetween(rotation.startDateMillis, selectedDateMillis)
        val state = when {
            elapsedDays < RED_DAYS -> RotationState.Occupied
            elapsedDays < RED_DAYS + ORANGE_DAYS -> RotationState.Recovering
            else -> RotationState.Available
        }

        return PastureDisplay(
            pasture = pasture,
            state = state,
            redStartMillis = rotation.startDateMillis,
            redEndMillis = addDays(rotation.startDateMillis, RED_DAYS - 1),
            orangeStartMillis = addDays(rotation.startDateMillis, RED_DAYS),
            orangeEndMillis = addDays(rotation.startDateMillis, RED_DAYS + ORANGE_DAYS - 1)
        )
    }

    private fun formatDate(millis: Long): String = dateFormatter.format(Date(millis))

    private fun addDays(millis: Long, days: Int): Long {
        val dateCalendar = Calendar.getInstance()
        dateCalendar.timeInMillis = millis
        dateCalendar.add(Calendar.DAY_OF_YEAR, days)
        return normalizeToStartOfDay(dateCalendar.timeInMillis)
    }

    private fun daysBetween(startMillis: Long, endMillis: Long): Long {
        val start = normalizeToStartOfDay(startMillis)
        val end = normalizeToStartOfDay(endMillis)
        return TimeUnit.MILLISECONDS.toDays(end - start)
    }

    private fun normalizeToStartOfDay(millis: Long): Long {
        val dateCalendar = Calendar.getInstance()
        dateCalendar.timeInMillis = millis
        dateCalendar.set(Calendar.HOUR_OF_DAY, 0)
        dateCalendar.set(Calendar.MINUTE, 0)
        dateCalendar.set(Calendar.SECOND, 0)
        dateCalendar.set(Calendar.MILLISECOND, 0)
        return dateCalendar.timeInMillis
    }

    companion object {
        private const val RED_DAYS = 5
        private const val ORANGE_DAYS = 15
    }
}
