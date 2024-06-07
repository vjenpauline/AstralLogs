package com.example.myapplication

// imports all important classes
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
    // displays the list of entries
    private lateinit var recyclerViewAdapter: JournalEntriesAdapter
    private lateinit var entries: MutableList<JournalEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_of_entries)

        // gets the list of journal entries from storage
        entries = getEntriesList(this)

        // setting up the adapter that manages how the data is displayed
        recyclerViewAdapter = JournalEntriesAdapter(entries, { entryId ->
            deleteEntryFromUI(entryId) // called to delete an entry
        }, { entry ->
            // handle pressing, navigates to AddJournalEntry with the entry data so user can edit
            val editIntent = Intent(this, AddJournalEntry::class.java)
            editIntent.putExtra("entryId", entry.id)
            editIntent.putExtra("entryTitle", entry.title)
            editIntent.putExtra("entryContent", entry.content)
            startActivity(editIntent)
        }, "gallery_of_entries")

        // updating the layout type for the adapter
        recyclerViewAdapter.updateLayoutType("gallery_of_entries")

        // set the adapter to the RecyclerView, makes it scroll
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_gallery)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        // buttons to go to homepage and add journal entry
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

        // information button that opens up popup
        findViewById<Button>(R.id.infoBtn).setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.information_card)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.show()
        }
    }

    // retrieves list of entries from shared preferences.
    private fun getEntriesList(context: Context): MutableList<JournalEntry> {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val entriesJson = sharedPreferences.getString("journal_entries", "[]")
        val type = object : TypeToken<MutableList<JournalEntry>>() {}.type
        return Gson().fromJson(entriesJson, type)
    }

    // removes an entry from the UI and updates storage
    private fun deleteEntryFromUI(deletedEntryId: String) {
        // finds the entry in the list
        val entryIndex = entries.indexOfFirst { it.id == deletedEntryId }
        if (entryIndex != -1) {
            entries.removeAt(entryIndex)
            recyclerViewAdapter.notifyItemRemoved(entryIndex)
            // saves the updated list
            saveEntriesList(entries)
        }
    }

    // saves the list of entries to shared preferences
    private fun saveEntriesList(entries: MutableList<JournalEntry>) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val entriesJson = Gson().toJson(entries)
        editor.putString("journal_entries", entriesJson)
        editor.apply()
    }

    override fun onResume() {
        super.onResume() // refreshes the list of entries
        updateRecentEntries()
    }

    private fun updateRecentEntries() {
        val entries = getEntriesList(this)
        if (entries.isNotEmpty()) {
            // updates RecyclerView with new entries
            recyclerViewAdapter.updateEntries(entries)
        }
    }
}
