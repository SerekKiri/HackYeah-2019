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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, FloatingHeadService::class.java)
        ContextCompat.startForegroundService(this, intent)

        setContentView(R.layout.activity_main)
        scanButon?.setOnClickListener {
            getPhoto()
        }
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
            val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val mProjection = projectionManager.getMediaProjection(resultCode, data!!)


            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)


            val mImageReader = ImageReader.newInstance(displayMetrics.widthPixels, displayMetrics.heightPixels, PixelFormat.RGBA_8888, 10)


            val handler = Handler()

            val flags =
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC

            mImageReader.setOnImageAvailableListener({ img ->
                mImageReader.setOnImageAvailableListener(null, handler);

                val image = mImageReader.acquireLatestImage();

                var plane = image.getPlanes()[0];
                val createBitmap = Bitmap.createBitmap(image.getWidth() + ((plane.getRowStride() - (plane.getPixelStride() * image.getWidth())) / plane.getPixelStride()), image.getHeight(), Bitmap.Config.ARGB_8888);
                createBitmap.copyPixelsFromBuffer(plane.getBuffer());
                val cropRect = image.getCropRect();
                val createBitmap2 = Bitmap.createBitmap(createBitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
                image.close();
                val lastBitmap = AxeBitmap(createBitmap2);
                var config = AxeConf().removeStandard(AxeStandard.BEST_PRACTICE)
                    .removeStandard(AxeStandard.PLATFORM)
                var axe = Axe(config)
                var root = WCAGScannerAccessibilityService.instance?.getRootView()
                if (root == null) {
                    Log.v("DASD", "DUPSKO")
                }
                var context = AxeContext(root, getAxeDevice(), lastBitmap, WCAGScannerAccessibilityService.instance?.eventStream)

                var res = axe.run(context)
                res.axeRuleResults?.forEach {
                    if (it.status != "PASS") {
                    Log.v("asd", it.ruleSummary)
                    it.props.values.forEach { Log.v("ASD", it?.toString() ?: "") }
                    Log.v("OKE", it.status)
                    Log.v("ASD", it.axeViewId)
                    }

                }
                WCAGScannerAccessibilityService.instance?.eventStream?.forEach {
                    Log.v("ASD", it.eventTypeName)
                }
                /* do something with [realSizeBitmap] */
            }, handler)

            mProjection.createVirtualDisplay(
                "screen-mirror", displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, 16, mImageReader.getSurface(), null,
                handler
            )


        }
    }
}
