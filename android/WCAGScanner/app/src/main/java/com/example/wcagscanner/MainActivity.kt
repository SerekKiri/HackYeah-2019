package com.example.wcagscanner

import android.os.Bundle
import com.deque.axe.android.AxeDevice
import android.os.Build.VERSION
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.util.DisplayMetrics
import com.deque.axe.android.Axe
import com.deque.axe.android.AxeConf
import com.deque.axe.android.AxeContext
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.util.Log
import android.R.attr.data
import android.media.projection.MediaProjection
import android.hardware.display.DisplayManager
import android.graphics.PixelFormat
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.media.ImageReader
import android.os.Handler
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deque.axe.android.constants.AxeStandard


class MainActivity : AppCompatActivity() {
    companion object {
        var projection: MediaProjection? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, FloatingHeadService::class.java)
        ContextCompat.startForegroundService(this, intent)

        setContentView(R.layout.activity_main)
        scanButon?.setOnClickListener {
            getPhoto()
        }
        getPhoto()

    }

    fun getPhoto() {
        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(projectionManager.createScreenCaptureIntent(), 2137)
    }


    private fun getAxeDevice(): AxeDevice {
        val str: String
        val displayMetrics = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
            displayMetrics
        )
        str = "Android Emulator"

        val sb = StringBuilder()
        sb.append("Android ")
        sb.append(VERSION.RELEASE)
        sb.append(" API Level ")
        sb.append(VERSION.SDK_INT)
        return AxeDevice(
            displayMetrics.density,
            str,
            sb.toString(),
            displayMetrics.heightPixels,
            displayMetrics.widthPixels
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2137) {
            val projectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            projection = projectionManager.getMediaProjection(resultCode, data!!)
        }
    }
}
