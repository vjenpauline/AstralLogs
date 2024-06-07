package com.example.myapplication

// imports all important classes
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3

class MainActivity : AppCompatActivity() {

    // variables for viewpager (onboarding)
    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        postToList()

        // sets up viewpager for swiping
        val viewPager = findViewById<ViewPager2>(R.id.view_pager2)
        viewPager.adapter = ViewPagerAdapter(titlesList, descList, imagesList)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager)

        // button to 'fake' swipe across the pages in case people don't know to swipe
        val fakeswipe = findViewById<Button>(R.id.fakeswipe)
        fakeswipe.setOnClickListener {
            viewPager.apply {
                beginFakeDrag()
                fakeDragBy(-500F)
                endFakeDrag()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == titlesList.size - 1) {

                    // if the last page is shown, hide the fakeswipe button
                    fakeswipe.visibility = View.GONE

                } else {
                    // if else, show the fakeswipe button
                    fakeswipe.visibility = View.VISIBLE
                }
            }
        })
    }

    // function that adds data to lists
    private fun addToList(title: String, description: String, image: Int) {
        titlesList.add(title)
        descList.add(description)
        imagesList.add(image)
    }

    // information and data shown on each page
    private fun postToList() {
        addToList("Track Your Dreams", "Effortlessly record every detail of your dream to recognize the symbolisms and signs.", R.drawable.onboarding1)
        addToList("Improve Your Sleep", "Log daily and find patterns of your sleeping on a detailed calendar.", R.drawable.onboarding2)
        addToList("placeholder", "placeholder", R.drawable.onboarding2) // placeholder for the last page
    }
}