package com.deque.axe.android.rules.hierarchy

import android.util.Log

import com.deque.axe.android.AxeRuleViewHierarchy
import com.deque.axe.android.AxeView
import com.deque.axe.android.constants.AndroidClassNames
import com.deque.axe.android.constants.AxeImpact
import com.deque.axe.android.constants.AxeStandard
import com.deque.axe.android.constants.AxeStatus
import com.deque.axe.android.utils.AxeTextUtils
import com.deque.axe.android.wrappers.AxeProps
import com.deque.axe.android.wrappers.AxeProps.Name

class ImageViewName : AxeRuleViewHierarchy(
    AxeStandard.WCAG_21,
    AxeImpact.CRITICAL,
    "1.1.1: Non Text Content (Missing content description)"
) {

    override fun collectProps(axeView: AxeView, axeProps: AxeProps) {
        axeProps[Name.CONTENT_DESCRIPTION] = axeView.contentDescription
        axeProps[Name.CLASS_NAME] = axeView.className
        axeProps[Name.IMPORTANT] = axeView.isImportantForAccessibility
    }

    override fun isApplicable(axeProps: AxeProps): Boolean {

        val className = axeProps.get(Name.CLASS_NAME, String::class.java)

        return AxeView.classNameIsOfType(className, AndroidClassNames.IMAGE_VIEW)
    }

    override fun runRule(axeProps: AxeProps): String {

        val isImportantForAccessibility = axeProps.get(Name.IMPORTANT, Boolean::class.java)
        val contentDescription = axeProps.get(Name.CONTENT_DESCRIPTION, String::class.java)
        Log.v("ASD", contentDescription!!)

        if (contentDescription == null || contentDescription.contains("null") || AxeTextUtils.isNullOrEmpty(
                contentDescription
            )
        ) {
            Log.v("ASD", "FAILL")
            return AxeStatus.FAIL
        } else {
            return AxeStatus.PASS
        }
    }
}
