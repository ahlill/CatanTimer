package com.example.catantimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.catantimer.databinding.ActivityMainBinding
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mLiveDataTimerViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

            binding.btnNext.setOnClickListener {
                mLiveDataTimerViewModel.updateTrigger()
                subscribe()
            }

    }

    private fun subscribe() {
        val elapsedTimer = Observer<Long?> { timeInt ->
            if (timeInt > 0) {
                val minute = floor(timeInt.toDouble() / 60).toLong()
                val second = floor(timeInt.toDouble() % 60).toLong()

                val timer = this@MainActivity.resources.getString(R.string.time, minute, second)
                binding.tvTimer.text = timer
            } else {
                val timer = this@MainActivity.resources.getString(R.string.time, 0, 0)
                binding.tvTimer.text = timer
            }
        }
            mLiveDataTimerViewModel.getElapsedTime().observe(this@MainActivity, elapsedTimer)
    }
}