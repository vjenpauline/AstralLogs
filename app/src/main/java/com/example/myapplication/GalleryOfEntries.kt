package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GalleryOfEntries : AppCompatActivity() {
    private lateinit var recyclerViewAdapter: JournalEntriesAdapter
    private lateinit var entries: MutableList<JournalEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_of_entries)

        // Fetch the current list of entries
        entries = getEntriesList(this)

        // Initialize the adapter with the current list of entries and a lambda to handle deletion
        recyclerViewAdapter = JournalEntriesAdapter(entries, { entryId ->
            deleteEntryFromUI(entryId)
        }, { entry ->
            // Handle item click, navigate to AddJournalEntry with the entry data
            val editIntent = Intent(this, AddJournalEntry::class.java)
            editIntent.putExtra("entryId", entry.id)
            editIntent.putExtra("entryTitle", entry.title)
            editIntent.putExtra("entryContent", entry.content)
            // ... pass other properties as needed ...
            startActivity(editIntent)
        }, "gallery_of_entries")

        recyclerViewAdapter.updateLayoutType("gallery_of_entries")

        // Set the adapter to the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_gallery) // Make sure you have a RecyclerView with this ID in your layout
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        findViewById<ImageButton>(R.id.homeBtn).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        findViewById<ImageButton>(R.id.addBtn).setOnClickListener {
            startActivity(Intent(this, AddJournalEntry::class.java))
        }

        findViewById<ImageButton>(R.id.indexBtn).apply {
            alpha = 0.2f

            setOnClickListener {
                Toast.makeText(this@GalleryOfEntries, "You're already on this page!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.infoBtn).setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.information_card)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.show()
        }
    }

    private fun getEntriesList(context: Context): MutableList<JournalEntry> {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val entriesJson = sharedPreferences.getString("journal_entries", "[]")
        val type = object : TypeToken<MutableList<JournalEntry>>() {}.type
        return Gson().fromJson(entriesJson, type)
    }

    private fun deleteEntryFromUI(deletedEntryId: String) {
        // Find the entry in the list and remove it
        val entryIndex = entries.indexOfFirst { it.id == deletedEntryId }
        if (entryIndex != -1) {
            entries.removeAt(entryIndex)
            recyclerViewAdapter.notifyItemRemoved(entryIndex)
            // Save the updated list to SharedPreferences
            saveEntriesList(entries)
        }
    }

    private fun saveEntriesList(entries: MutableList<JournalEntry>) {
        // Serialize and save the entries list to SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val entriesJson = Gson().toJson(entries)
        editor.putString("journal_entries", entriesJson)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        updateRecentEntries()
    }

    private fun updateRecentEntries() {
        val entries = getEntriesList(this)
        if (entries.isNotEmpty()) {
            // Update your RecyclerView with the new entries
            recyclerViewAdapter.updateEntries(entries)
        }
    }
}
