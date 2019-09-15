package com.example.wcagscanner

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.deque.axe.android.AxeEvent.Builder
import com.deque.axe.android.AxeView

class AxeAccessibilityEvent(val accessibilityEvent: AccessibilityEvent) : Builder {

    override fun eventType(): Int? {
        return accessibilityEvent.eventType
    }

    override fun packageName(): String {
        return accessibilityEvent.packageName?.toString() ?: ""
    }

    override fun className(): String {

        return accessibilityEvent?.className?.toString() ?: ""
    }
    override fun axeView(): AxeView? {
        if (this.accessibilityEvent.recordCount > 0) {
            val record = this.accessibilityEvent.getRecord(0)
            if (record.source != null) {
                return AxeView(NodeInfo(AccessibilityNodeInfoCompat.wrap(record.source)))
            }
        }
        return null
    }
}