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
import android.graphics.Rect
import android.os.Parcelable
import android.view.WindowManager
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build




class FloatingHeadService : Service(), FloatingViewListener {
    private val TAG = "ChatHeadService"

    val EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area"

    private val NOTIFICATION_ID = 9083150

    private var mFloatingViewManager: FloatingViewManager? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 既にManagerが存在していたら何もしない
        if (mFloatingViewManager != null) {
            return START_STICKY
        }

        val metrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val inflater = LayoutInflater.from(this)
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false) as ImageView
        iconView.setOnClickListener {
            Log.d(TAG, "Click head!")
        }

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

        // 常駐起動
        startForeground(NOTIFICATION_ID, createNotification(this))

        return START_REDELIVER_INTENT
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