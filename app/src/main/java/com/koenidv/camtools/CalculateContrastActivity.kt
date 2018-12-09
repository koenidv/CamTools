package com.koenidv.camtools

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.colorbox.ColorBox


class CalculateContrastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_contrast)

        val resultLayout = findViewById<LinearLayout>(R.id.resultLayout)
        var colorLayout = findViewById<LinearLayout>(R.id.colorLayout)
        var modeLayout = findViewById<LinearLayout>(R.id.modeLayout)
        var modeTextViews = findViewById<TextView>(R.id.modeTextView)

        val gradient = GradientDrawable()
        gradient.colors = intArrayOf(Color.parseColor("#3f4cff"), Color.parseColor("#ff9c3f"))
        gradient.orientation = GradientDrawable.Orientation.BOTTOM_TOP
        resultLayout.background = gradient
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ff9c3f")))

        colorLayout.setOnClickListener {
            ColorBox.showColorBox("color_picker", this)
        }

    }
}
