package com.example.wcagscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.results_screen.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap



class ResultsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.results_screen)

        val data = intent.extras?.getByteArray("image")
        val bmp = BitmapFactory.decodeByteArray(data, 0, data!!.size)
        resultsScreenshot.setImageBitmap(bmp)
    }
}