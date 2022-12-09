package com.kaspersky.kaspresso.tutorial.flaky

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaspersky.kaspresso.tutorial.R
import com.kaspersky.kaspresso.tutorial.databinding.ActivityFlakyBinding
import kotlinx.coroutines.delay

class FlakyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlakyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlakyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAnimation()
    }

    private fun startAnimation() {
        lifecycleScope.launchWhenResumed {
            delay(3000)
            binding.rootScrollView.smoothScrollBy(0, 10000)
            delay(3000)
            binding.button5.text = getString(R.string.button_5_changed)
            delay(10000)
            binding.rootScrollView.smoothScrollTo(0, 0)
            delay(1000)
            binding.button1.text = getString(R.string.button_1_changed)
        }
    }
}
