package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddJournalEntry : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_journal_entries)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val entryId = intent.getStringExtra("entryId")
        val entryTitle = intent.getStringExtra("entryTitle")
        val entryContent = intent.getStringExtra("entryContent")

        val entryDateTextView: TextView = findViewById(R.id.entry_date)
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        entryDateTextView.text = formattedDate

        val entryHeader = findViewById<EditText>(R.id.entry_header)
        entryHeader.apply {
            maxLines = 1
            setHorizontallyScrolling(true)
        }

        val entryText = findViewById<EditText>(R.id.entry_text)
        entryText.apply {
            maxLines = 26
            movementMethod = ScrollingMovementMethod()
            setScroller(Scroller(this@AddJournalEntry))
        }

        // Load existing tags if editing an entry
        val tagContainer = findViewById<LinearLayout>(R.id.tag_container)
        val existingTags = entryId?.let { loadTagsForEntry(it) } ?: mutableListOf()
        existingTags.forEach { tag ->
            addTagToLayout(tag, tagContainer)
        }

        if (entryId != null && entryTitle != null && entryContent != null) {
            entryHeader.setText(entryTitle)
            entryText.setText(entryContent)
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val addTagBtn = findViewById<Button>(R.id.entry_addTagBtn)
        addTagBtn.setOnClickListener { v ->
            showAddTagDialog(v, tagContainer)
        }

        val saveBtn = findViewById<Button>(R.id.entry_saveBtn)
        saveBtn.setOnClickListener {
            val id = entryId ?: generateUniqueId()
            val header = entryHeader.text.toString()
            val text = entryText.text.toString()
            val tags = (0 until tagContainer.childCount).map { index ->
                (tagContainer.getChildAt(index) as TextView).text.toString()
            }

            val newEntry = JournalEntry(id, header, text, tags.toMutableList())
            saveEntry(newEntry)
            finish()
        }
    }

    private fun showAddTagDialog(view: View, tagContainer: LinearLayout) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Enter New Tag")

        val input = EditText(view.context)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val tagText = input.text.toString()
            addTagToLayout(tagText, tagContainer)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addTagToLayout(tagText: String, tagContainer: LinearLayout) {
        val newTag = TextView(this)
        newTag.apply {
            id = View.generateViewId()
            text = tagText
            background = ContextCompat.getDrawable(this@AddJournalEntry, R.drawable.tag_bg)
            setPadding(40, 8, 40, 8)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            typeface = ResourcesCompat.getFont(this@AddJournalEntry, R.font.dmsans_italic)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 0, 10, 0)
            }
            // Add a click listener to the new tag for deletion
            setOnClickListener { v ->
                val deleteBuilder = AlertDialog.Builder(v.context)
                deleteBuilder.setTitle("Delete Tag")
                deleteBuilder.setMessage("Are you sure you want to delete this tag?")
                deleteBuilder.setPositiveButton("OK") { dialog, _ ->
                    tagContainer.removeView(v)
                    // Update the list of tags in SharedPreferences
                    val updatedTags = getTagsFromLayout(tagContainer)
                    val entryId = intent.getStringExtra("entryId") ?: generateUniqueId()
                    saveTagsForEntry(entryId, updatedTags)
                    dialog.dismiss()
                }
                deleteBuilder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                deleteBuilder.show()
            }
        }
        tagContainer.addView(newTag)
    }

    private fun getTagsFromLayout(tagContainer: LinearLayout): MutableList<String> {
        return (0 until tagContainer.childCount).map { index ->
            (tagContainer.getChildAt(index) as TextView).text.toString()
        }.toMutableList()
    }

    private fun loadTagsForEntry(entryId: String): MutableList<String> {
        val tagsJson = sharedPreferences.getString("tags_$entryId", "[]")
        val type = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson(tagsJson, type)
    }

    private fun saveEntry(entry: JournalEntry) {
        val entries = getEntriesList().apply {
            removeAll { it.id == entry.id }
            add(0, entry)
        }
        saveEntriesList(entries)
        entry.tags.let { saveTagsForEntry(entry.id, it) }
    }

    private fun getEntriesList(): MutableList<JournalEntry> {
        val entriesJson = sharedPreferences.getString("journal_entries", "[]")
        val type = object : TypeToken<MutableList<JournalEntry>>() {}.type
        return Gson().fromJson(entriesJson, type)
    }

    private fun saveEntriesList(entries: MutableList<JournalEntry>) {
        val entriesJson = Gson().toJson(entries)
        editor.putString("journal_entries", entriesJson)
        editor.apply()
    }

    private fun saveTagsForEntry(entryId: String, tags: List<String>) {
        val tagsJson = Gson().toJson(tags)
        editor.putString("tags_$entryId", tagsJson)
        editor.apply()
    }
}