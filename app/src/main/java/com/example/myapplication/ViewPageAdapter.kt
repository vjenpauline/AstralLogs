package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageButton
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(
    private var title: List<String>,
    private var details: List<String>,
    private var images: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PAGE = 0
        private const val VIEW_TYPE_LAST_PAGE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == title.size - 1) VIEW_TYPE_LAST_PAGE else VIEW_TYPE_PAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PAGE) {
            Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))
        } else {
            LastPageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_page_last, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_PAGE) {
            (holder as Pager2ViewHolder).bind(title[position], details[position], images[position])
        } else {
            (holder as LastPageViewHolder).bind()
        }
    }

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.iv_title)
        val itemDetails: TextView = itemView.findViewById(R.id.iv_about)
        val itemImage: ImageView = itemView.findViewById(R.id.iv_img)

        fun bind(title: String, details: String, image: Int) {
            itemTitle.text = title
            itemDetails.text = details
            itemImage.setImageResource(image)
        }
    }

    inner class LastPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val askNameTextView: TextView = itemView.findViewById(R.id.iv_askname)
        val headingTextView: TextView = itemView.findViewById(R.id.iv_heading)
        val logoImageView: ImageView = itemView.findViewById(R.id.logo)
        val arrowButton: ImageButton = itemView.findViewById(R.id.arrow_btn)
        val inputNameEditText: EditText = itemView.findViewById(R.id.editText_input_name)

        fun bind() {
            askNameTextView.text = "What is your name?"
            headingTextView.text = "Let's start logging!"
            logoImageView.setImageResource(R.drawable.logo)
            inputNameEditText.setText("")
            arrowButton.setOnClickListener {
                val context = itemView.context
                val inputName = inputNameEditText.text.toString()
                if (inputName.isBlank()) {
                    // Show a Toast message if the EditText is empty
                    Toast.makeText(context, "Please enter your name.", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(context, HomePage::class.java).apply {
                        putExtra("user", inputName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}
