package com.example.aitraininglogkotlin.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aitraininglogkotlin.Card
import com.example.aitraininglogkotlin.adapters.CardAdapter
import com.example.aitraininglogkotlin.R
import com.example.aitraininglogkotlin.cardsList
import com.example.aitraininglogkotlin.databinding.ActivityMainBinding
import com.example.aitraininglogkotlin.helpers.PermissionHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissions = PermissionHelper(this@MainActivity)

        if (!permissions.allRuntimePermissionsGranted()) {
            permissions.getRuntimePermissions()
        }

        populateCards()

        // Initiate Recycler view
        binding.rvMain.apply {
            layoutManager = GridLayoutManager(applicationContext,3)
            adapter = CardAdapter(cardsList, this@MainActivity)
        }
    }




    private fun populateCards() {
        val card1 = Card(
            R.drawable.ic_object_detection_foreground,
            "Object detection"
        )
        cardsList.add(card1)

        val card2 = Card(
            R.drawable.ic_pose_detection_foreground,
            "Pose detection"
        )
        cardsList.add(card2)
    }

}