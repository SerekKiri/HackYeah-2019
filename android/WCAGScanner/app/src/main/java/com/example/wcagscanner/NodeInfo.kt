package com.example.wcagscanner

import android.graphics.Rect
import android.util.Log
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.deque.axe.android.AxeView
import com.deque.axe.android.wrappers.AxeRect
import java.util.*

class NodeInfo(var nodeInfoCompat: AccessibilityNodeInfoCompat?) : AxeView.Builder {
    override fun contentDescription(): String? {
        return nodeInfoCompat?.contentDescription.toString()
    }

    override fun boundsInScreen(): AxeRect {
        if (this.nodeInfoCompat == null) {
            return AxeRect(0,0,0,0)
        }
        nodeInfoCompat?.run {
            val rect = Rect()
            getBoundsInScreen(rect)
            return AxeRect(rect.left, rect.right, rect.top, rect.bottom)
        }
        return AxeRect(0,0,0,0)

    }

    override fun children(): MutableList<AxeView> {
        if (nodeInfoCompat == null) {
            return mutableListOf()
        }
        val res = mutableListOf<AxeView>()
        for (i in 0 until nodeInfoCompat!!.childCount) {
            nodeInfoCompat?.getChild(i)?.run {
                res.add(AxeView(NodeInfo(this)))
            }
        }

        return res
    }

    override fun className(): String {
        return nodeInfoCompat?.className?.toString() ?: "empty classname"
    }

    override fun text(): String {
        return nodeInfoCompat?.text.toString()
    }

    override fun isAccessibilityFocusable(): Boolean {
        return nodeInfoCompat?.actionList?.contains(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_ACCESSIBILITY_FOCUS) ?: false
    }

    override fun isClickable(): Boolean {
        return nodeInfoCompat?.actionList?.contains(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK) ?: false
    }

    override fun isEnabled(): Boolean {
        return nodeInfoCompat?.isEnabled ?: false
    }

    override fun isImportantForAccessibility(): Boolean {
        return nodeInfoCompat?.isImportantForAccessibility ?: false
    }

    override fun labeledBy(): AxeView? {
        nodeInfoCompat?.labeledBy?.run {
            return AxeView(NodeInfo(this))
        }
        return null
    }

    override fun packageName(): String {
        return nodeInfoCompat?.packageName.toString()
    }
}