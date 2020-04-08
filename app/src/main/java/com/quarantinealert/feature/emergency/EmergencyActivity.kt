package com.quarantinealert.feature.emergency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quarantinealert.R
import kotlinx.android.synthetic.main.activity_emergency.*

class EmergencyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)
        setOnClick()
    }

    private fun setOnClick() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
