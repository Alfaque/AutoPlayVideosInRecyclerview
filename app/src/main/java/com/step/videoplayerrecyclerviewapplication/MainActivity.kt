package com.step.videoplayerrecyclerviewapplication

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.exoplayer2.*
import com.step.videoplayerrecyclerviewapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), VideoAdapter.OnHolderCreated {


    lateinit var binding: ActivityMainBinding
    lateinit var adapter: VideoAdapter
    var list = ArrayList<String>()
    var listOfHolder = ArrayList<VideoAdapter.HolderItem>()

    //    var url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
//    var url = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    var url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    val TAG = MainActivity::class.java.simpleName
//    private lateinit var percentVisibilityOnScrollListener: PercentVisibilityOnScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        list.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        list.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4")
        list.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4")
        list.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4")

        adapter = VideoAdapter(this, list, this)

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter


//        val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
//        linearSnapHelper.attachToRecyclerView(binding.recyclerview)


//        percentVisibilityOnScrollListener = PercentVisibilityOnScrollListener(adapter);
//        binding.recyclerview.addOnScrollListener(percentVisibilityOnScrollListener);

    }

    override fun onHolderCreatedListener(holderItem: VideoAdapter.HolderItem) {


        if (listOfHolder.isNullOrEmpty()) {
            listOfHolder.add(holderItem)

        } else {

            var isITemFound = false

            for (index in listOfHolder.indices) {

                if (listOfHolder[index].position == holderItem.position) {
                    Log.d(TAG, "onHolderCreatedListener: ")
                    isITemFound = true
                    break
                } else {
                    isITemFound = false
                }

            }

            if (!isITemFound) {
                listOfHolder.add(holderItem)

            }


        }

        Log.d(TAG, "onHolderCreatedListener: ")


    }

    override fun onitemScrolled(position: Int) {

        playItem(position)
    }

    private fun playItem(position: Int) {

        if (!listOfHolder.isNullOrEmpty()) {

            listOfHolder.forEach {
                it.holder.pauseVideo()
            }

            listOfHolder[position].holder.playVideo()

        }

    }

    override fun onItemPlayed(position: Int) {

        playItem(position)
    }

//    override fun onResume() {
//        super.onResume()
//        // onResume, if a video view is visible, resume it
////        percentVisibilityOnScrollListener.changePlayerState(binding.recyclerview)
//    }

//    override fun onPause() {
//        super.onPause()
//       binding.recyclerview.changePlayingState(false)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.recyclerview.changePlayingState(true)
//    }
}