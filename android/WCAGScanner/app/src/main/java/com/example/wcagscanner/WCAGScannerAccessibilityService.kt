package com.example.wcagscanner

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.deque.axe.android.AxeEvent
import com.deque.axe.android.AxeView
import com.deque.axe.android.wrappers.AxeEventStream
import com.example.wcagscanner.AxeAccessibilityEvent
import com.example.wcagscanner.NodeInfo

class WCAGScannerAccessibilityService : AccessibilityService() {
    var eventStream = AxeEventStream()

    fun getRootView(): AxeView {
        return AxeView(NodeInfo(AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)))
    }

    companion object {
        var instance: WCAGScannerAccessibilityService? = null
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        eventStream.addEvent(AxeEvent(AxeAccessibilityEvent(event!!)))
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }
}