package com.example.wcagscanner

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener
import androidx.core.app.NotificationCompat
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager
import android.view.LayoutInflater
import android.os.Parcelable
import android.view.WindowManager
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.app.NotificationManager
import android.app.NotificationChannel
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.deque.axe.android.Axe
import com.deque.axe.android.AxeConf
import com.deque.axe.android.AxeContext
import com.deque.axe.android.AxeDevice
import com.deque.axe.android.constants.AxeStandard
import kotlinx.android.parcel.Parcelize
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream

@Parcelize
data class BrokenRule(val message: String, val left: Int, val top: Int, val right: Int, val bottom: Int, val elementName: String, val elementId: String) : Parcelable

class FloatingHeadService : Service(), FloatingViewListener {
    private val TAG = "ChatHeadService"

    val EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area"

    private val NOTIFICATION_ID = 9083150
    lateinit var iconText: TextView
    lateinit var floatingButton: View

    private var mFloatingViewManager: FloatingViewManager? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (mFloatingViewManager != null) {
            return START_STICKY
        }

        val metrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val inflater = LayoutInflater.from(this)
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false)

        floatingButton = iconView
        iconView.setOnClickListener {
            runScan()
            Log.d(TAG, "Click head!")
        }
        iconText = iconView.findViewById<TextView>(R.id.floatingbuttonText) as TextView

        mFloatingViewManager = FloatingViewManager(this, this)
        mFloatingViewManager!!.setFixedTrashIconImage(R.drawable.ic_trash_fixed)
        mFloatingViewManager!!.setActionTrashIconImage(R.drawable.ic_trash_action)
        mFloatingViewManager!!.setSafeInsetRect(
            intent.getParcelableExtra<Parcelable>(
                EXTRA_CUTOUT_SAFE_AREA
            ) as Rect?
        )
        val options = FloatingViewManager.Options()
        options.overMargin = (16 * metrics.density).toInt()
        mFloatingViewManager!!.addViewToWindow(iconView, options)

        startForeground(NOTIFICATION_ID, createNotification(this))

        return START_REDELIVER_INTENT
    }

    fun runScan() {
        iconText.text = "Scanning..."
        val displayMetrics = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(displayMetrics)

        floatingButton.isVisible = false
        val mImageReader = ImageReader.newInstance(displayMetrics.widthPixels, displayMetrics.heightPixels, PixelFormat.RGBA_8888, 10)

        val handler = Handler()

        val flags =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC


        mImageReader.setOnImageAvailableListener({ img ->
            mImageReader.setOnImageAvailableListener(null, handler);
            Log.v("ASD", "OII")
            floatingButton.isVisible = true

            val image = mImageReader.acquireLatestImage();

            var plane = image.planes[0];
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

            var failedRes = res.axeRuleResults?.filter { it.status == "FAIL" }
            WCAGScannerAccessibilityService.instance?.eventStream?.forEach {
                Log.v("ASD", it.eventTypeName)
            }
            iconText.text = "WCAG TEST"


            var canvas = Canvas(createBitmap2)
            var rules = mutableListOf<BrokenRule>()

            failedRes?.forEach {
                ruleRes ->
                Log.v("DOOPA", ruleRes.toJson())
                val node = root?.query { it.axeViewId == ruleRes.axeViewId }?.first()
                Log.v("ASD", node?.boundsInScreen?.toJson() ?:" OOF")

                node?.boundsInScreen?.run {
                    val myPaint = Paint()
                    myPaint.style = Paint.Style.STROKE
                    myPaint.color = Color.rgb(212, 0, 0)
                    myPaint.strokeWidth = (10f)
                    canvas.drawRect(
                        Rect(
                            node!!.boundsInScreen!!.left,
                            node!!.boundsInScreen.top,
                            node!!.boundsInScreen.right!!,
                            node!!.boundsInScreen!!.bottom
                        ), myPaint
                    )

                    myPaint.style = Paint.Style.FILL
                    myPaint.color = Color.rgb(145, 145, 0)
                    myPaint.alpha = 130
                    myPaint.strokeWidth = (1f)
                    canvas.drawRect(
                        Rect(
                            node!!.boundsInScreen!!.left,
                            node!!.boundsInScreen.top,
                            node!!.boundsInScreen.right!!,
                            node!!.boundsInScreen!!.bottom
                        ), myPaint
                    )
                }
                rules.add(
                    BrokenRule(ruleRes.ruleSummary, node!!.boundsInScreen!!.left,
                        node!!.boundsInScreen.top,
                        node!!.boundsInScreen.right!!,
                        node!!.boundsInScreen!!.bottom, ruleRes.props.get("className") as String, ruleRes.axeViewId))
            }

            HtmlRaportGen.generateReport(createBitmap2, rules)
            /* do something with [realSizeBitmap] */
        }, handler)

        MainActivity.projection?.createVirtualDisplay(
            "screen-mirror", displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, 16, mImageReader.getSurface(), null,
            handler
        )

    }

    fun getAxeDevice(): AxeDevice {
        val str: String
        val displayMetrics = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
            displayMetrics
        )
        str = "Android Emulator"

        val sb = StringBuilder()
        sb.append("Android ")
        sb.append(Build.VERSION.RELEASE)
        sb.append(" API Level ")
        sb.append(Build.VERSION.SDK_INT)
        return AxeDevice(
            displayMetrics.density,
            str,
            sb.toString(),
            displayMetrics.heightPixels,
            displayMetrics.widthPixels
        )
    }

    override fun onDestroy() {
        destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onFinishFloatingView() {
        stopSelf()
        Log.d(TAG, "finish deleted")
    }

    override fun onTouchFinished(isFinishing: Boolean, x: Int, y: Int) {
        if (isFinishing) {
            Log.d(TAG, "deleted soon")
        } else {
            Log.d(TAG, "touch finished position: $x $y")
        }
    }

    private fun destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.removeAllViewToWindow()
            mFloatingViewManager = null
        }
    }

    private fun createNotification(context: Context): Notification {
        val channelId = "ID_OF_CHANNEL"
        val channelName = "example name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(defaultChannel)
        }

        val builder = NotificationCompat.Builder(
            context,
            channelId
        )
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("notification title")
        builder.setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_MIN
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE)

        return builder.build()
    }
}