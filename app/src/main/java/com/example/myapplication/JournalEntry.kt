package com.example.myapplication

// data class that represents journal entries
data class JournalEntry(
    val id: String, // unique ID of entry
    val title: String, // title/header
    val content: String, // content/text of entry
    val tags: MutableList<String> = mutableListOf() // list of tags
)